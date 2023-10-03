package com.example.frats;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class groupChat extends AppCompatActivity {

    TextView groupName;
    EditText et;
    Button send;

    ListView chatList;
    String key = new String();
    String myPhone = new String("");

    ArrayList<String> messages = new ArrayList<>(10);

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

            key = groups.group.get( index ).first;
            groupName.setText( groups.group.get( index ).second );

            getMyPhone();
            initWithAnyChats();

            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference myRef = db.getReference("group");



            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar cal = Calendar.getInstance();
                    String message = et.getText().toString();
                    et.setText("");
                    String time = new String(cal.get( Calendar.HOUR ) +
                            " : " + cal.get( Calendar.MINUTE ) +
                            ( ( cal.get( Calendar.AM_PM ) == Calendar.AM ) ? " AM " : " PM " ) );

                    groupMsg meso = new groupMsg(myPhone, message, time);

                    myRef.child(key).push().setValue( meso );

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

class readFromCloud implements Runnable
{
    DatabaseReference dbRef;

    public readFromCloud(DatabaseReference dbRef)
    {
        this.dbRef = dbRef;
    }
    @Override
    public void run() {

        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

            }
        });

    }
}
