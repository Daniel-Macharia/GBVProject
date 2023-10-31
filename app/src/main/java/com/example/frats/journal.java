package com.example.frats;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Scanner;
import java.util.StringJoiner;

public class journal extends AppCompatActivity {

    private Button save,review;
    private TextView title,date;
    private EditText notes;

    private static final int Notification_ID = 1;

    @Override
    protected void onCreate(Bundle savedStateInstance)
    {
        try{
            super.onCreate(savedStateInstance);
            setContentView(R.layout.journal);

            title = findViewById( R.id.title );
            save = findViewById(R.id.saveData);
            notes = findViewById(R.id.note);
            review = findViewById(R.id.review);
            date = findViewById(R.id.date);

            Calendar c = Calendar.getInstance();
            String dateStr = c.get(Calendar.DATE) + "/" + ( c.get(Calendar.MONTH) + 1 ) + "/" + c.get(Calendar.YEAR);

            Intent editIntent = getIntent();
            String text = editIntent.getStringExtra("editText" );

            if( text != null )
            {
                try{
                    String oldDate = editIntent.getStringExtra("oldDate");
                    String oldTime = editIntent.getStringExtra("oldTime");
                    title.setText("Edit Journal Entry");
                    date.setText(oldDate);
                    notes.setText(text);
                    review.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, "Editing text", Toast.LENGTH_SHORT).show();

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(journal.this, "saving an edit to the entry", Toast.LENGTH_SHORT).show();
                            String newEntry = notes.getText().toString();
                            int EntryID = Integer.parseInt( editIntent.getStringExtra("EntryID") );

                            String newDate = c.get(Calendar.DATE) + " - " + ( c.get(Calendar.MONTH) + 1 ) + " - " + c.get(Calendar.YEAR);

                            int h = c.get(Calendar.HOUR) , m = c.get(Calendar.MINUTE) , s = c.get( Calendar.SECOND);

                            h = (h == 0 ? 12 : h );

                            String newTime = "" + ( h < 10 ? "0" + h : h ) + ":" + ( m < 10 ? "0" + m : m ) + ":" + ( s < 10 ? "0" + s : s ) +
                                    ((c.get(Calendar.AM_PM) == Calendar.AM ) ? " AM" : " PM");

                            journalEntry j = new journalEntry(getApplicationContext());
                            j.open();
                            j.updateEntry( EntryID, newDate, newTime, newEntry, oldDate, oldTime,text );
                            j.close();

                            // Intent entriesIntent = new Intent(journal.this, journalEntriesList.class);
                            //startActivity(entriesIntent);

                            finish();
                        }
                    });
                }catch( Exception e )
                {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            date.setText( dateStr );

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    String date = c.get(Calendar.DATE) + " - " + ( c.get(Calendar.MONTH) + 1 ) + " - " + c.get(Calendar.YEAR);
                    int h = c.get(Calendar.HOUR) , m = c.get(Calendar.MINUTE) , s = c.get( Calendar.SECOND);

                    h = (h == 0 ? 12 : h );

                    String time = "" + ( h < 10 ? "0" + h : h ) + ":" + ( m < 10 ? "0" + m : m ) + ":" + ( s < 10 ? "0" + s : s ) +
                            ((c.get(Calendar.AM_PM) == Calendar.AM ) ? " AM" : " PM");
                    String entry = notes.getText().toString();

                    journalEntry e = new journalEntry(journal.this);

                    try {
                        e.open();

                        e.makeEntry(date,time,entry, "");
                        Toast.makeText(journal.this, "Entry made successfully" , Toast.LENGTH_SHORT).show();
                        notes.setText("");

                        e.close();
                    }catch(SQLException ex)
                    {
                        Toast.makeText(journal.this, ex.toString(),Toast.LENGTH_SHORT).show();
                    }

                    //String str = "saving data (" + notes.getText().toString() + " )";
                    //String name = java.util.Calendar.getInstance().getTime().toString();

                    //Toast.makeText(journal.this, "Entry made sucessfully" , Toast.LENGTH_SHORT).show();

                }
            });

            review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent listIntent = new Intent(journal.this, journalEntriesList.class);
                    startActivity(listIntent);
                }
            });

        }catch( Exception e )
        {
            Toast.makeText( this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
