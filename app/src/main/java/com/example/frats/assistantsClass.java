package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Scanner;

public class assistantsClass extends AppCompatActivity {
    private ListView l;
    private TextView title;
    private String[] userOrAssistant = new String[4];
    private ArrayList<String> list = new ArrayList<>(10);
    private ArrayList<userOrAssistant> newList = new ArrayList<>(10);
    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assistants);
        l = findViewById(R.id.assistants_list);
        title = findViewById(R.id.title);

        //loadUserFromLocalDb();
       // list = getUsersFromLocalDB();
        //newList  = new ArrayList<>(10);
        getUsersFromLocalDB();


        try {


            // userOrAssistant = new String[4];
            user u = new user(assistantsClass.this);
            u.open();
            userOrAssistant = u.readData();
            u.close();
            //FirebaseDatabase myDB = FirebaseDatabase.getInstance();
            //DatabaseReference myRef = myDB.getReference("assistant");
            String uOa = "";
            if( userOrAssistant[3].equals("users") )
            {
                uOa =  "assistant" ;

                //ArrayAdapter<String> arr = new ArrayAdapter<>(assistantsClass.this,R.layout.assistant_contact,R.id.name,list);
                userOrAssistantAdapter arr = new userOrAssistantAdapter( this, newList);
                l.setAdapter(arr);

            }
            else
            {
                uOa = "users";
                title.setText("My Survivors");
                //loadChats();

                userOrAssistantAdapter arr = new userOrAssistantAdapter( this, newList);
                l.setAdapter(arr);
            }


        }catch(Exception e)
        {
            Toast.makeText(assistantsClass.this, e.toString(), Toast.LENGTH_SHORT).show();
        }



    }
    private boolean contains(ArrayList<String> list, String value)
    {

        for( String compare : list )
        {
            if( value.equals( compare ) )
            {
                return true;
            }
        }

        return false;
    }

   /* private ArrayList<String> loadChats()
    {
        ArrayList<String> mySurvivors = new ArrayList<>(10);

        try{
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference myRef = db.getReference("chat_room");


            myRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {

                    if( dataSnapshot.hasChildren() )
                    {
                        for( DataSnapshot roomSnapShot : dataSnapshot.getChildren() )
                        {
                            if( roomSnapShot.hasChild("participants") )
                            {
                                String[] participants = new String[2];
                                if( roomSnapShot.child("participants").hasChild("p1") )
                                {
                                    participants[0] = roomSnapShot.child("participants").child("p1").getValue().toString();
                                }
                                if( roomSnapShot.child("participants").hasChild("p2") )
                                {
                                    participants[1] = roomSnapShot.child("participants").child("p2").getValue().toString();
                                }

                                if( userOrAssistant[2].equals( participants[0]) ||
                                        userOrAssistant[2].equals(participants[1]) )
                                {
                                    //get this message since I`m among the participants
                                    if( roomSnapShot.hasChild("messages") )
                                    {
                                        if( roomSnapShot.child( "messages").hasChildren() )
                                        {
                                            for( DataSnapshot message : roomSnapShot.child("messages").getChildren() )
                                            {
                                                msg newMessage = new msg();

                                                if( message.hasChild("sender") )
                                                {
                                                    newMessage.sender = message.child("sender").getValue().toString();
                                                }
                                                if( message.hasChild("recipient") )
                                                {
                                                    newMessage.recipient = message.child("recipient").getValue().toString();
                                                }
                                                if( message.hasChild("content") )
                                                {
                                                    newMessage.content = message.child("content").getValue().toString();
                                                }
                                                if( message.hasChild("time") )
                                                {
                                                    newMessage.time = message.child("time").getValue().toString();
                                                }
                                                //Toast.makeText(assistantsClass.this, "Adding user and messages", Toast.LENGTH_SHORT).show();
                                                messages messo = new messages(assistantsClass.this);
                                                messo.open();
                                                messo.addNewMessage( new String( newMessage.sender),
                                                        new String( newMessage.recipient ),
                                                        new String( newMessage.content),
                                                        new String( newMessage.time));
                                                messo.close();

                                                //if( userOrAssistant[2].equals(participants[0] ) )
                                                //   mySurvivors.add( participants[1] );
                                                //else
                                                //    mySurvivors.add( participants[0] );

                                                if( newMessage.recipient.equals( userOrAssistant[2] ) )
                                                    message.getRef().removeValue();

                                            }
                                        }
                                    }

                                    String theirNumber = new String();
                                    if( userOrAssistant[2].equals( participants[0] ) )
                                        theirNumber = participants[1];
                                    else
                                        theirNumber = participants[0];

                                    if( !contains( list, theirNumber ) )
                                    {
                                        list.add( new String( theirNumber ) );
                                        mySurvivors.add( new String( theirNumber ) );

                                    }



                                }
                            }
                        }

                    }
                    else {
                        //nothing
                    }

                }
            }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    DatabaseReference usersReference = db.getReference("users");

                    usersReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot result = task.getResult();

                            if( result.hasChildren() )
                            {
                                for( DataSnapshot userData : result.getChildren() )
                                {
                                    String username = "", phone = "";

                                    if( userData.hasChild("username") )
                                    {
                                        username = userData.child("username").getValue().toString();
                                    }
                                    if( userData.hasChild("phone") )
                                    {
                                        phone = userData.child("phone").getValue().toString();
                                    }

                                    if( mySurvivors.contains( phone ) )
                                    {
                                        newList.add( new userOrAssistant( new String( username ), new String( phone )));

                                        user us = new user( assistantsClass.this);
                                        us.open();
                                        us.createUser( new String(username),
                                                new String( phone ),
                                                new String(phone),
                                                new String("users"),
                                                0 );
                                        us.close();
                                    }

                                }
                            }
                        }

                    });
                }
            });


        }catch( Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


        return mySurvivors;

    }  */

    private void getUsersFromLocalDB()
    {
        ArrayList<String[]> r = new ArrayList<>(10);

        user u = new user(assistantsClass.this);
        u.open();
        r = u.getUsers();
        u.close();
        //String s = "";

       // ArrayList<String> result = new ArrayList<>();
        for( String[] user : r )
        {
           // s += user[0] + " " + user[1] + " \n";
            //result.add( new String( user[1] ));
            newList.add( new userOrAssistant( new String( user[0] ), new String( user[1] ) ) );
            //Toast.makeText(this, "Old user = " + user[1], Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText( this, s, Toast.LENGTH_LONG).show();

        //return result;
    }

}
