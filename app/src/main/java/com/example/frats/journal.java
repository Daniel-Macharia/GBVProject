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

public class journal extends AppCompatActivity {

    private Button save,review;
    private TextView date;
    private EditText notes;

    private static final int Notification_ID = 1;

    @Override
    protected void onCreate(Bundle savedStateInstance)
    {

        super.onCreate(savedStateInstance);
        setContentView(R.layout.journal);

        save = findViewById(R.id.saveData);
        notes = findViewById(R.id.note);
        review = findViewById(R.id.review);
        date = findViewById(R.id.date);

        Calendar c = Calendar.getInstance();
        String dateStr = c.get(Calendar.DATE) + "/" + ( c.get(Calendar.MONTH) + 1 ) + "/" + c.get(Calendar.YEAR);

        date.setText( dateStr );

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String date = c.get(Calendar.DATE) + " - " + ( c.get(Calendar.MONTH) + 1 ) + " - " + c.get(Calendar.YEAR);     String time = "" + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) +
                        ((c.get(Calendar.AM_PM) == Calendar.AM ) ? " AM" : " PM");
                String entry = notes.getText().toString();

                journalEntry e = new journalEntry(journal.this);

                try {
                    e.open();

                    e.makeEntry(date,time,entry);
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

    }

}
