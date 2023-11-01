package com.example.frats;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WriteStatement extends AppCompatActivity {

    EditText recipient, statement;

    Button sendStatement;

    @Override
    protected void onCreate( Bundle SavedInstanceState )
    {
        super.onCreate( SavedInstanceState );
        setContentView( R.layout.write_statement);

        recipient = findViewById(R.id.recipient);
        sendStatement = findViewById(R.id.sendStatement);
        statement = findViewById(R.id.statement);

        sendStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rec = recipient.getText().toString();
                String report = statement.getText().toString();

                if( MyFirebaseUtilityClass.validatePhone( WriteStatement.this, rec ) )
                {
                    Toast.makeText(WriteStatement.this, "To: " + rec + "\nStatement: \n " + report, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(WriteStatement.this, "Please ensure the phone number is valid\n\n" +
                            "e.g 07... or 01...", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
