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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    String thisChatKey;

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

            //obtain this user`s phone number in data[2]
            user u = new user(chat.this);
            u.open();
            final String data[] = u.readData();
            u.close();

            //obtain the intended recipient`s phone number
            Intent thisIntent = getIntent();
            String myRecipient = thisIntent.getStringExtra("recipient");

            //String thisChatKey = new String();
            //FirebaseApp.initializeApp(getBaseContext());

            /*user u = new user(chat.this);
            u.open();
            String data[] = new String[3];
            data = u.readData();
            u.close();*/

            ArrayList<String> arr = new ArrayList<>(10);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //DatabaseReference myRef = database.getReference("chat_room");
            DatabaseReference chatRoomRef = database.getReference("chat_room");
            Toast.makeText(this, "adding on complete listener to chat_room", Toast.LENGTH_SHORT).show();
            chatRoomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                boolean found = false;
                @Override
                public void onComplete(Task<DataSnapshot> task) {
                    //boolean found = false;
                    Toast.makeText(chat.this, "getting snapshot of chat_room", Toast.LENGTH_SHORT).show();

                    if( task.isSuccessful() )
                    {
                        DataSnapshot chats = task.getResult();

                        Toast.makeText(chat.this, chats.toString(), Toast.LENGTH_SHORT).show();
                        if( chats.hasChildren() )
                        {
                            Iterable<DataSnapshot>  allChats = chats.getChildren();
                            for( DataSnapshot c : allChats )
                            {
                                if( c.hasChild("participants") )
                                {
                                    String []participants = new String[2];
                                    participants[0] = c.child("participants").child("p1").getValue().toString();
                                    participants[1] = c.child("paticipants").child("p2").getValue().toString();

                                    Toast.makeText(chat.this, c.toString(), Toast.LENGTH_SHORT).show();
                                    //validate the sender and receiver
                                    if( (participants[0].equals(data[2]) || participants.equals(myRecipient) ) &&
                                            ( participants[1].equals(data[2]) || participants[1].equals(myRecipient) ) )
                                    {
                                        found = true;
                                        //load the chat
                                        if( c.hasChild("messages") )
                                        {
                                            if( c.child("messages").hasChildren() )
                                            {
                                                Iterable<DataSnapshot> messages = c.child("messages").getChildren();
                                                for( DataSnapshot message : messages )
                                                {
                                                    String s = "";
                                                    if( message.hasChild("content") )
                                                    {
                                                        s = message.child("content").getValue().toString();

                                                    }

                                                    if( message.hasChild("time"))
                                                    {
                                                        s += "\n\n" + message.child("time").getValue().toString();

                                                    }

                                                    arr.add( new String(s));

                                                }
                                            }
                                        }

                                        DatabaseReference dbr = c.child(c.getKey()).child("messages").getRef();
                                        thisChatKey = dbr.getKey();

                                        dbr.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if( snapshot.hasChildren() )
                                                {
                                                    for( DataSnapshot d : snapshot.getChildren())
                                                    {
                                                        String s = "";
                                                        if( d.hasChild("content") )
                                                        {
                                                            s += d.child("content").getValue().toString();
                                                        }
                                                        if( d.hasChild("time") )
                                                        {
                                                            s += d.child("time").getValue().toString();
                                                        }

                                                        if( arr.contains(s) )
                                                            continue;
                                                        else
                                                            arr.add(s);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                Toast.makeText(chat.this, error.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else {
                                        //create a new chat
                                        //chatRoom newRoom = new chatRoom(data[2], myRecipient);
                                        //chatRoomRef.push().setValue(newRoom);
                                    }
                                }
                            }
                        }

                        //if chat is not found
                        if( !found )
                        {
                            chatRoom newRoom = new chatRoom(data[2], myRecipient);
                            chatRoomRef.push().setValue(newRoom);
                        }

                    }
                    else {
                        Toast.makeText(chat.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        //if the chat isn`t found create a new one

                    }

                    /* //if the chat isn`t found create a new one
                    if( found == false )
                    {
                        chatRoom newRoom = new chatRoom(data[2], myRecipient);
                        chatRoomRef.push().setValue(newRoom);
                    } */
                }

            });

            //myRef.setValue("Hello, World!");

            //ArrayList<String> arr = new ArrayList<>(10);

            /* myRef.addValueEventListener(new ValueEventListener() {
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
        }); */

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = "";
                    s = e.getText().toString();
                    e.setText("");

                    Calendar c = Calendar.getInstance();
                    String time = c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) +
                            ( ( c.get(Calendar.AM_PM) == Calendar.AM) ? " AM" : " PM" );

                    //msg m = new msg(data[2],"0712696965",s, time);
                    msg m = new msg(data[2],myRecipient,s, time);
                   // myRef.push().setValue(m);
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

class p
{
    public String p1;
    public String p2;

    public p(){}
    public p(String p1, String p2)
    {
        this.p1 = p1;
        this.p2 = p2;
    }
}

class chatRoom
{
    public p participants;
    public ArrayList<msg> messages;

    public chatRoom(){}
    public chatRoom(String sender,String recipient)
    {
        participants.p1 = sender;
        participants.p2 = recipient;
        //message.sender = sender;
        //message.recipient = recipient;
    }

}
