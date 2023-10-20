package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class termsAndConditions extends AppCompatActivity {
    Button toLogin;
    TextView termsOfUse;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_and_conditions);

        toLogin = findViewById(R.id.accept);
        termsOfUse = findViewById(R.id.termsOfUse);

        String terms = "";

        try {
            InputStream readTerms = getAssets().open("frats_terms_and_conditions.txt");
            int size = readTerms.available();

            byte[] buffer = new byte[size];

            readTerms.read(buffer);
            terms = new String(buffer);

        }catch (IOException ioException) {
            Toast.makeText(termsAndConditions.this, ioException.toString(),Toast.LENGTH_SHORT).show();
        }

        termsOfUse.setText(terms);


        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWelcomeView();
            }
        });
    }


    public void loadWelcomeView()
    {
        Intent welcomeIntent = new Intent( termsAndConditions.this, welcome.class);
        startActivity(welcomeIntent);
        finish();
    }


}
