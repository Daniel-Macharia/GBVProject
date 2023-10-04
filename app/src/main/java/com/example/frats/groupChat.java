package com.example.frats;

import android.content.Context;
import android.content.Intent;
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

            Intent intent = getIntent();
            int index = intent.getIntExtra("index", 0);

            groupKey = groups.group.get( index ).first;
            groupName.setText( groups.group.get( index ).second );

            getMyPhone();
            //initWithAnyChats();


            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference myRef = db.getReference("group");

            myTask task = new myTask(myRef);
            Thread t = new Thread( task );
            t.start();


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

                    myRef.child(groupKey).push().setValue( meso );

                    Toast.makeText(groupChat.this, message, Toast.LENGTH_SHORT).show();

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


    private void initWithAnyChats()
    {
        ArrayList<chatMessage> result = new ArrayList<>(10);
        String myPhone = new String("0712696965");

        messages m = new messages( groupChat.this );
        m.open();
        ArrayList<msg> data = m.getMessagesSentTo(myPhone);
        m.close();

        //String s = "";
        for( msg message : data)
        {
            chatMessage s = new chatMessage( message.content, message.time, Gravity.END);
            result.add( s );
        }

        chatMessageAdapter adapter = new chatMessageAdapter( groupChat.this, result);
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

                        chatMessageAdapter adapter = new chatMessageAdapter(groupChat.this, chatMessages);
                        chatList.setAdapter( adapter );
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

    }

}

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

