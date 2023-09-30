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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class groupChat extends AppCompatActivity {

    TextView groupName;
    EditText et;
    Button send;

    ListView chatList;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.group_chat_room );

        groupName = findViewById( R.id.groupName );
        et = findViewById( R.id.messageToSend );
        send = findViewById( R.id.send );
        chatList = findViewById( R.id.messageList );

        Intent intent = getIntent();
        String groupTitle = intent.getStringExtra("groupName");

        groupName.setText( groupTitle );

        initWithAnyChats();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = et.getText().toString();
                et.setText("");

                Toast.makeText(groupChat.this, message, Toast.LENGTH_SHORT).show();

            }
        });

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
