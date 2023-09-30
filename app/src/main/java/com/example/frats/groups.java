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

public class groups extends AppCompatActivity {

    ListView groupList;

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

                    Intent groupChatRoomIntent = new Intent( groups.this, groupChat.class );
                    groupChatRoomIntent.putExtra("groupName", groupNames.get( i ) );
                    startActivity( groupChatRoomIntent );

                }
            });

        }catch( Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private void initGroupNames()
    {
        groupNames.add("Rape");
        groupNames.add("Female Genital Mutilation (FGM)");
        groupNames.add("Physical Violence");
        groupNames.add("Trafficking");
        groupNames.add("other Forms of Violence");
    }

}
