package com.example.frats;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    Button login;
    //Button signup;
    EditText userName,userPassWord;

    public static String isUserOrAssistant = null;
    public static boolean found = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_xml);

        login = findViewById(R.id.login);
        //signup = findViewById(R.id.signup);
        userName = findViewById(R.id.user_name);
        userPassWord = findViewById(R.id.user_password);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name,pass,u_name,u_pass;

                name = userName.getText().toString();
                pass = userPassWord.getText().toString();

                try {
                    user validateUser = new user(login.this);
                    validateUser.open();
                    String[] data = validateUser.readData();
                    validateUser.close();

                    if( data[0].equals("") )
                    {
                        //Toast.makeText(login.this, "User not in local database", Toast.LENGTH_SHORT).show();
                        loginWithFirebase(name, pass);
                        return;
                    }

                    u_name = data[0];
                    u_pass = data[1];

                    //validate info
                    if( name.equals(u_name) )
                    {
                        if( pass.equals( u_pass ) )
                        {
                            Dialog d = new Dialog(login.this);
                            d.setTitle("Login Successful!");
                            TextView tv = new TextView(login.this);
                            tv.setText("Login Successful!");
                            d.setContentView(tv);
                            d.show();

                            MyFirebaseUtilityClass.checkGroupsAllowed( login.this, data[2] );
                            MyFirebaseUtilityClass.loadAssistantsOrUsers( (data[3].equals("users") ? "assistant" : "users" ), login.this);
                            MyFirebaseUtilityClass.loadGroupChats( login.this, data[2] );

                            //Toast.makeText(login.this,data[2],Toast.LENGTH_SHORT).show();

                           loadHomeView(data[3]);

                        }
                        else {
                            Dialog d = new Dialog(login.this);
                            d.setTitle("Login Failed!");
                            TextView tv = new TextView(login.this);
                            tv.setText("Invalid password!");
                            d.setContentView(tv);
                            d.show();

                            userPassWord.setText("");

                        }
                    }else {
                        Dialog d = new Dialog(login.this);
                        d.setTitle("Login Failed!");
                        TextView tv = new TextView(login.this);
                        tv.setText("Invalid Username!");
                        d.setContentView(tv);
                        d.show();

                        userPassWord.setText("");
                        userName.setText("");
                    }

                }
                catch(Exception e)
                {
                    Toast.makeText(login.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                finally
                {

                }

            }
        });


    }
    private void loadHomeView( String userOrAssistant)
    {
        Intent toHome = new Intent(login.this, homeViewActivity.class);
        toHome.putExtra("isUser", new String( userOrAssistant ) );
        startActivity(toHome);
        finish();
    }

    private void loginWithFirebase(String username, String password)
    {
        //Toast.makeText(this, "Loging in with details from firebase", Toast.LENGTH_SHORT).show();
        MyFirebaseUtilityClass.checkIfUserExists( login.this, username, password);

        if( !found )
        {
            //Toast.makeText(this, "User details not found from firebase", Toast.LENGTH_SHORT).show();
            //finish();
            return;
        }

        //loadHomeView(isUserOrAssistant);
    }

}

class ListenIfFirebaseReturned implements Runnable {

    @Override
    public void run()
    {
        while( ! login.found )
        {

        }

    }

}

