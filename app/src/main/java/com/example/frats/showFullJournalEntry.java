package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class showFullJournalEntry extends AppCompatActivity {

    TextView fullEntry;
    TextView header;
    TextView lastEditedOn;
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
            lastEditedOn = findViewById(R.id.lastEditedOn);

            Intent intent = getIntent();
            Bundle b = intent.getBundleExtra("data");

            String entryText = b.getString("entry");
            String editDate = b.getString("editDate");
            header.setText( b.getString("header") );
            fullEntry.setText( entryText );
            lastEditedOn.setText(editDate);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(showFullJournalEntry.this, "Clicked Edit", Toast.LENGTH_SHORT).show();
                    try{

                        Intent editIntent = new Intent(showFullJournalEntry.this, journal.class );
                        editIntent.putExtra("editText", entryText );
                        editIntent.putExtra("oldDate", b.getString("date") );
                        editIntent.putExtra("oldTime", b.getString("oldTime"));
                        editIntent.putExtra("EntryID", b.getString("EntryID") );
                        startActivity(editIntent);
                        finish();

                    }catch( Exception e )
                    {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch( Exception e )
        {

            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }

    }

}
