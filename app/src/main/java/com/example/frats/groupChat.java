package com.example.frats;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class groupChat extends AppCompatActivity {

    TextView groupName;
    EditText et;
    Button send;

    ListView chatList;
    public String groupKey = new String();
    String myPhone = new String("");

    public ArrayList<chatMessage> chatMessages = new ArrayList<>(10);

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {

        try{

            super.onCreate( savedInstanceState );
            setContentView( R.layout.group_chat_room );

            groupName = findViewById( R.id.groupName );
            et = findViewById( R.id.messageToSend );
            send = findViewById( R.id.send );
            chatList = findViewById( R.id.messageList );

            //notifyOfMessage();

            Intent intent = getIntent();
            int index = intent.getIntExtra("index", 0);

            groupKey = groups.group.get( index ).first;
            groupName.setText( groups.group.get( index ).second );

            getMyPhone();
            //initWithAnyChats();
            //getMessagesFromFirebase messagesFromFirebase = new getMessagesFromFirebase(getApplicationContext());
            //messagesFromFirebase.add();


            loadFromLocalDb();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference myRef = db.getReference("group");

            myTask task = new myTask(myRef);
            Thread t = new Thread( task );
            t.start();

            //loadFromLocalDb();


            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    String message = et.getText().toString();
                    et.setText("");
                    int hour = cal.get( Calendar.HOUR );
                    int min = cal.get( Calendar.MINUTE );
                    String time = new String(  ( (hour < 10 ) ? ("0" + hour ) : hour) +
                            " : " + ( (min < 10 ) ? ( "0" + min ) :  min) +
                            ( ( cal.get( Calendar.AM_PM ) == Calendar.AM ) ? " AM " : " PM " ) );

                    groupMsg meso = new groupMsg(myPhone, message, time);

                    myRef.child(groupKey).child("message").push().setValue( meso ).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if( task.isSuccessful() )
                            {
                                Toast.makeText(groupChat.this, "sent: " + message, Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(groupChat.this, "Failed to connect to the internet\n" +
                                        "check your internet connection \n" +
                                        "and try again ", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }
            });
        }catch( Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void getMyPhone()
    {
        user u = new user( this);
        u.open();
        myPhone = ( u.readData())[2];
        u.close();
    }


    private ArrayList<chatMessage> getFromLocalDb()
    {
        ArrayList<chatMessage> result = new ArrayList<>(10);
        //String myPhone = new String("0712696965");

        messages m = new messages( groupChat.this );
        m.open();
        ArrayList<msg> data = m.getMessagesSentTo(groupKey);
        m.close();

        //String s = "";
        for( msg message : data)
        {
            int gravity = message.sender.equals(myPhone) ? Gravity.END : Gravity.START;
            chatMessage s = new chatMessage( message.sender + "\n\n" + message.content, message.time, gravity);
            result.add( s );
        }

        return result;
    }

    private void loadFromLocalDb()
    {

        chatMessages = getFromLocalDb();

        chatMessageAdapter adapter = new chatMessageAdapter( groupChat.this, chatMessages);
        chatList.setAdapter( adapter );
    }


    private boolean contains(ArrayList<chatMessage> list, chatMessage value)
    {
        for( chatMessage listItem : list )
        {
            if( listItem.getMessage().equals( value.getMessage() ) && listItem.getTime().equals( value.getTime() ) )
                return true;
        }
        return false;
    }

    class myTask implements Runnable
    {
        DatabaseReference dbRef;
        public myTask(DatabaseReference dbRef)
        {
            this.dbRef = dbRef;
        }

        @Override
        public void run()
        {
            try{
                dbRef.child(groupKey).child("message").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        ArrayList<chatMessage> result = getMessagesFromLocalDb();

                        if( snapshot.hasChildren() )
                        {

                            for( DataSnapshot data : snapshot.getChildren() )
                            {
                                String content = new String( "" );
                                String sender = new String( "" );
                                String time = new String( "" );
                                int g ;

                                if( data.hasChild( "content" ) )
                                {
                                    content = data.child( "content" ).getValue().toString();
                                }
                                if( data.hasChild( "sender" ) )
                                {
                                    sender = data.child( "sender" ).getValue().toString();
                                }
                                if( data.hasChild( "time" ) )
                                {
                                    time = data.child( "time" ).getValue().toString();
                                }

                                if( sender.equals(myPhone) )
                                    g = Gravity.END;
                                else
                                    g = Gravity.START;

                                chatMessage m = new chatMessage( new String(content), new String(time), g);

                                if( !contains( result, m ) )
                                {
                                    result.add( new chatMessage( new String(content), new String(time), g) );
                                    //insert into the local db
                                    messages messo = new messages( groupChat.this );
                                    messo.open();
                                    messo.addNewMessage( new String(sender), new String(groupKey), new String(content), new String(time) );
                                    messo.close();
                                }
                            }
                        }


                        //chatMessageAdapter adapter = new chatMessageAdapter(groupChat.this, result);
                        //chatList.setAdapter( adapter );

                        //notifyOfMessage();
                        loadFromLocalDb();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(groupChat.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }

                });
            }catch( Exception e )
            {
                Toast.makeText(groupChat.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        private ArrayList<chatMessage> getMessagesFromLocalDb()
        {

            ArrayList<chatMessage> result = new ArrayList<>(10);
            //String myPhone = new String("0712696965");

            messages m = new messages( groupChat.this );
            m.open();
            ArrayList<msg> data = m.getMessagesSentTo(groupKey);
            m.close();

            //String s = "";
            for( msg message : data)
            {
                int gravity = message.sender.equals(myPhone) ? Gravity.END : Gravity.START;
                chatMessage s = new chatMessage( message.content, message.time, gravity);
                result.add( s );
            }

            return result;

        }


    }

}

/*
class myTask implements Runnable
{
    DatabaseReference dbRef;
    Context context;
    String groupKey;
    String myPhone;

    ArrayList<chatMessage> chatMessages;

    ListView chatList;
    public myTask(DatabaseReference dbRef, Context context, ArrayList<chatMessage> chatMessages, ListView chatList, String groupKey, String myPhone)
    {
        this.dbRef = dbRef;
        this.context = context;
        this.chatMessages = chatMessages;
        this.chatList = chatList;
        this.groupKey = groupKey;
        this.myPhone = myPhone;
    }

    @Override
    public void run()
    {
        try{
            dbRef.child(groupKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if( snapshot.hasChildren() )
                    {
                        for( DataSnapshot data : snapshot.getChildren() )
                        {
                            String content = new String( "" );
                            String sender = new String( "" );
                            String time = new String( "" );
                            int g = Gravity.END;

                            if( data.hasChild( "content" ) )
                            {
                                content = data.child( "content" ).getValue().toString();
                            }
                            if( data.hasChild( "sender" ) )
                            {
                                sender = data.child( "sender" ).getValue().toString();
                            }
                            if( data.hasChild( "time" ) )
                            {
                                time = data.child( "time" ).getValue().toString();
                            }

                            if( sender.equals(myPhone) )
                                g = Gravity.END;
                            else
                                g = Gravity.START;

                            chatMessage m = new chatMessage( new String(content), new String(time), g);

                            if( !contains( chatMessages, m ) )
                                chatMessages.add( new chatMessage( new String(content), new String(time), g) );

                        }
                    }

                   // chatMessageAdapter adapter = new chatMessageAdapter(context, chatMessages);
                   // chatList.setAdapter( adapter );

                    notifyOfMessage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                }

            });
        }catch( Exception e )
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyOfMessage()
    {

        //send a notification that some new message has been recieved

    }

    private boolean contains(ArrayList<chatMessage> list, chatMessage value)
    {
        for( chatMessage listItem : list )
        {
            if( listItem.getMessage().equals( value.getMessage() ) && listItem.getTime().equals( value.getTime() ) )
                return true;
        }
        return false;
    }

} */


class groupMsg
{
    public String sender = new String("");
    public String content = new String("");
    public String time = new String("");

    public groupMsg(){}

    public groupMsg( String sender, String content, String time)
    {
        this.sender = sender;
        this.content = content;
        this.time = time;
    }
}

class groupParticipant
{
    public String username = new String("");
    public String phone = new String("");

    public groupParticipant(){}

    public groupParticipant( String username, String phone )
    {
        this.username = username;
        this.phone = phone;
    }
}

class participants
{
    public participants()
    {

    }
}

class GroupChatRoom
{
    public String groupName = new String("");
    public groupMsg message = new groupMsg();

    public GroupChatRoom(){}

    public GroupChatRoom( String groupName)
    {
        this.groupName = groupName;
    }

}

