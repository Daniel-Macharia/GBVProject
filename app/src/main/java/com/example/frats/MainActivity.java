package com.example.frats;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button user,assistant,login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if there`s a registered user
        String check[] = new String[2];
        try{
            com.example.frats.user checkUser = new user( MainActivity.this );
            checkUser.open();

            check = checkUser.readData();
            if( !check[0].equals("") )//if a user account exists
            {                             //load the login page
                //setContentView(R.layout.login_xml);
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);

                return;
            }

            checkUser.close();
        }catch( Exception e)
        {
            Dialog d = new Dialog(MainActivity.this);
            d.setTitle("Failed to fetch data");
            TextView tv = new TextView(MainActivity.this);
            tv.setText("Failed to connect to the databases!");
            d.setContentView(tv);
            //d.show();
        }

        //if no user account exists
        //load and setup the sign up page
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