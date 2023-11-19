package com.example.frats;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
    EditText e;
    Button send;
    ArrayList<chatMessage> arr = new ArrayList<>(10);

    TextView title;
    String thisChatKey = null;
    String data[] = new String[4];
    DatabaseReference chatRoomRef;
    String myRecipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        title = findViewById(R.id.chat_name);

        //notifyOfMessage();

        Intent in = getIntent();

        title.setText(in.getStringExtra("chatName"));


        try {

            chatList = findViewById(R.id.chatList);
            e = findViewById(R.id.messageToSend);
            send = findViewById(R.id.sendMessage);

            user u = new user(chat.this);
            u.open();
            data = u.readData();
            u.close();

            Intent thisIntent = getIntent();
            myRecipient = thisIntent.getStringExtra("recipient");

            loadFromLocalDB(myRecipient);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            chatRoomRef = database.getReference("chat_room");

            readFromFirebase readTask = new readFromFirebase();
            Thread t = new Thread(readTask);
            t.start();

            int count = 0;
            NewMessageCounter nmc = new NewMessageCounter(this);
            nmc.open();
            count = nmc.getCount(myRecipient);
            nmc.close();
            Toast.makeText(this, ((count < 1 ) ? 0 : count) + " New Mesages from this chat", Toast.LENGTH_SHORT).show();

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        EncryptMessage em = new EncryptMessage();
                        String s = "";
                        s = em.encrypt( e.getText().toString() );
                        e.setText("");

                        Calendar c = Calendar.getInstance();
                        int hr = c.get(Calendar.HOUR);
                        int min = c.get(Calendar.MINUTE);
                        String time = ((hr < 10) ? ("0" + hr) : ("" + hr)) + ":" + ((min < 10) ? ("0" + min) : ("" + min)) +
                                ((c.get(Calendar.AM_PM) == Calendar.AM) ? " AM" : " PM");


                        msg m = new msg(data[2], myRecipient, s , time);

                        if (thisChatKey == null) {
                            Toast.makeText(chat.this, "Check your internet connection\n and try again", Toast.LENGTH_SHORT).show();
                        } else {
                            chatRoomRef.child(thisChatKey).child("messages").push().setValue(m);
                            messages ms = new messages(chat.this);
                            ms.open();
                            ms.addNewMessage(new String(m.sender),
                                    new String(m.recipient),
                                    new String( m.content ),
                                    new String(m.time));
                            ms.close();
                        }
                        //loadFromLocalDB(myRecipient);

                    } catch (Exception e) {
                        Toast.makeText(chat.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // chatMessageAdapter arrayAdapter = new chatMessageAdapter(chat.this,arr);
            //chatList.setAdapter(arrayAdapter);

        } catch (Exception e) {
            Toast.makeText(chat.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean contains(ArrayList<chatMessage> list, chatMessage value) {

        for (chatMessage listItem : list) {
            if (listItem.getMessage().equals(value.getMessage()) && listItem.getTime().equals(value.getTime()))
                return true;
        }

        return false;
    }

    private void loadFromLocalDB(String recipient) {
        ArrayList<msg> ms = new ArrayList<>(10);
        //ArrayList<chatMessage> arr = new ArrayList<>(10);
        EncryptMessage em = new EncryptMessage();

        messages messageDB = new messages(chat.this);
        messageDB.open();
        ms = messageDB.getMessagesSentTo(recipient, data[2]);
        messageDB.close();

        for (msg m : ms) {
            int g = (recipient.equals(m.recipient) ? Gravity.END : Gravity.START);
            chatMessage chatM = new chatMessage( em.decrypt( m.content), m.time, g);

            if (!contains(arr, chatM)) {
                arr.add(new chatMessage( chatM.getMessage() , m.time, g));
            }

        }

        //now add these messages to the chat message list
        chatMessageAdapter arrayAdapter = new chatMessageAdapter(chat.this, arr);
        chatList.setAdapter(arrayAdapter);

    }


    class readFromFirebase implements Runnable {

        private Task<DataSnapshot> initThisChatKey() {
            Task<DataSnapshot> t = chatRoomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {


                    DataSnapshot result = task.getResult();

                    if (result.hasChildren()) {
                        for (DataSnapshot chat : result.getChildren()) {
                            if (chat.hasChild("participants")) {
                                String p1 = new String("");
                                String p2 = new String("");
                                if (chat.child("participants").hasChild("p1")) {
                                    p1 = chat.child("participants").child("p1").getValue().toString();
                                }
                                if (chat.child("participants").hasChild("p2")) {
                                    p2 = chat.child("participants").child("p2").getValue().toString();
                                }

                                //verify
                                if ((p1.equals(data[2]) && p2.equals(myRecipient)) ||
                                        (p1.equals(myRecipient) && p2.equals(data[2]))) {
                                    thisChatKey = chat.getKey().toString();
                                    return;
                                }

                            }

                        }
                    }


                    if (thisChatKey == null) {
                        chatRoom c = new chatRoom(data[2], myRecipient);
                        thisChatKey = chatRoomRef.push().getKey().toString();
                        chatRoomRef.child(thisChatKey).setValue(c);
                    }

                }


            });

            return t;

        }

        @Override
        public void run() {
            initThisChatKey().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    chatRoomRef.child(thisChatKey).child("messages").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            EncryptMessage em = new EncryptMessage();

                            loadFromLocalDB( myRecipient );

                           // MyFirebaseUtilityClass.valueChangedForThisChatRoom( chatList, snapshot, chat.this, myRecipient);


                            if (snapshot.hasChildren()) {
                                for (DataSnapshot message : snapshot.getChildren()) {
                                    msg mess = new msg();
                                    String m = "";
                                    String t = "";
                                    int g = 0;
                                    if (message.hasChild("content")) {
                                        m = message.child("content").getValue().toString();
                                        mess.content = new String(m);
                                    }
                                    if (message.hasChild("time")) {
                                        t = message.child("time").getValue().toString();
                                        mess.time = new String(t);
                                    }
                                    if (message.hasChild("sender")) {
                                        String sender = message.child("sender").getValue().toString();
                                        g = ((sender.equals(data[2])) ? Gravity.END : Gravity.START);
                                        mess.sender = new String(sender);
                                    }
                                    if (message.hasChild("recipient")) {
                                        String r = message.child("recipient").getValue().toString();

                                        mess.recipient = new String(r);
                                    }

                                    chatMessage meso = new chatMessage(new String( em.decrypt(mess.content) ), new String(mess.time), g);
                                    if (!contains(arr, meso)) {
                                       // Toast.makeText(chat.this, "inserted " + meso.getMessage(), Toast.LENGTH_SHORT).show();
                                        arr.add(meso);
                                        try {
                                            messages ms = new messages(chat.this);
                                            ms.open();
                                            ms.addNewMessage(mess.sender, mess.recipient,  mess.content, mess.time);
                                            ms.close();
                                        } catch (Exception e) {
                                            Toast.makeText(chat.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                   // if (mess.recipient.equals(data[2])) {
                                       // message.getRef().removeValue();
                                   // }


                                }


                            }

                            //chatMessageAdapter arrayAdapter = new chatMessageAdapter(chat.this, arr);
                           // chatList.setAdapter(arrayAdapter);
                            //notifyOfMessage();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });

        }

    }

    private void notifyOfMessage() {
        String id = "noId";
        String body = " new Message(s)";
       /*  NotificationManager nm = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

        Notification notify = new Notification.Builder( getApplicationContext() )
                .setContentTitle( "Notification" )
                .setContentText(body)
                .setSmallIcon(R.drawable.balloon1).build();

        //notify.flags |= Notification.FLAG_AUTO_CANCEL;

        nm.notify(0, notify); */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "name";
            String description = "desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);

            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(chat.this, id)
                .setSmallIcon(R.drawable.balloon1)
                .setContentTitle("Notification")
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat nmc = NotificationManagerCompat.from(chat.this);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        nmc.notify(7, builder.build());

    }

}


class msg
{
    public String sender = "";
    public String recipient = "";
    public String content = "";
    public String time = "";

    public msg(){}

    public msg(String sender,String recipient,String content, String time)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
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
    //public msg messages = new msg();

    public chatRoom(){}
    public chatRoom(String sender,String recipient)
    {
        participants.p1 = sender;
        participants.p2 = recipient;

    }

}


