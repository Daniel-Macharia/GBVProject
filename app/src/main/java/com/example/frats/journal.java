package com.example.frats;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class journal extends AppCompatActivity {

    private Button save;
    private EditText notes;

    @Override
    protected void onCreate(Bundle savedStateInstance)
    {

        super.onCreate(savedStateInstance);
        setContentView(R.layout.journal);

        save = findViewById(R.id.saveData);
        notes = findViewById(R.id.note);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str = "saving data (" + notes.getText().toString() + " )";
                String name = java.util.Calendar.getInstance().getTime().toString();

                Toast.makeText(journal.this, str , Toast.LENGTH_SHORT).show();

            }
        });

    }
}
