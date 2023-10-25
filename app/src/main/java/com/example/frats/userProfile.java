package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class userProfile extends AppCompatActivity {

    TextView userName,phoneNumber;
    Button edit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile);

        userName = findViewById(R.id.userName);
        phoneNumber = findViewById(R.id.phoneNumber);
        edit = findViewById(R.id.edit);

        user thisUser = new user(userProfile.this);

        String userData[] = new String[3];

        thisUser.open();
        userData = thisUser.readData();

        userName.setText(userData[0]);
        phoneNumber.setText(userData[2]);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent( userProfile.this, EditProfile.class );
                startActivity( editIntent );
            }
        });

    }
}
