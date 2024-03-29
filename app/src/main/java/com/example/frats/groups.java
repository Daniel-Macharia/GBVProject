package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import android.util.Pair;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class groups extends AppCompatActivity {

    ListView groupList;
   // public static Map<String, String> group;
    public static ArrayList<Pair<String, String> > group = new ArrayList<Pair<String, String>>(4);
    String myPhone = new String("");

    ArrayList<String> groupNames = new ArrayList<>(10);
    GroupItemAdapter adapter;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {


        try{
            super.onCreate( savedInstanceState );
            setContentView( R.layout.groups );
            groupList = findViewById( R.id.groupList );

            initGroupNames();
            initMyPhone();
            //getNewMessageCount();

            //ArrayAdapter<String> arr = new ArrayAdapter<>(groups.this, R.layout.group_item, R.id.groupName, groupNames);
            //groupList.setAdapter(arr);
            adapter = new GroupItemAdapter(this, group);
            groupList.setAdapter(adapter);

            groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                   /*  checkIfMember(group.get(i).first); .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            Toast.makeText(groups.this, "finished checking if user is member of group " + group.get(i).first, Toast.LENGTH_SHORT).show();
                        }
                    }); */

                    if( !allowedAccessToGroup(group.get(i).first) )
                    {
                        Toast.makeText(groups.this, "Please request an 'Assistant' for access to the group", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent groupChatRoomIntent = new Intent( groups.this, groupChat.class );
                    groupChatRoomIntent.putExtra("index", i );
                    startActivity( groupChatRoomIntent );
                    NewMessageCounter nmc = new NewMessageCounter(getApplicationContext());
                    nmc.open();
                    nmc.setCount( group.get(i).first, 0);
                    nmc.close();

                }
            });

        }catch( Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void initMyPhone()
    {
        user u = new user( this );
        u.open();
        myPhone = u.readData()[2];
        u.close();
    }

    private boolean checkIfMember(String groupKey)
    {
        userGroupAccessPermissions permissions = new userGroupAccessPermissions(groups.this);
        permissions.open();
        boolean isAllowedAccess = permissions.isAllowedAccessTo(groupKey);
        permissions.close();

        return isAllowedAccess;

        //Toast.makeText(this, "Clicked " + groupKey, Toast.LENGTH_SHORT).show();
       /* FirebaseDatabase db = FirebaseDatabase.getInstance();

        DatabaseReference dbRef = db.getReference("group");

        dbRef.child(groupKey).child("member").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot result = task.getResult();
                if( result.hasChildren() )
                {
                    for( DataSnapshot member : result.getChildren() )
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
                               userGroupAccessPermissions permissions = new userGroupAccessPermissions(groups.this);
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
                               Toast.makeText( groups.this, e.toString(), Toast.LENGTH_SHORT ).show();
                           }


                        }
                    }
                }
            }
        }); */

    }

    private boolean allowedAccessToGroup( String groupKey)
    {
        user u = new user(getApplication());
        u.open();
        String[] data = u.readData();
        u.close();

        if( data[3].equals("assistant") )
            return true;


        boolean isAllowed = false;
        try{
            userGroupAccessPermissions permissions = new userGroupAccessPermissions(groups.this);
            permissions.open();
            isAllowed = permissions.isAllowedAccessTo(groupKey);
            permissions.close();
        }catch( Exception e )
        {
            Toast.makeText(groups.this, e.toString(), Toast.LENGTH_SHORT).show();
        }


        return isAllowed;
    }

    private void initGroupNames()
    {
        if( group.isEmpty() )
        {
            group.add( new Pair("rape", "Rape") );
            group.add( new Pair("fgm", "Female Genital Mutilation (FGM)") );
            group.add( new Pair( "physical", "Physical Violence") );
            group.add( new Pair("trafficking", "Trafficking") );
            group.add( new Pair( "other", "Other Forms of Violence") );
        }

        for( Pair<String, String> val: group)
        {
            groupNames.add( new String( val.second ) );
        }

    }

    private void getNewMessageCount()
    {
        String r = "";

        NewMessageCounter nmc = new NewMessageCounter(this);
        nmc.open();
        for( Pair<String, String> val : group )
        {
            r += val.first + nmc.getCount( val.first ) + "\n";
        }
        nmc.close();

        Toast.makeText(this, "New Messages Are: " + r, Toast.LENGTH_SHORT).show();
    }

}
