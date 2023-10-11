package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import android.util.Pair;

public class groups extends AppCompatActivity {

    ListView groupList;
   // public static Map<String, String> group;
    public static ArrayList<Pair<String, String> > group = new ArrayList<Pair<String, String>>(4);

    ArrayList<String> groupNames = new ArrayList<>(10);

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {


        try{
            super.onCreate( savedInstanceState );
            setContentView( R.layout.groups );
            groupList = findViewById( R.id.groupList );

            initGroupNames();

            ArrayAdapter<String> arr = new ArrayAdapter<>(groups.this, R.layout.group_item, R.id.groupName, groupNames);
            groupList.setAdapter(arr);

            groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if( !allowedAccessToGroup() )
                    {
                        Toast.makeText(groups.this, "Please request an 'Assistant' for access to the group", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent groupChatRoomIntent = new Intent( groups.this, groupChat.class );
                    groupChatRoomIntent.putExtra("index", i );
                    startActivity( groupChatRoomIntent );

                }
            });

        }catch( Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean allowedAccessToGroup()
    {
        user u = new user(getApplication());
        u.open();
        String[] data = u.readData();
        u.close();

        //only assistant is allowed direct access to the group chat
        //users must request assistants for permission
        if( data[3].equals("assistant") )
            return true;
        else
            return false;
    }

    private void initGroupNames()
    {
        group.add( new Pair("rape", "Rape") );
        group.add( new Pair("fgm", "Female Genital Mutilation (FGM)") );
        group.add( new Pair( "physical", "Physical Violence") );
        group.add( new Pair("trafficking", "Trafficking") );
        group.add( new Pair( "other", "Other Forms of Violence") );

        for( Pair<String, String> val: group)
        {
            groupNames.add( new String( val.second ) );
        }

    }

}
