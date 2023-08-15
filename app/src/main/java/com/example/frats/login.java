package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {

    private DatabaseReference mDatabase;

    Button login, signup;
    EditText userName,userPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_xml);

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        userName = findViewById(R.id.user_name);
        userPassWord = findViewById(R.id.user_password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toHome = new Intent(login.this, homeViewActivity.class);
                startActivity(toHome);

                //String name = userName.getText().toString();
                //String  passWord = userPassWord.getText().toString();

                // ...Connect to firebase
                //mDatabase = FirebaseDatabase.getInstance().getReference();

                //add user
                //mDatabase.child("user").setValue(name);
               // mDatabase.child("user").child("password").setValue(passWord);
               // mDatabase.child("user").child(name).child("password").setValue(passWord);
                //mDatabase.child("pass").setValue(name);


            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toSignUp = new Intent( login.this, MainActivity.class);
                startActivity(toSignUp);

            }
        });



    }
}
