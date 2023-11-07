package com.example.frats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

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

                try{
                    Intent intent = getIntent();
                    Bundle data = intent.getBundleExtra("data");
                    String username = data.getString("username");
                    String password = data.getString("password");
                    String phone = data.getString("phone");
                    String isUser = data.getString("isUser");

                    user newUser = new user( termsAndConditions.this );
                    newUser.open();
                    newUser.createUser(username, password, phone, isUser, 1);
                    newUser.close();

                     MyFirebaseUtilityClass.addNewUser(isUser, new newUser(username, phone) );

                     if( isUser.equals("assistant") )
                     {
                         MyFirebaseUtilityClass.addToListOfParticipantsOfAllGroups("group", termsAndConditions.this, username, phone);
                     }
                     //set work request for checking new chat and group messages
                    Constraints constraints = new Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build();

                    PeriodicWorkRequest request = new PeriodicWorkRequest.Builder( MyNewWorker.class, 15, TimeUnit.MINUTES )
                            .setConstraints(constraints)
                            .build();

                    WorkManager.getInstance( getApplicationContext() ).enqueue(request);

                    loadWelcomeView();
                }catch(Exception e )
                {
                    Toast.makeText(termsAndConditions.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadWelcomeView()
    {
       // makeFirebaseWorkRequest();

       // FirebaseWorkRequestTask task = new FirebaseWorkRequestTask(this);
        //Thread t = new Thread(task);
        //t.start();

        Intent welcomeIntent = new Intent( termsAndConditions.this, welcome.class);
        startActivity(welcomeIntent);
        finish();
    }


}
