package com.example.frats;

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

    @Override
    protected void onCreate(Bundle savedStateInstance)
    {

        super.onCreate(savedStateInstance);
        setContentView(R.layout.journal);

        save = findViewById(R.id.saveData);
        notes = findViewById(R.id.note);
        review = findViewById(R.id.review);
        date = findViewById(R.id.date);

        date.setText( parseDate(java.util.Calendar.getInstance().getTime().toString()) );

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = java.util.Calendar.getInstance().getTime().toString();
                String time = "";
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

    private String parseDate(String s)
    {
        String d = "";

        //int spaces = 0;
        Scanner read = new Scanner(s);

        for(int j = 0; read.hasNext(); j++)
        {
            if(j == 3 || j == 4)
                continue;

            d += " " + read.next();

        }
        return d;
    }
}
