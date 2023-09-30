package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class showFullJournalEntry extends AppCompatActivity {

    TextView fullEntry;
    TextView header;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {

        try{
            super.onCreate( savedInstanceState );
            setContentView( R.layout.show_full_journal_entry );

            header = findViewById( R.id.header );
            fullEntry = findViewById( R.id.fullEntry );

            Intent intent = getIntent();
            Bundle b = intent.getBundleExtra("data");

            header.setText( b.getString("header") );
            fullEntry.setText( b.getString("entry") );

        }catch( Exception e )
        {

            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

}
