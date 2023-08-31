package com.example.frats;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Consumer;

public class chat extends AppCompatActivity {

    ListView chatList;
    //TableLayout chatList;
    EditText e;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);


        try {

            chatList = findViewById(R.id.chatList);
            //chatList = findViewById(R.id.chat_list);
            e = findViewById(R.id.messageToSend);
            send = findViewById(R.id.sendMessage);
            //FirebaseApp.initializeApp(getBaseContext());

            /*user u = new user(chat.this);
            u.open();
            String data[] = new String[3];
            data = u.readData();
            u.close();*/

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message");

            //myRef.setValue("Hello, World!");

            Intent thisIntent = getIntent();
            String myRecipient = thisIntent.getStringExtra("recipient");

            ArrayList<String> arr = new ArrayList<>(10);

             myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                //arr.add(value);

                //msg m = (msg) dataSnapshot.getValue(msg.class);
                //arr.add(m.content);
                //String m = dataSnapshot.getValue().toString();
                //arr.add(m);

                Iterable<DataSnapshot> iter = dataSnapshot.getChildren();

               //iter.forEach( (element) -> {arr.add( ( element.getValue().toString()) );});

                for( DataSnapshot msgSnapShot : iter)
                {
                    //check if this message is sent to me
                    if( msgSnapShot.hasChild("recipient") )
                    {
                        String p = msgSnapShot.child("recipient").getValue().toString();
                        user u = new user(chat.this);
                        u.open();
                        String data[] = new String[3];
                        data = u.readData();

                       if( !( p.equals( data[2] ) ) && !(p.equals(myRecipient)) )
                       {
                           if( msgSnapShot.hasChild("sender") )
                           {
                               String s = msgSnapShot.child("sender").getValue().toString();
                               if( !( s.equals( data[2] ) ) && !( s.equals(myRecipient)))
                                   continue;
                           }

                       }

                        u.close();
                    }

                    String s = "";
                    if( msgSnapShot.hasChild("content") )
                    {
                        s = msgSnapShot.child("content").getValue().toString();

                    }

                    if( msgSnapShot.hasChild("time"))
                    {
                        s += "\n\n" + msgSnapShot.child("time").getValue().toString();

                    }

                    if( arr.contains(s))
                        continue;
                    else
                        arr.add(s);
                }
               // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(chat.this, R.layout.chat_message, R.id.msg, arr);
                //chatList.setAdapter(arrayAdapter);
                

                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(chat.this, error.toString(),Toast.LENGTH_SHORT).show();
                // Failed to read value
              //  Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = "";
                    s = e.getText().toString();
                    e.setText("");

                    user u = new user(chat.this);
                    u.open();
                    String data[] = new String[3];
                    data = u.readData();
                    u.close();

                    Calendar c = Calendar.getInstance();
                    String time = c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) +
                            ( ( c.get(Calendar.AM_PM) == Calendar.AM) ? " AM" : " PM" );

                    //msg m = new msg(data[2],"0712696965",s, time);
                    msg m = new msg(data[2],myRecipient,s, time);
                    myRef.push().setValue(m);
                    //ArrayList<String> arr = new ArrayList<>(10);

                    //arr.add(s);

                    // Read from the database
                }
            });

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(chat.this, R.layout.chat_message, R.id.msg, arr);
            chatList.setAdapter(arrayAdapter);


        }catch(Exception e)
        {
            Toast.makeText(chat.this, e.toString(),Toast.LENGTH_SHORT).show();
        }

    }
}


class msg
{
    public String sender;
    public String recipient;
    public String content;
    public String time;

    public msg(){}

    public msg(String sender,String recipient,String content, String time)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
         //time = android.os.SystemClock.currentNetworkTimeClock().toString();
        this.time = time;
    }

}
