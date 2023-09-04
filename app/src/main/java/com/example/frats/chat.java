package com.example.frats;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

    TextView title;
    String thisChatKey = new String();
    //ArrayList<chatMessage> arr = new ArrayList<>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        title = findViewById(R.id.chat_name);

        Intent in = getIntent();

        title.setText( in.getStringExtra("chatName"));


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

            //ArrayList<String> arr = new ArrayList<>(10);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //DatabaseReference myRef = database.getReference("chat_room");
            DatabaseReference chatRoomRef = database.getReference("chat_room");
           // Toast.makeText(this, "adding on complete listener to chat_room", Toast.LENGTH_SHORT).show();

            //DataSnapshot chatSnap = chatRoomRef.get().getResult();
            //Toast.makeText(chat.this, chatRoomRef.get().toString(), Toast.LENGTH_SHORT).show();

            chatRoomRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot chatSnapshot) {
                    //Toast.makeText(chat.this, chatSnapshot.toString(), Toast.LENGTH_SHORT).show();

                    if( loadChat(chatRoomRef,chatSnapshot,myRecipient,data[2]) )
                    {
                        //after loading chats,add value event listener
                        chatRoomRef.child(thisChatKey).child("messages").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if( snapshot.hasChildren() )
                                {
                                    ArrayList<chatMessage> arr = new ArrayList<>(10);
                                    for( DataSnapshot message : snapshot.getChildren() )
                                    {
                                        String m = "";
                                        String t = "";
                                        int g = 0;
                                        if(message.hasChild("content") )
                                            m = message.child("content").getValue().toString();
                                            //s+= message.child("content").getValue().toString();
                                        if( message.hasChild("time") )
                                            t = message.child("time").getValue().toString();
                                            //s+= "\n\n" + message.child("time").getValue().toString();
                                        if( message.hasChild("sender") )
                                        {
                                            String sender = message.child("sender").getValue().toString();
                                            g = ( (sender.equals(data[2]) ) ? Gravity.END : Gravity.START);
                                        }

                                        //if(arr.contains( new chatMessage( new String(m), new String(t) )) )
                                        //    continue;
                                       // else
                                            arr.add( new chatMessage( new String(m), new String(t), g) );
                                    }
                                    chatMessageAdapter arrayAdapter = new chatMessageAdapter(chat.this,arr);
                                    chatList.setAdapter(arrayAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            });

               /*  chatRoomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    boolean found = false;
                    @Override
                    public void onComplete(Task<DataSnapshot> task) {
                        //Tasks.whenAllComplete(task);
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

                     //if the chat isn`t found create a new one
                    /* if( found == false )
                    {
                        chatRoom newRoom = new chatRoom(data[2], myRecipient);
                        chatRoomRef.push().setValue(newRoom);
                    }

                    }
                }); */


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
                    try {


                        String s = "";
                        s = e.getText().toString();
                        e.setText("");

                        Calendar c = Calendar.getInstance();
                        int hr = c.get(Calendar.HOUR);
                        int min = c.get(Calendar.MINUTE);
                        String time = ( (hr < 10) ? ("0" + hr) : ("" + hr)) + ":" + ( (min < 10) ? ("0" + min) : ("" + min)) +
                                ((c.get(Calendar.AM_PM) == Calendar.AM) ? " AM" : " PM");

                        //msg m = new msg(data[2],"0712696965",s, time);
                        msg m = new msg(data[2], myRecipient, s, time);
                        // myRef.push().setValue(m);
                        //ArrayList<String> arr = new ArrayList<>(10);
                        chatRoomRef.child(thisChatKey).child("messages").push().setValue(m);

                        //arr.add(s);

                        // Read from the database
                    }catch(Exception e)
                    {
                        Toast.makeText(chat.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //chatMessageAdapter arrayAdapter = new chatMessageAdapter(chat.this,arr);
            //chatList.setAdapter(arrayAdapter);
           // chatList.setStackFromBottom(true);

        }catch (Exception e)
        {
            Toast.makeText(chat.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean loadChat(DatabaseReference chatRoomRef,DataSnapshot chatSnapshot,String myRecipient,String myPhone)
    {
        try {
            boolean found = false;


            if (chatSnapshot.hasChildren()) {

                for (DataSnapshot room : chatSnapshot.getChildren()) {
                    String participants[] = {new String(myPhone), new String(myRecipient)};
                    if (room.hasChild("participants")) {
                        String p1, p2;
                        p1 = room.child("participants").child("p1").getValue().toString();
                        p2 = room.child("participants").child("p2").getValue().toString();

                        if ((p1.equals(participants[0]) && p2.equals(participants[1])) ||
                                (p2.equals(participants[0]) && p1.equals(participants[1]))) {
                            ArrayList<chatMessage> arr = new ArrayList<>(10);
                            found = true;
                            thisChatKey = room.getKey().toString();
                            //Toast.makeText(this, "found chat!", Toast.LENGTH_SHORT).show();
                            if (room.hasChild("messages")) {

                                if (room.child("messages").hasChildren()) {
                                    for (DataSnapshot message : room.child("messages").getChildren()) {
                                        String m = "";
                                        String t = "";
                                        int g = 0;
                                        if (message.hasChild("content"))
                                            m = message.child("content").getValue().toString();
                                           // s += message.child("content").getValue().toString();
                                        if (message.hasChild("time"))
                                            t = message.child("time").getValue().toString();
                                            //s += "/n/n" + message.child("time").getValue().toString();
                                        if( message.hasChild("sender") )
                                        {
                                            String sender = message.child("sender").getValue().toString();

                                            g = ( (sender.equals(myPhone) ) ? Gravity.END : Gravity.START );
                                        }
                                        chatMessage newMessage = new chatMessage( new String(m), new String(t), g );
                                        //if(arr.contains( newMessage ) )
                                         //   continue;
                                       // else
                                            arr.add( newMessage );
                                    }
                                }
                            }

                            //if chat is found,then load the messages
                            //to the list view
                            chatMessageAdapter arrayAdapter = new chatMessageAdapter(chat.this,arr);
                            chatList.setAdapter(arrayAdapter);

                        } else {
                            //nothing
                        }
                    }
                }

                if( !found )
                {
                    //Toast.makeText(this, "creating new chat", Toast.LENGTH_SHORT).show();
                    chatRoom newChat = new chatRoom(myPhone, myRecipient);
                    thisChatKey = chatRoomRef.push().getKey().toString();
                    chatRoomRef.child(thisChatKey).setValue(newChat);
                }

            }

        }catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        return true;
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
    public String p1 = new String();
    public String p2 = new String();

    public p(){}
    public p(String p1, String p2)
    {
        this.p1 = p1;
        this.p2 = p2;
    }
}

class chatRoom
{
    public p participants = new p();
    //public ArrayList<msg> messages;
    public msg messages = new msg();

    public chatRoom(){}
    public chatRoom(String sender,String recipient)
    {
        participants.p1 = sender;
        participants.p2 = recipient;
        //message.sender = sender;
        //message.recipient = recipient;
    }

}
