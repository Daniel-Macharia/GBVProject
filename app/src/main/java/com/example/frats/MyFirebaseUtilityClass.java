package com.example.frats;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

public class MyFirebaseUtilityClass {

    private static boolean isConnectedToNetwork(Context context)
    {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if( networkInfo == null )
        {
            Toast.makeText(context, "Not Connected to Network", Toast.LENGTH_SHORT).show();
        }
        else if( networkInfo.isAvailable() )
        {
            Toast.makeText(context, "Connected to Network\n\n" + networkInfo.getReason(), Toast.LENGTH_SHORT).show();
            return true;
        }


        return false;
    }

    public static void addNewUser(String url, newUser u)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(url);
        myRef.push().setValue(u);
    }

    public static void addToListOfParticipantsOfAllGroups(String url, Context context, String username, String phone)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(url);

        groupParticipant p = new groupParticipant( new String( username), new String(phone) );

        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                // Toast.makeText(createUser.this, "getting groups", Toast.LENGTH_SHORT).show();

                DataSnapshot groups = task.getResult();

                if( groups.hasChildren() )
                {
                    for( DataSnapshot group : groups.getChildren() )
                    {
                        String thisGroupKey = group.getKey();

                        if( group.hasChild("member" ) )
                        {
                            //Toast.makeText(createUser.this, "there exists members in this group", Toast.LENGTH_SHORT).show();
                            boolean isInGroup = false;

                            if( group.child("member").hasChildren() )
                            {
                                //Toast.makeText(createUser.this, "getting groups", Toast.LENGTH_SHORT).show();

                                ArrayList<String> membersPhones = new ArrayList<>(10);
                                for( DataSnapshot member : group.child("member").getChildren() )
                                {
                                    String name = "", contact = "";

                                    if( member.hasChild("usename") )
                                    {
                                        name = member.child("username").getValue().toString();
                                    }
                                    if( member.hasChild("phone") )
                                    {
                                        contact = member.child("phone").getValue().toString();
                                    }


                                    membersPhones.add( new String( contact ) );
                                }

                                if( !membersPhones.contains( p.phone ) )
                                {
                                    //add participant p to the group
                                    assert  thisGroupKey != null;
                                    dbRef.child(thisGroupKey).child("member").push().setValue(p);

                                }

                            }
                        }else {
                            //Toast.makeText(createUser.this, "Creating new members list\n"
                            //      + "key value is " + thisGroupKey, Toast.LENGTH_SHORT).show();
                            //create member node in the chat
                            //dbRef.child(thisGroupKey).push().setValue(new participants() );
                            // if( thisGroupKey == null )
                            //     Toast.makeText(createUser.this, "Group key is null", Toast.LENGTH_SHORT).show();
                            // else
                            try{
                                dbRef.child(thisGroupKey).child("member").push().setValue(p);
                            }catch( Exception e )
                            {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }




                    }
                }

            }

        });

    }

    private static boolean contains(ArrayList<String> list, String value)
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

    private static ArrayList<String> readFromLocalDB( Context context)
    {

        ArrayList<String[]> r = new ArrayList<>(10);

        user u = new user(context);
        u.open();
        r = u.getUsers();
        u.close();
        //String s = "";

        ArrayList<String> result = new ArrayList<>();
        for( String[] user : r )
        {
            result.add( new String( user[1] ));
        }

        return result;

    }

    public static void loadAssistantsOrUsers(String url, Context context)
    {
        isConnectedToNetwork(context);
        ArrayList<String> list = readFromLocalDB(context);

        if(url.equals( "assistant") )
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference myRef = database.getReference( url );
            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
            {
                @Override
                public void onComplete(Task<DataSnapshot> task)
                {
                    ArrayList<String> list = readFromLocalDB(context);

                    if( task.isSuccessful())
                    {
                        DataSnapshot snap = task.getResult();
                        //Iterable<DataSnapshot> iter = snap.getChildren();

                        if( snap.hasChildren() )
                        {
                            for( DataSnapshot s : snap.getChildren() )
                            {

                                String a[] = new String[2];
                                if(s.hasChild("username") )
                                    a[0] = s.child("username").getValue().toString();
                                if(s.hasChild("phone") )
                                    a[1] = s.child("phone").getValue().toString();

                                if( contains( list, a[1] ) )
                                {
                                    continue;
                                }

                                //list.add( a[1]);
                                //newList.add( new userOrAssistant( new String( a[0] ), new String( a[1] ) ) );

                                user u = new user(context);
                                u.open();
                                u.createUser( new String(a[0]), "", new String(a[1]),
                                        "", 0);
                                u.close();

                            }
                        }

                    }
                    else {
                        //if the task failed to complete successfully
                    }

                    Toast.makeText(context, "Finished Loading users", Toast.LENGTH_SHORT).show();

                }

            });

        } else if (url.equals("users")) {
            //load users for this assistant

            user u = new user(context);
            u.open();
            String[] userOrAssistant = u.readData();
            u.close();

            ArrayList<String> newChatNumbers = new ArrayList<>(10);

            try{
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                DatabaseReference myRef = database.getReference("chat_room");


                myRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        //ArrayList<String> list = readFromLocalDB(context);

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

                                        myRef.child( roomSnapShot.getKey() ).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                valueChangedForThisChatRoom( snapshot, context, ( userOrAssistant[2].equals( participants[0] ) ? participants[1] : participants [0] ) );
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

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
                                                    messages messo = new messages(context);
                                                    messo.open();
                                                    messo.addNewMessage( new String( newMessage.sender),
                                                            new String( newMessage.recipient ),
                                                            new String( newMessage.content),
                                                            new String( newMessage.time));
                                                    messo.close();

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

                                        if( !list.contains( theirNumber ) )
                                        {
                                            //list.add( new String( theirNumber ) );
                                            newChatNumbers.add( new String( theirNumber ) );

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
                        DatabaseReference usersReference = database.getReference("users");

                        usersReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {

                                //ArrayList<String> list = readFromLocalDB(context);

                                DataSnapshot users = task.getResult();

                                if( users.hasChildren() )
                                {
                                    for( DataSnapshot userData : users.getChildren() )
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

                                        if( !list.contains( phone) && newChatNumbers.contains( phone ) )
                                        {
                                            //newList.add( new userOrAssistant( new String( username ), new String( phone )));

                                            user us = new user( context);
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

                        Toast.makeText(context, "Finished adding my survivors to local db", Toast.LENGTH_SHORT).show();
                    }
                });


            }catch( Exception e)
            {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }


            //return mySurvivors;

        }

    }

    private static boolean containsMessage( ArrayList<chatMessage> list, chatMessage value) {

        for (chatMessage listItem : list) {
            if (listItem.getMessage().equals(value.getMessage()) && listItem.getTime().equals(value.getTime()))
                return true;
        }

        return false;
    }

    private static ArrayList<chatMessage> loadMessagesFromLocalDB(Context context, String[] data, String recipient) {
        ArrayList<msg> ms = new ArrayList<>(10);
        ArrayList<chatMessage> arr = new ArrayList<>(10);

        messages messageDB = new messages(context);
        messageDB.open();
        ms = messageDB.getMessagesSentTo(recipient, data[2]);
        messageDB.close();

        for (msg m : ms) {
            int g = (recipient.equals(m.recipient) ? Gravity.END : Gravity.START);
            chatMessage chatM = new chatMessage(m.content, m.time, g);

            if ( !containsMessage(arr, chatM) ) {
                arr.add(new chatMessage(m.content, m.time, g));
            }

        }

        return arr;
    }

    public static void valueChangedForThisChatRoom(DataSnapshot snapshot, Context context, String recipient)
    {
        user u = new user(context);
        u.open();
        String[] data = u.readData();
        u.close();

        ArrayList <chatMessage> arr = loadMessagesFromLocalDB(context, data, recipient);

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

                chatMessage meso = new chatMessage(new String(mess.content), new String(mess.time), g);
                if (!containsMessage(arr, meso)) {
                    Toast.makeText(context, "Received new message: " + meso.getMessage(), Toast.LENGTH_SHORT).show();
                    //arr.add(meso);
                    try {
                        messages ms = new messages(context);
                        ms.open();
                        ms.addNewMessage(mess.sender, mess.recipient, mess.content, mess.time);
                        ms.close();
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }

                if (mess.recipient.equals(data[2])) {
                    message.getRef().removeValue();
                }


            }


        }


        //chatMessageAdapter arrayAdapter = new chatMessageAdapter(context, arr);
        //chatList.setAdapter(arrayAdapter);
    }

    public static void addUserToGroup( String title, String username, String phone)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("group");
        dbRef.child(title).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot result = task.getResult();

                if( result.hasChild("member") )
                {
                    if( result.child("member").hasChildren() )
                    {
                        ArrayList<String> members = new ArrayList<>(10);
                        for( DataSnapshot member : result.child("member").getChildren() )
                        {
                            String phone = "";

                            if( member.hasChild("phone") )
                            {
                                phone = member.child("phone").getValue().toString();
                            }

                            members.add( phone );

                        }

                        if( !members.contains( phone ) )
                        {
                            groupParticipant p = new groupParticipant(username, phone);
                            dbRef.child(title).child("member").push().setValue(p);
                        }
                    }
                }
                else
                {
                    groupParticipant p = new groupParticipant(username, phone);
                    dbRef.child(title).child("member").push().setValue(p);
                }
            }

        });

    }

    public static void checkGroupsAllowed( Context context, String myPhone)
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference dbRef = db.getReference("group");

        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                DataSnapshot groups = task.getResult();

                if( groups.hasChildren() )
                {

                    for( DataSnapshot group : groups.getChildren() )
                    {
                        String groupKey = group.getKey();

                        if( group.hasChild("member") )
                        {
                            if( group.child("member").hasChildren() )
                            {
                                for( DataSnapshot member : group.child("member").getChildren() )
                                {
                                    String phone= new String("");

                                    if( member.hasChild("phone") )
                                    {
                                        phone = member.child("phone").getValue().toString();
                                    }

                                    if( myPhone.equals( phone ) )
                                    {
                                        try{
                                            // Toast.makeText(groups.this, "user is in this group", Toast.LENGTH_SHORT).show();
                                            //return from function
                                            //isMember[0] = true;
                                            userGroupAccessPermissions permissions = new userGroupAccessPermissions(context);
                                            permissions.open();

                                            if( permissions.isAllowedAccessTo(groupKey) )
                                            {
                                                // Toast.makeText(groups.this, "not adding permission to this group", Toast.LENGTH_SHORT).show();
                                                permissions.close();
                                                return;
                                            }
                                            else
                                            {
                                                // Toast.makeText(groups.this, "adding permission to this group", Toast.LENGTH_SHORT).show();
                                                permissions.insertPermission(groupKey, "allowed");
                                                permissions.close();
                                            }
                                        }catch( Exception e )
                                        {
                                            Toast.makeText( context, e.toString(), Toast.LENGTH_SHORT ).show();
                                        }


                                    }
                                }
                            }
                        }
                    }
                }

            }
        });

    }

    private static ArrayList<chatMessage> getGroupMessagesFromLocalDb(Context context, String groupKey, String myPhone)
    {

        ArrayList<chatMessage> result = new ArrayList<>(10);
        //String myPhone = new String("0712696965");

        messages m = new messages( context);
        m.open();
        ArrayList<msg> data = m.getMessagesSentTo(groupKey);
        m.close();

        //String s = "";
        for( msg message : data)
        {
            int gravity = message.sender.equals(myPhone) ? Gravity.END : Gravity.START;
            chatMessage s = new chatMessage( message.content, message.time, gravity);
            result.add( s );
        }

        return result;

    }

    public static void loadGroupChats(Context context, String myPhone )
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("group");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.hasChildren() )
                {
                    for( DataSnapshot group : snapshot.getChildren() )
                    {
                        String groupKey = group.getKey();

                        if( group.hasChild("message") )
                        {
                            if( group.child("message").hasChildren() )
                            {
                                for( DataSnapshot message : group.child("message").getChildren() )
                                {
                                    ArrayList<chatMessage> result = getGroupMessagesFromLocalDb( context, groupKey, myPhone);

                                    if( snapshot.hasChildren() )
                                    {

                                        for( DataSnapshot data : snapshot.getChildren() )
                                        {
                                            String content = new String( "" );
                                            String sender = new String( "" );
                                            String time = new String( "" );
                                            int g ;

                                            if( data.hasChild( "content" ) )
                                            {
                                                content = data.child( "content" ).getValue().toString();
                                            }
                                            if( data.hasChild( "sender" ) )
                                            {
                                                sender = data.child( "sender" ).getValue().toString();
                                            }
                                            if( data.hasChild( "time" ) )
                                            {
                                                time = data.child( "time" ).getValue().toString();
                                            }

                                            if( sender.equals(myPhone) )
                                                g = Gravity.END;
                                            else
                                                g = Gravity.START;

                                            chatMessage m = new chatMessage( new String(content), new String(time), g);

                                            if( !containsMessage( result, m ) )
                                            {
                                                result.add( new chatMessage( new String(content), new String(time), g) );
                                                //insert into the local db
                                                messages messo = new messages( context );
                                                messo.open();
                                                messo.addNewMessage( new String(sender), new String(groupKey), new String(content), new String(time) );
                                                messo.close();
                                            }
                                        }
                                    }

                                    //chatMessageAdapter adapter = new chatMessageAdapter(groupChat.this, result);
                                    //chatList.setAdapter( adapter );

                                    //notifyOfMessage();
                                    //loadFromLocalDb();
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

}
