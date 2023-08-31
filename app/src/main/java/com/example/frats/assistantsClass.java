package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.util.Scanner;

public class assistantsClass extends AppCompatActivity {
    ListView l;
    TextView title;

    String selectedAssistant;
    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assistants);
        l = findViewById(R.id.assistants_list);
        title = findViewById(R.id.title);

        try {

            String userOrAssistant[] = new String[4];
            user u = new user(assistantsClass.this);
            u.open();
            userOrAssistant = u.readData();
            u.close();
            FirebaseDatabase myDB = FirebaseDatabase.getInstance();
            //DatabaseReference myRef = myDB.getReference("assistant");
            String uOa = "";
            if( userOrAssistant[3].equals("users") )
                uOa =  "assistant" ;
            else
            {
                uOa = "users";
                title.setText("my survivors");
            }


            DatabaseReference myRef = myDB.getReference(uOa);

           // DataSnapshot snap = myRef.get().getResult();
            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
            {
               @Override
               public void onComplete(Task<DataSnapshot> task)
               {
                   if( task.isSuccessful())
                   {
                       DataSnapshot snap = task.getResult();
                       Iterable<DataSnapshot> iter = snap.getChildren();

                       ArrayList<String> list = new ArrayList<>(10);
                       for( DataSnapshot s : iter)
                       {

                           String a[] = new String[2];
                           if(s.hasChild("username") )
                               a[0] = s.child("username").getValue().toString();
                           if(s.hasChild("phone") )
                               a[1] = s.child("phone").getValue().toString();

                           list.add(a[0] + " : " + a[1]);
                       }

                       ArrayAdapter<String> arr = new ArrayAdapter<>(assistantsClass.this,R.layout.assistant_contact,R.id.name,list);
                       l.setAdapter(arr);
                   }
                   else {

                   }
               }

            });


        }catch(Exception e)
        {
            Toast.makeText(assistantsClass.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Iterable<DataSnapshot> iter = snapshot.getChildren();

                ArrayList<String> list = new ArrayList<>(10);
                for( DataSnapshot s : iter)
                {

                    String a[] = new String[2];
                    if(s.hasChild("username") )
                        a[0] = s.child("username").getValue().toString();
                    if(s.hasChild("phone") )
                        a[1] = s.child("phone").getValue().toString();

                    list.add(a[0] + " : " + a[1]);
                }

                ArrayAdapter<String> arr = new ArrayAdapter<>(assistantsClass.this,R.layout.assistant_contact,R.id.name,list);
                l.setAdapter(arr);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(assistantsClass.this, error.toString(),Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    public void loadChat(View view)
    {
        TextView t = view.findViewById(R.id.name);
        String phoneStr = t.getText().toString();

        Scanner s = new Scanner(phoneStr);
        s.next();//discard name
        s.next();//discard colon
        String phone = s.next();

        Intent chatIntent = new Intent( assistantsClass.this, chat.class);
        chatIntent.putExtra("recipient",phone);
        startActivity(chatIntent);
    }
}
