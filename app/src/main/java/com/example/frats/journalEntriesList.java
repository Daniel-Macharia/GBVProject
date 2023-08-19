package com.example.frats;

import android.database.SQLException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class journalEntriesList extends AppCompatActivity {
    ListView l;
    @Override
    protected  void onCreate(  Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_entries_list);

        l = findViewById(R.id.entriesID);

        try {
            journalEntry e = new journalEntry(journalEntriesList.this);
            e.open();

            ArrayList<String[]> data = e.getEntries();
            //entryLayout arr[] = new entryLayout[data.size()];
            String arr[] = new String[data.size()];

            for(int i = 0; i < arr.length; i++)
            {
                //arr[i].tv.setText( data.get(i)[2] );//set text view`s text to the journal entry
                arr[i] = data.get(i)[2];
            }

            //ArrayAdapter<entryLayout> arrAdapt = new ArrayAdapter<entryLayout>(journalEntriesList.this,R.layout.entry_layout, arr);
            ArrayAdapter<String> arrAdapt = new ArrayAdapter<String>(journalEntriesList.this,R.layout.entry_layout,R.id.entry, arr);
            l.setAdapter(arrAdapt);

            e.close();
        }catch(SQLException ex)
        {
            Toast.makeText(journalEntriesList.this,ex.toString(),Toast.LENGTH_SHORT).show();
        }

    }
}
