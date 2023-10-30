package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class showFullJournalEntry extends AppCompatActivity {

    TextView fullEntry;
    EditText fullEntryEdit;
    TextView header;
    Button edit;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {

        try{
            super.onCreate( savedInstanceState );
            setContentView( R.layout.show_full_journal_entry );

            header = findViewById( R.id.header );
            fullEntry = findViewById( R.id.fullEntry );
            edit = findViewById( R.id.edit );
            fullEntryEdit = findViewById( R.id.fullEntryEdit);

            Intent intent = getIntent();
            Bundle b = intent.getBundleExtra("data");

            String entryText = b.getString("entry");
            header.setText( b.getString("header") );
            fullEntry.setText( entryText );

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(showFullJournalEntry.this, "Clicked Edit", Toast.LENGTH_SHORT).show();

                    fullEntryEdit.setText( entryText );
                    fullEntryEdit.setHeight( fullEntry.getHeight() );
                    fullEntry.setHeight(0);
                }
            });

        }catch( Exception e )
        {

            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

}
