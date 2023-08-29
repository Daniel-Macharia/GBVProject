package com.example.frats;

import static android.content.ContentValues.TAG;

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

            ArrayList<String> arr = new ArrayList<>(10);

             myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getValue(String.class);
                //arr.add(value);

                String m = dataSnapshot.getValue().toString();
                arr.add(m);

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

                    msg m = new msg(data[2],"0712696965",s);
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
    public msg(String sender,String recipient,String content)
    {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
         //time = android.os.SystemClock.currentNetworkTimeClock().toString();
        time = "";
    }

}
