package com.example.frats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button user,assistant,login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        login = findViewById(R.id.login);
        user = findViewById(R.id.user);
        assistant = findViewById(R.id.assist);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToTermsAndConditions();
            }
        });

        assistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToTermsAndConditions();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLogin();
            }
        });
    }

    private void switchToHome()//switch to the landing or home activity
    {
        Intent switchToHomeIntent = new Intent(this, homeViewActivity.class);
        startActivity(switchToHomeIntent);
    }

    private void switchToTermsAndConditions()
    {
        Intent intent = new Intent( this, termsAndConditions.class);
        startActivity(intent);
    }

    private void switchToLogin()
    {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }
}