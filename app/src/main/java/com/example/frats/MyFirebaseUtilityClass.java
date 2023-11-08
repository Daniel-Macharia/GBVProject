package com.example.frats;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Pair;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

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
import java.util.StringJoiner;

public class MyFirebaseUtilityClass {

    public static boolean isConnectedToNetwork(Context context)
    {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if( networkInfo == null )
        {
            //Toast.makeText(context, "Not Connected to Network", Toast.LENGTH_SHORT).show();
        }
        else if( networkInfo.isAvailable() )
        {
            //Toast.makeText(context, "Connected to Network\n\n" , Toast.LENGTH_SHORT).show();
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

    public static void loadAssistantsOrUsers( Context context, String url, String contact)
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

                    //Toast.makeText(context, "Finished Loading users", Toast.LENGTH_SHORT).show();

                }

            });

        } else if (url.equals("users")) {
            //load users for this assistant

           // user u = new user(context);
            //u.open();
            //String[] userOrAssistant = u.readData();
            //u.close();

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

                                    if( contact.equals( participants[0]) ||
                                            contact.equals(participants[1]) )
                                    {

                                        /* myRef.child( roomSnapShot.getKey() ).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                valueChangedForThisChatRoom( snapshot, context, ( userOrAssistant[2].equals( participants[0] ) ? participants[1] : participants [0] ) );
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        }); */

                                        //get this message since user is among the participants
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

                                                    if( newMessage.recipient.equals( contact ) )
                                                        message.getRef().removeValue();

                                                }
                                            }
                                        }

                                        String theirNumber = new String();
                                        if( contact.equals( participants[0] ) )
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
                                        String username = "", phoneNumber = "";

                                        if( userData.hasChild("username") )
                                        {
                                            username = userData.child("username").getValue().toString();
                                        }
                                        if( userData.hasChild("phone") )
                                        {
                                            phoneNumber = userData.child("phone").getValue().toString();
                                        }

                                        if( !list.contains( phoneNumber) && newChatNumbers.contains( phoneNumber ) )
                                        {
                                            //newList.add( new userOrAssistant( new String( username ), new String( phone )));

                                            user us = new user( context);
                                            us.open();
                                            us.createUser( new String(username),
                                                    new String( phoneNumber ),
                                                    new String(phoneNumber),
                                                    new String("users"),
                                                    0 );
                                            us.close();
                                        }

                                    }
                                }
                            }

                        });

                        //Toast.makeText(context, "Finished adding my survivors to local db", Toast.LENGTH_SHORT).show();
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
                   // Toast.makeText(context, "Received new message: " + meso.getMessage(), Toast.LENGTH_SHORT).show();
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
                       // Toast.makeText(context, "New Group", Toast.LENGTH_SHORT).show();
                        int newMessageCount = 0;
                        String groupKey = group.getKey();
                        ArrayList<chatMessage> result = getGroupMessagesFromLocalDb( context, groupKey, myPhone);

                        if( group.hasChild("message") )
                        {
                            if( group.child("message").hasChildren() )
                            {
                                for( DataSnapshot message : group.child("message").getChildren() )
                                {
                                    //Toast.makeText(context, "new message", Toast.LENGTH_SHORT).show();

                                   // if( message.hasChildren() )
                                   // {

                                      //  for( DataSnapshot data : message.getChildren() )
                                      //  {
                                            String content = new String( "" );
                                            String sender = new String( "" );
                                            String time = new String( "" );
                                            int g ;

                                            if( message.hasChild( "content" ) )
                                            {
                                                content = message.child( "content" ).getValue().toString();
                                            }
                                            if( message.hasChild( "sender" ) )
                                            {
                                                sender = message.child( "sender" ).getValue().toString();
                                            }
                                            if( message.hasChild( "time" ) )
                                            {
                                                time = message.child( "time" ).getValue().toString();
                                            }

                                            if( sender.equals("") || content.equals("") || time.equals("") )
                                                continue;

                                            if( sender.equals(myPhone) )
                                                g = Gravity.END;
                                            else
                                                g = Gravity.START;

                                            chatMessage m = new chatMessage( new String(content), new String(time), g);

                                            if( !containsMessage( result, m ) )
                                            {
                                                result.add( new chatMessage( new String(content), new String(time), g) );
                                                newMessageCount++;
                                                //insert into the local db
                                                messages messo = new messages( context );
                                                messo.open();
                                                messo.addNewMessage( new String(sender), new String(groupKey), new String(content), new String(time) );
                                                messo.close();
                                                //updateMessageCount(context, groupKey, 1);
                                               // Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
                                               // Toast.makeText(context, "new group Message to " + groupKey, Toast.LENGTH_SHORT).show();
                                            }
                                       // }
                                    //}

                                    //chatMessageAdapter adapter = new chatMessageAdapter(groupChat.this, result);
                                    //chatList.setAdapter( adapter );

                                    //notifyOfMessage();
                                    //loadFromLocalDb();
                                }
                            }
                        }

                        if( newMessageCount > 0 )
                            updateMessageCount(context, groupKey, newMessageCount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private static void updateMessageCount( Context context, String chatID, int newMessageCount)
    {
        try{
            NewMessageCounter nmc = new NewMessageCounter( context );
            nmc.open();
            int current = nmc.getCount( chatID );
            if( current == -1 )
                nmc.addChat(chatID);

            nmc.setCount( chatID, current + newMessageCount);
            postNotification( context,  getNotificationID(chatID), chatID,current + newMessageCount + " New Messages From " + chatID);


            nmc.close();

        }catch ( Exception e )
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private static int getNotificationID( String chatID )
    {
        int id = 1000;

        try{
            int n = Integer.parseInt( chatID );
            id += n;
        }catch( NumberFormatException e )
        {
            //when it's a string
            int n = chatID.length();
            id += n;
        }

        return id;
    }



    public static void checkIfUserExists(Context context, String username, String phone)
    {
        try{


            if( !isConnectedToNetwork(context) )
            {
                Toast.makeText(context, "Network not available\nCheck your network connection and try again!", Toast.LENGTH_SHORT).show();
                //return null;
            }else{
                Toast.makeText(context, "Network available\nPlease wait a moment", Toast.LENGTH_SHORT).show();

            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users");
            DatabaseReference assistantRef = database.getReference("assistant");

            usersRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {

                    if( dataSnapshot.hasChildren() )
                    {
                        for( DataSnapshot user : dataSnapshot.getChildren() )
                        {
                            String name = "", number = "";

                            if( user.hasChild("username") )
                            {
                                name = user.child("username").getValue().toString();
                            }
                            if( user.hasChild("phone") )
                            {
                                number = user.child("phone").getValue().toString();
                            }

                            if( username.equals(name) && phone.equals(number) )
                            {
                                Toast.makeText(context, "user found", Toast.LENGTH_SHORT).show();
                                login.isUserOrAssistant = "users";
                                login.found = true;
                            }
                        }
                    }

                }

            });

            assistantRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {

                    if( dataSnapshot.hasChildren() )
                    {
                        for( DataSnapshot user : dataSnapshot.getChildren() )
                        {
                            String name = "", number = "";

                            if( user.hasChild("username") )
                            {
                                name = user.child("username").getValue().toString();
                            }
                            if( user.hasChild("phone") )
                            {
                                number = user.child("phone").getValue().toString();
                            }

                            if( username.equals(name) && phone.equals(number) )
                            {
                                Toast.makeText(context, "assistant found", Toast.LENGTH_SHORT).show();
                                login.isUserOrAssistant = "assistant";
                                login.found = true;
                            }
                        }
                    }

                }

            });

        }catch( Exception e )
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public static ArrayList<String> findUser()
    {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        ArrayList<String> list = new ArrayList<>(10);

        usersRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                if( dataSnapshot.hasChildren() )
                {
                    for( DataSnapshot user : dataSnapshot.getChildren() )
                    {
                        String number = "";

                        if( user.hasChild("phone") )
                        {
                            number = user.child("phone").getValue().toString();
                        }

                        if( !number.equals("") )
                            list.add( new String( number ));



                    }
                }

               MainActivity.hasFinishedSearchingUser = true;

            }

        });

        return list;
    }

    public static ArrayList<String> findAssistant()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference assistantRef = database.getReference("assistant");
        ArrayList<String> list = new ArrayList<>(10);

        assistantRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                if( dataSnapshot.hasChildren() )
                {
                    for( DataSnapshot user : dataSnapshot.getChildren() )
                    {
                        String number = "";

                        if( user.hasChild("phone") )
                        {
                            number = user.child("phone").getValue().toString();
                        }

                        if( !number.equals("") )
                            list.add( new String(number) );


                    }
                }

                MainActivity.hasFinishedSearchingAssistant = true;

            }

        });

        return list;
    }

    public static ArrayList<Pair<String, String>> findUserData()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");
        ArrayList<Pair<String, String>> userList = new ArrayList<>(10);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot result = task.getResult();

                if( result.hasChildren() )
                {
                    for( DataSnapshot user : result.getChildren() )
                    {
                        String username = "", phone = "";

                        if( user.hasChild("username") )
                        {
                            username = user.child("username").getValue().toString();
                        }
                        if( user.hasChild( "phone" ) )
                        {
                            phone = user.child( "phone" ).getValue().toString();
                        }

                        if( !username.equals("") && !phone.equals("") )
                            userList.add( new Pair<String, String>( new String(username), new String(phone) ) );
                    }
                }

                login.finishedGettingUser = true;
            }
        });


        return userList;
    }

    public static ArrayList<Pair<String, String>> findAssistantData()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("assistant");
        ArrayList<Pair<String, String>> assistantList = new ArrayList<>(10);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot result = task.getResult();

                if( result.hasChildren() )
                {
                    for( DataSnapshot assistant : result.getChildren() )
                    {
                        String username = "", phone = "";

                        if( assistant.hasChild("username") )
                        {
                            username = assistant.child("username").getValue().toString();
                        }
                        if( assistant.hasChild( "phone" ) )
                        {
                            phone = assistant.child( "phone" ).getValue().toString();
                        }

                        if( !username.equals("") && !phone.equals("") )
                            assistantList.add( new Pair<String, String>( new String(username), new String(phone) ) );
                    }
                }
                login.finishedGettingAssistant = true;
            }
        });


        return assistantList;
    }

    public static void updateAllGroups(Context context)
    {

        try{
            user u = new user( context );
            u.open();
            String myPhone = u.readData()[2];
            u.close();
            loadGroupChats(context, myPhone);
             /* FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference groupRef = database.getReference("group");

            groupRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postNotification(context, 10,"new group messages", "check application to see group messages");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }); */
        }catch( Exception e )
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private static ArrayList<chatMessage> loadChatMessage( Context context, String myPhone, String recipient)
    {
        ArrayList<chatMessage> result = new ArrayList<>(10);

        ArrayList<msg> ms = new ArrayList<>(10);

        messages messageDB = new messages(context);
        messageDB.open();
        ms = messageDB.getMessagesSentTo(recipient, myPhone);
        messageDB.close();

        for( msg message : ms )
        {
            int g = ( message.sender.equals( myPhone ) ? Gravity.END : Gravity.START );
            result.add( new chatMessage( message.content, message.time, g) );
        }

        return result;
    }

    public static void updateAllChats(Context context)
    {
        try{

            ArrayList< Pair<String, DatabaseReference > > recipientChatRefPairList = new ArrayList<>(10);

            ArrayList<String[]> data;
            String[] thisUserData = new String[4];
            user u = new user(context);
            u.open();
            data = u.getUsers();
            thisUserData = u.readData();
            u.close();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference chatRef = database.getReference("chat_room");

            String myPhone = new String( thisUserData[2] );

            chatRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    //Toast.makeText(context, "Getting chats", Toast.LENGTH_SHORT).show();
                    DataSnapshot chatRooms = task.getResult();

                    for( String[] currentUser : data )
                    {
                        String recipient = new String(currentUser[1]);
                        if( chatRooms.hasChildren() )
                        {
                            for( DataSnapshot room :chatRooms.getChildren() )
                            {
                                //String roomRec = new String( recipient );

                                if( room.hasChild("participants") )
                                {
                                    String p1 = "", p2 = "";
                                    if( room.child("participants").hasChild("p1") )
                                    {
                                        p1 = room.child("participants").child("p1").getValue().toString();
                                    }
                                    if( room.child("participants").hasChild("p2") )
                                    {
                                        p2 = room.child("participants").child("p2").getValue().toString();
                                    }

                                    if( (p1.equals( myPhone ) && p2.equals(recipient) )
                                            || ( p2.equals( myPhone ) && p1.equals(recipient) ) )
                                    {
                                        recipientChatRefPairList.add( new Pair<>( new String( recipient ), room.child("messages").getRef() ) );

                                    }

                                }


                            }
                        }
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                     for( Pair< String, DatabaseReference > pair : recipientChatRefPairList )
                     {
                         pair.second.addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {

                                 String newRecipient = new String(pair.first);
                                 String newMyPhone = new String(myPhone);

                                 ArrayList<chatMessage> result = loadChatMessage( context, newMyPhone, newRecipient);
                                 int count = 0;
                                 if( snapshot.hasChildren() )
                                 {
                                     for( DataSnapshot message : snapshot.getChildren() )
                                     {
                                         String content = "", sender = "", rec = "", time ="";

                                         if( message.hasChild("content") )
                                         {
                                             content = message.child("content").getValue().toString();
                                         }
                                         if( message.hasChild("time") )
                                         {
                                             time = message.child("time").getValue().toString();
                                         }
                                         if( message.hasChild("sender") )
                                         {
                                             sender = message.child("sender").getValue().toString();
                                         }
                                         if( message.hasChild("recipient") )
                                         {
                                             rec = message.child("recipient").getValue().toString();
                                         }

                                         int g = Gravity.START;

                                         if( sender.equals(newMyPhone) )
                                             g = Gravity.END;

                                         if( sender.equals("") || rec.equals("") )
                                             continue;

                                         chatMessage meso = new chatMessage( new String(content), new String(time), g);
                                         if( !containsMessage( result, meso) )
                                         {
                                             //update message counter
                                             count++;
                                             //add this message to messages SQLite Database table
                                             messages m = new messages( context );
                                             m.open();
                                             m.addNewMessage( sender, rec, content, time );
                                             m.close();
                                             Toast.makeText(context, "Adding Message: " +
                                                     sender + " to " + rec + "\n" + content, Toast.LENGTH_SHORT).show();
                                         }

                                     }
                                     if( count > 0)
                                     {
                                         updateMessageCount(context, newRecipient, count);
                                     }

                                 }

                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {

                             }
                         });
                     }
                }
            });


        }catch( Exception e )
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void setListenerOnChat( DatabaseReference chatRoomRef, Context context, String myPhone, String recipient)
    {
        chatRoomRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            String newMyPhone = new String( myPhone );
            String newRecipient = new String( recipient );
            ArrayList<chatMessage> result = loadChatMessage( context, newMyPhone, newRecipient);

            if( snapshot.hasChildren() )
            {
                int count = 0;
                for( DataSnapshot message : snapshot.getChildren() )
                {
                    String content = "", sender = "", rec = "", time ="";

                    if( message.hasChild("content") )
                    {
                        content = message.child("content").getValue().toString();
                    }
                    if( message.hasChild("time") )
                    {
                        time = message.child("time").getValue().toString();
                    }
                    if( message.hasChild("sender") )
                    {
                        sender = message.child("sender").getValue().toString();
                    }
                    if( message.hasChild("recipient") )
                    {
                        rec = message.child("recipient").getValue().toString();
                    }

                    int g = Gravity.START;
                    if( sender.equals(newMyPhone) )
                        g = Gravity.END;

                    if( sender.equals("") || rec.equals("") )
                        continue;

                    chatMessage meso = new chatMessage(content, time, g);
                    if( !containsMessage( result, meso) )
                    {
                        //update message counter
                        count++;
                        messages m = new messages( context );
                        m.open();
                        m.addNewMessage( sender, rec, content, time);
                        m.close();

                    }

                }

                if( count > 0)
                {
                    updateMessageCount(context, newRecipient, count);
                }

            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void postNotification( Context context, int id, String title, String text)
    {
        String channelId = "thisChannelId";
        String channelName = "thisChannelName";
        NotificationManager manager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT );
            manager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder( context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon( R.mipmap.icon );

        if(ActivityCompat.checkSelfPermission( context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED )
        {
            manager.notify(id, builder.build() );
        }else{
           // ActivityCompat.requestPermissions( context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1 );
        }
    }


    public static boolean validatePhone(Context context, String phone)
    {
        if( phone.length() == 10 )
        {
            if( phone.startsWith("07") || phone.startsWith("01") )
            {
                try{
                    int number = Integer.parseInt( phone );

                }catch( NumberFormatException e )
                {
                    return false;
                }catch( Exception e )
                {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        }


        return false;
    }

    public static void updateUsername( Context context, String name , String phone )
    {
        try{
            if( isConnectedToNetwork(context) )
            {
                if( isUser(context) )
                {
                    //update users
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference userRef = database.getReference("users");

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot result = task.getResult();

                            String key = "";

                            if ( result.hasChildren() )
                            {
                                for( DataSnapshot user : result.getChildren() )
                                {
                                    String username = "", phoneNumber = "";

                                    if( user.hasChild("username"))
                                    {
                                        username = user.child("username").getValue().toString();
                                    }
                                    if( user.hasChild("phone"))
                                    {
                                        phoneNumber = user.child("phone").getValue().toString();
                                    }

                                    if( username.equals(name) && phoneNumber.equals(phone) )
                                    {
                                        //updateGroupMessagesWhereSenderIs( null, phoneNumber, username);
                                        //if( user.hasChild("username") )
                                        key = user.getKey();
                                        break;
                                    }
                                }
                            }

                            if( !key.equals("") )
                            {
                                userRef.child(key).child("username").setValue(name);
                            }

                        }
                    });
                }
                else
                {
                    //update assistants
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference assistantRef = database.getReference("assistant");

                    assistantRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot result = task.getResult();

                            String key = "";

                            if ( result.hasChildren() )
                            {
                                for( DataSnapshot assistant : result.getChildren() )
                                {
                                    String username = "", phoneNumber = "";

                                    if( assistant.hasChild("username"))
                                    {
                                        username = assistant.child("username").getValue().toString();
                                    }
                                    if( assistant.hasChild("phone"))
                                    {
                                        phoneNumber = assistant.child("phone").getValue().toString();
                                    }

                                    if( username.equals(name) && phoneNumber.equals(phone) )
                                    {
                                        //  updateGroupMessagesWhereSenderIs(null, phoneNumber, username);
                                        //if( assistant.hasChild("username"))
                                        key = assistant.getKey();
                                        break;
                                    }
                                }
                            }

                            if( !key.equals("") )
                            {
                                assistantRef.child(key).child("username").setValue(name);
                            }

                        }
                    });
                }
            }
            else
            {
                //schedule job for some later time
            }
        }catch( Exception e )
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void updatePhone( Context context, String oldContact, String newContact)
    {
        try{
            if( isConnectedToNetwork(context) )
            {
                if( isUser(context) )
                {
                    //update users
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference userRef = database.getReference("users");

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot result = task.getResult();

                            String key = "";

                            if ( result.hasChildren() )
                            {
                                for( DataSnapshot user : result.getChildren() )
                                {
                                    String phoneNumber = "";

                                    if( user.hasChild("phone"))
                                    {
                                        phoneNumber = user.child("phone").getValue().toString();
                                    }

                                    if( phoneNumber.equals(oldContact) )
                                    {
                                        //updateGroupMessagesWhereSenderIs(null, contact, username);
                                        key = user.getKey();
                                        break;
                                    }
                                }
                            }

                            if( !key.equals("") )
                            {
                                userRef.child(key).child("phone").setValue(newContact);
                                // updateGroupMessagesWhereSenderIs(contact);
                                // updateContactToAllMyChats(contact);
                            }

                        }
                    });
                }
                else
                {
                    //update assistants
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference assistantRef = database.getReference("users");

                    assistantRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            DataSnapshot result = task.getResult();

                            String key = "";

                            if ( result.hasChildren() )
                            {
                                for( DataSnapshot assistant : result.getChildren() )
                                {
                                    String phoneNumber = "";

                                    if( assistant.hasChild("phone"))
                                    {
                                        phoneNumber = assistant.child("phone").getValue().toString();
                                    }

                                    if( phoneNumber.equals(oldContact) )
                                    {
                                        //updateGroupMessagesWhereSenderIs(null, contact, username);
                                        key = assistant.getKey();
                                        break;
                                    }
                                }
                            }

                            if( !key.equals("") )
                            {
                                assistantRef.child(key).child("contact").setValue(newContact);
                                //updateGroupMessagesWhereSenderIs(contact);
                                //updateContactToAllMyChats(contact);
                            }

                        }
                    });
                }
            }
            else
            {
                //schedule job for some later time
            }
        }catch( Exception e )
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isUser( Context context )
    {
        user u = new user( context );
        u.open();
        String[] data = u.readData();
        u.close();

        if( data[3].equals("assistant") )
            return false;

        return true;
    }

    public static void updateGroupMessagesWhereSenderIs( Context context, String oldContact, String sender, String name )
    {
        try{
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference groupRef = database.getReference("group");

            groupRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    DataSnapshot result = task.getResult();

                    if( result.hasChildren() )
                    {
                        for( DataSnapshot group : result.getChildren() )
                        {
                            if( group.hasChild("member") )
                            {
                                if( group.child("member").hasChildren() )
                                {
                                    for( DataSnapshot member : group.child( "member" ).getChildren() )
                                    {
                                        String username = "", phone= "";

                                        if( member.hasChild("username") )
                                        {
                                            username = member.child("username").getValue().toString();
                                        }
                                        if( member.hasChild("phone") )
                                        {
                                            phone = member.child("phone").getValue().toString();
                                        }


                                        if( oldContact == null )
                                        {
                                            if( username.equals(name) && phone.equals(sender) )
                                            {
                                                member.getRef().child("username").setValue(name);
                                            }
                                        }
                                        else
                                        {
                                            if( phone.equals( oldContact) )
                                            {
                                                DatabaseReference ref = member.getRef();
                                                ref.child("phone").setValue(sender);
                                                ref.child("username").setValue(name);
                                                if( group.hasChild("message") )
                                                    updateGroupMessages( context,group.child("message").getRef(), oldContact, sender);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }catch( Exception e )
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateContactToAllMyChats( Context context, String oldContact, String newContact )
    {
       try{
           FirebaseDatabase database = FirebaseDatabase.getInstance();
           DatabaseReference chatRoomRef = database.getReference("chat_room");

           chatRoomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DataSnapshot> task) {
                   DataSnapshot result = task.getResult();

                   if( result.hasChildren() )
                   {
                       for( DataSnapshot room : result.getChildren() )
                       {
                           if( room.hasChild("participants") )
                           {
                               String p1 = "", p2 ="";

                               if( room.child("participants").hasChild("p1") )
                               {
                                   p1 = room.child("participants").child("p1").getValue().toString();
                               }

                               if( room.child("participants").hasChild("p2") )
                               {
                                   p2 = room.child("participants").child("p2").getValue().toString();
                               }

                               if( p1.equals(oldContact))
                               {
                                   room.child("participants").child("p1").getRef().setValue(newContact);
                                   if( room.hasChild("message") )
                                       updateChatMessages( context, room.child("messages").getRef(), oldContact, newContact );
                               }
                               else if( p2.equals(oldContact) )
                               {
                                   room.child("participants").child("p2").getRef().setValue(newContact);
                                   if( room.hasChild("message") )
                                       updateChatMessages( context, room.child("messages").getRef(), oldContact, newContact );
                               }

                           }
                       }
                   }
               }
           });
       }catch( Exception e )
       {
           Toast.makeText( context, e.toString(), Toast.LENGTH_SHORT).show();
       }
    }

    private static void updateChatMessages( Context context, DatabaseReference messages, String oldContact, String newContact )
    {
       try{
           messages.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DataSnapshot> task) {
                   DataSnapshot result = task.getResult();
                   if( result.hasChildren() )
                   {
                       for( DataSnapshot message : result.getChildren() )
                       {
                           String sender = "", recipient = "";
                           if( message.hasChild("sender") )
                           {
                               sender = message.child("sender").getValue().toString();
                           }
                           if( message.hasChild("recipient") )
                           {
                               recipient = message.child("recipient").getValue().toString();
                           }

                           if( sender.equals( oldContact ) )
                           {
                               message.child("sender").getRef().setValue(newContact);
                           }
                           if( recipient.equals( oldContact ) )
                           {
                               message.child("recipient").getRef().setValue(newContact);
                           }
                       }
                   }
               }
           });
       }catch( Exception e )
       {
           Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
       }
    }

    private static void updateGroupMessages( Context context, DatabaseReference messages, String oldContact, String newContact )
    {
       try{
           messages.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DataSnapshot> task) {
                   DataSnapshot result = task.getResult();
                   if( result.hasChildren() )
                   {
                       for( DataSnapshot message : result.getChildren() )
                       {
                           String sender = "";
                           if( message.hasChild("sender") )
                           {
                               sender = message.child("sender").getValue().toString();
                           }

                           if( sender.equals( oldContact ) )
                           {
                               message.child("sender").getRef().setValue(newContact);
                           }
                       }
                   }
               }
           });
       }catch( Exception e )
       {
           Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
       }
    }

}
