package com.example.frats;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

            e.close();

            //entryLayout arr[] = new entryLayout[data.size()];
            final int s = data.size();
            String arr[] = new String[s];
            //int j = s - 1;

            for(int i = 0, j = s - 1; i < s; i++, j--)
            {
                //arr[i].tv.setText( data.get(i)[2] );//set text view`s text to the journal entry
                arr[j] = data.get(i)[0] + "\n" + data.get(i)[1] + "\n\n" + data.get(i)[2];
            }

            //ArrayAdapter<entryLayout> arrAdapt = new ArrayAdapter<entryLayout>(journalEntriesList.this,R.layout.entry_layout, arr);
            ArrayAdapter<String> arrAdapt = new ArrayAdapter<String>(journalEntriesList.this,R.layout.entry_layout,R.id.entry, arr);
            l.setAdapter(arrAdapt);

            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    int max = data.size() - 1;
                    int index = max - i;
                    String entryHeader = "MyNotes \\ " + data.get(index)[0] + " \\ " + data.get(index)[1];
                    String fullEntry = data.get( index )[2];

                    Bundle b = new Bundle();
                    b.putString("entry", fullEntry );
                    b.putString("header", entryHeader );
                    //do absolutely nothing
                    Intent fullJournalEntryIntent = new Intent( journalEntriesList.this, showFullJournalEntry.class );

                    fullJournalEntryIntent.putExtra("data", b);

                    startActivity( fullJournalEntryIntent );
                }
            });


        }catch(SQLException ex)
        {
            Toast.makeText(journalEntriesList.this,ex.toString(),Toast.LENGTH_SHORT).show();
        }

    }
}
