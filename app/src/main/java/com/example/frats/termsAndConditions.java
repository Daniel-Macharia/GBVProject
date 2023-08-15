package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class termsAndConditions extends AppCompatActivity {
    Button toWelcome;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_and_conditions);

        toWelcome = findViewById(R.id.accept);

        toWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCreateUserView();
            }
        });
    }

    private void loadCreateUserView()
    {
        Intent intent = new Intent( this, createUser.class);
        startActivity(intent);
    }
}
