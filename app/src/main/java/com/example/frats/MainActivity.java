package com.example.frats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.Dialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements Configuration.Provider {

    Button user,assistant,login;
    EditText phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //workerThread();
        //check if there`s a registered user
        String check[] = new String[4];
        try{

            //getMessagesFromFirebase messagesFromFirebase = new getMessagesFromFirebase(getApplicationContext());
            //messagesFromFirebase.add();

            //workerThread();
            com.example.frats.user checkUser = new user( MainActivity.this );
            checkUser.open();

            check = checkUser.readData();
            if( !check[0].equals("") )//if a user account exists
            {                             //load the login page
                //setContentView(R.layout.login_xml);
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish();

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
        phone = findViewById(R.id.phone_number);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = phone.getText().toString();
                if( validatePhone(phoneNumber) )
                    return;
                boolean isUser = true;

                switchToTermsAndConditions(phoneNumber,isUser);
            }
        });

        assistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = phone.getText().toString();
                if( validatePhone(phoneNumber) )
                    return;
                boolean isUser = false;

                switchToTermsAndConditions(phoneNumber,isUser);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLogin();
            }
        });




    }

    @Override
    public Configuration getWorkManagerConfiguration()
    {
        Toast.makeText(getApplicationContext(), "Getting custom work manager configuration", Toast.LENGTH_SHORT).show();

        return new Configuration.Builder()
                .setMinimumLoggingLevel( Log.INFO )
                .build();
    }

    private void workerThread()
    {
       // android.content.Context context = getApplicationContext();
        try{
            Toast.makeText( getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();


            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build();

            WorkRequest w = OneTimeWorkRequest.from(LoadMessages.class);
            WorkManager.getInstance(getApplicationContext()).enqueue(w);


            /* WorkRequest w = new OneTimeWorkRequest.Builder(LoadMessages.class)
                    .setConstraints(constraints)
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build();

           WorkManager.getInstance(getApplicationContext()).enqueue(w);

            WorkManager.initialize( getApplicationContext(), getWorkManagerConfiguration() );
            WorkManager wm = WorkManager.getInstance(getApplicationContext());
            wm.enqueue(w);
            wm.getWorkInfoByIdLiveData( w.getId() ).observe(new LifecycleOwner() {
                @NonNull
                @Override
                public Lifecycle getLifecycle() {
                    return null;
                }
            }, workInfo -> {
                if( workInfo.getState() != null && wor)

            }); */

            Toast.makeText(getApplicationContext(), "Hello II", Toast.LENGTH_SHORT).show();

        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validatePhone(String phone)
    {
        if( phone.equals(null) || phone.equals("") )
        {
            Toast.makeText(MainActivity.this, "Enter a valid Phone Number", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void switchToTermsAndConditions( String s, boolean isUser)
    {
        Intent intent = new Intent( this, termsAndConditions.class);
        intent.putExtra("phone", new String(s) );
        intent.putExtra("isUser", isUser);
        startActivity(intent);
    }

    private void switchToLogin()
    {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }
}