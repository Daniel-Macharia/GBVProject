package com.example.frats;

import android.app.Application;
import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;

public class LoadMessages extends Worker
{

public LoadMessages( Context context, WorkerParameters workerParams )
{
    super( context, workerParams );
    Toast.makeText(context, "in constructor of LoadMessages", Toast.LENGTH_SHORT).show();

}

    @NonNull
    @Override
    public Result doWork() {
        Toast.makeText( getApplicationContext(), "Doing some work in doWork() ", Toast.LENGTH_SHORT).show();


        try{

        Context context = getApplicationContext();
        Toast.makeText(context, "Doing some work in doWork() ", Toast.LENGTH_SHORT).show();

        //getMessagesFromFirebase messages = new getMessagesFromFirebase(context);

       // messages.add();

        return Worker.Result.success();
    }catch( Exception e )
    {
        Toast.makeText( getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        return Worker.Result.failure();
    }
    }
}

class getMessagesFromFirebase {


    Context context;
    private FirebaseDatabase db;
    private DatabaseReference groupRef, chatRef;
    public getMessagesFromFirebase( Context context)
    {
        this.context = context;
        db = FirebaseDatabase.getInstance();
        groupRef = db.getReference("group");
        chatRef = db.getReference("chat_room");

    }

    public void add()
    {
       // addChatMessages();
        addGroupMessages();
    }

    private void addChatMessages()
    {
        Toast.makeText( context, "Adding Chat Messages", Toast.LENGTH_SHORT).show();

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //read chatMessages
                if( snapshot.hasChildren() )
                {
                    for( DataSnapshot chatRoom : snapshot.getChildren() )
                    {

                        String thisChatKey = chatRoom.getKey();


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void addGroupMessages()
    {
        Toast.makeText( context, "Adding Group Messages", Toast.LENGTH_SHORT).show();

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if( snapshot.hasChildren() )
                {
                    for( DataSnapshot nthGroup : snapshot.getChildren() )
                    {

                        //check for this group any changes
                        String thisGroupKey = new String( nthGroup.getKey() );

                        ArrayList<msg> l = new ArrayList<>(10);
                        messages meso = new messages( context );
                        meso.open();
                        l = meso.getMessagesSentTo(thisGroupKey);
                        meso.close();

                        if( nthGroup.hasChildren() )
                        {

                            for( DataSnapshot message : nthGroup.getChildren() )
                            {
                                String content = "", sender = "", time = "";

                                if( message.hasChild( "content" ) )
                                {
                                    content = new String( message.child("content").getValue().toString() );
                                }
                                if( message.hasChild( "sender" ) )
                                {
                                    sender = new String( message.child( "sender" ).getValue().toString() );
                                }
                                if( message.hasChild("time") )
                                {
                                    time = new String( message.child( "time" ).getValue().toString() );
                                }

                                msg m = new msg( new String( sender ), new String( thisGroupKey) , new String( content ) , new String( time ) );
                                if( !contains( l, m) )
                                {
                                    l.add( m );
                                    meso.open();
                                    meso.addNewMessage( new String( m.sender ), new String( m.recipient ), new String( m.content ), new String( m.time ) );
                                    meso.close();
                                }

                            }

                        }



                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean contains( ArrayList<msg> list, msg value )
    {
        for( msg v : list )
        {
            if( v.content.equals( value.content ) && v.sender.equals( value.sender )
                    && v.recipient.equals( value.recipient ) && v.time.equals( value.time ) )
            {
                return true;
            }
        }

        return false;
    }


}


