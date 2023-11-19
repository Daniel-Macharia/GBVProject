package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class CreateNewPassword extends AppCompatActivity  {


    Button create;
    EditText pass, confirm;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.create_new_password);

            create = findViewById( R.id.create );
            pass = findViewById(R.id.pass);
            confirm = findViewById( R.id.confirm );


            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String password = "", confirmPass = "";

                    password = pass.getText().toString();
                    confirmPass = confirm.getText().toString();

                    if( password.equals("") )
                    {
                        Toast.makeText(CreateNewPassword.this, "Password cannot be blank!", Toast.LENGTH_SHORT).show();
                        return;
                    }else if( confirmPass.equals("") )
                    {
                        Toast.makeText(CreateNewPassword.this, "Confirm password cannot be blank!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if( !password.equals(confirmPass) )
                    {
                        Toast.makeText(CreateNewPassword.this, "Password and the confirm password do not match!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //when password and the confirm password do match
                    if( MyFirebaseUtilityClass.validatePassword(password) )
                    {
                        EncryptMessage em = new EncryptMessage();
                        user u = new user(CreateNewPassword.this);
                        u.open();
                        u.updateUserPassword( em.encrypt(password) );
                        String[] data = u.readData();
                        u.close();

                        MyFirebaseUtilityClass.updatePassword(data[3], data[0], data[2], em.encrypt(password) );

                        Toast.makeText(CreateNewPassword.this, "Successfully updated the password", Toast.LENGTH_SHORT).show();

                        finish();
                    }else
                    {
                        Toast.makeText(CreateNewPassword.this, "Enter a Strong password." +
                                "\nShould be at least 8 characters long\n" +
                                "and contain digits and both upper and lowercase characters", Toast.LENGTH_SHORT).show();
                    }
                }
            });

           /* Intent intent = getIntent();
            String username = intent.getStringExtra("username");
            String phone = intent.getStringExtra("phone");
            boolean isUser = intent.getBooleanExtra("isUser", true);



            create.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick( View view )
                {
                    String password, confirmPassword;
                    password = pass.getText().toString();
                    confirmPassword = confirm.getText().toString();

                    if(  password.equals("") || confirmPassword.equals("") )
                    {
                        Toast.makeText(CreateNewPassword.this, "Invalid password or confirm password", Toast.LENGTH_SHORT).show();
                    }else if( !confirmPassword.equals(password) )
                    {
                        Toast.makeText(CreateNewPassword.this, "The password and confirm password do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        user u = new user(CreateNewPassword.this);
                        u.open();
                        u.createUser(username, password, phone, (isUser ? "users" : "assistant"), 1);
                        u.close();

                        MyFirebaseUtilityClass.updatePassword( (isUser ? "users" : "assistant"), username, phone, password);

                        Intent intent = new Intent( CreateNewPassword.this, homeViewActivity.class );
                        intent.putExtra("isUser", isUser ? "users" : "assistant");
                        startActivity(intent);
                        initData(phone, isUser ? "assistant" : "users");

                        //set work request for checking new chat and group messages
                        Constraints constraints = new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build();

                        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder( MyNewWorker.class, 15, TimeUnit.MINUTES )
                                .setConstraints(constraints)
                                .build();

                        WorkManager.getInstance( getApplicationContext() ).enqueue(request);

                        //finish();
                        finishAffinity();
                    }

                }
            }); */

        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void initData(String phone, String isUorA)
    {
        MyFirebaseUtilityClass.checkGroupsAllowed( getApplicationContext(), phone );
        MyFirebaseUtilityClass.loadAssistantsOrUsers( getApplicationContext(), isUorA, phone);
        MyFirebaseUtilityClass.loadGroupChats( getApplicationContext(), phone );
    }

}
