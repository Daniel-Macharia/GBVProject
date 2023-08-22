package com.example.frats;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class chat extends AppCompatActivity {

    ListView chatList;
    EditText e;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.chat);


            chatList = findViewById(R.id.chatList);
            e = findViewById(R.id.messageToSend);
            send = findViewById(R.id.sendMessage);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message");

            myRef.setValue("Hello, World!");

            ArrayList<String> arr = new ArrayList<>(10);

       /* myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                arr.add(value);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(chat.this, R.layout.chat_message, R.id.msg, arr);
                chatList.setAdapter(arrayAdapter);

                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(chat.this, error.toString(),Toast.LENGTH_SHORT).show();
                // Failed to read value
              //  Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = "";
                    s = e.getText().toString();


                    myRef.setValue(s);
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
