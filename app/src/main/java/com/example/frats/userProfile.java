package com.example.frats;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class userProfile extends AppCompatActivity {

    TextView userName,phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile);

        userName = findViewById(R.id.userName);
        phoneNumber = findViewById(R.id.phoneNumber);

        user thisUser = new user(userProfile.this);

        String userData[] = new String[3];

        thisUser.open();
        userData = thisUser.readData();

        userName.setText(userData[0]);
        phoneNumber.setText(userData[2]);

    }
}
