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

    ArrayList<String[]> data = new ArrayList<>(10);
    @Override
    protected  void onCreate(  Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_entries_list);

        l = findViewById(R.id.entriesID);

        try {
              setListItems();
            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    int max = data.size() - 1;
                    int index = max - i;
                    String entryHeader = "MyNotes \\ " + data.get(index)[0] + " \\ " + data.get(index)[1];
                    String date = data.get(index)[0];
                    String time = data.get(index)[1];
                    String fullEntry = data.get( index )[2];
                    String editDate = data.get(index)[4];
                    String EntryID = data.get(index)[3];

                    Bundle b = new Bundle();
                    b.putString("entry", fullEntry );
                    b.putString("header", entryHeader );
                    b.putString("date", date);
                    b.putString("oldTime", time);
                    b.putString("editDate", editDate);
                    b.putString("EntryID", EntryID);
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

    private void setListItems()
    {
        journalEntry e = new journalEntry(journalEntriesList.this);
        e.open();
        data = e.getEntries();
        e.close();

        final int s = data.size();
        String arr[] = new String[s];

        for(int i = 0, j = s - 1; i < s; i++, j--)
        {
            //arr[i].tv.setText( data.get(i)[2] );//set text view`s text to the journal entry
            //arr[j] = data.get(i)[0] + "\n" + data.get(i)[1] + "\n\n" + data.get(i)[2];
            arr[j] = data.get(i)[2];
        }

        ArrayAdapter<String> arrAdapt = new ArrayAdapter<String>(journalEntriesList.this,R.layout.entry_layout,R.id.entry, arr);
        l.setAdapter(arrAdapt);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setListItems();
    }
}
