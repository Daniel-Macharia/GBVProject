package com.example.frats;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
    TextView password;

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
        password = findViewById( R.id.passLabel);

        user validateUser = new user(login.this);
        validateUser.open();
        String[] data = validateUser.readData();
        validateUser.close();

        if( data[0].equals("") )
        {
            Toast.makeText(login.this, "User not in local database", Toast.LENGTH_SHORT).show();
            password.setText("phone:");
            userPassWord.setInputType(InputType.TYPE_CLASS_PHONE);
            userPassWord.setHint("phone number");

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name, phone;
                    name = userName.getText().toString();
                    phone = userPassWord.getText().toString();

                    if( name.equals("") || phone.equals(""))
                    {
                        Toast.makeText(login.this, "Invalid username or phone number", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if( !found )
                        {
                            MyFirebaseUtilityClass.checkIfUserExists( login.this, name, phone);
                            Toast.makeText(login.this, "Loading....\nPlease wait.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent( login.this, CreateNewPassword.class);
                        intent.putExtra("username", name);
                        intent.putExtra("phone", phone);
                        intent.putExtra("isUser", isUserOrAssistant.equals("assistant") ? false : true );
                        startActivity(intent);

                        //initData( phone, isUserOrAssistant.equals("users") ? "assistant" : "users" );
                    }
                }
            });

        }else {


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String name,pass,u_name,u_pass;

                    name = userName.getText().toString();
                    pass = userPassWord.getText().toString();

                    try {

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

                                Toast.makeText(login.this,data[2],Toast.LENGTH_SHORT).show();
                                initData( data[2], data[3].equals("users") ? "assistant" : "users" );

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


                }
            });

        }

    }

    public void initData(String phone, String isUorA)
    {
        MyFirebaseUtilityClass.checkGroupsAllowed( login.this, phone );
        MyFirebaseUtilityClass.loadAssistantsOrUsers( login.this, isUorA, phone);
        MyFirebaseUtilityClass.loadGroupChats( login.this, phone );
    }

    public void loadHomeView(String is_user_or_assistant)
    {

        Intent toHome = new Intent( login.this, homeViewActivity.class );
        toHome.putExtra("isUser", is_user_or_assistant);
        startActivity(toHome);
        finish();

    }
    private void loadCreateNewPasswordView( String userOrAssistant)
    {
        Intent toCreateNewPassword = new Intent(login.this, CreateNewPassword.class);
        //toHome.putExtra("isUser", new String( userOrAssistant ) );
        startActivity(toCreateNewPassword);
        finish();
    }

    private void loginWithFirebase(String username, String password)
    {
        //Toast.makeText(this, "Loging in with details from firebase", Toast.LENGTH_SHORT).show();

        if( !found )
        {
            //Toast.makeText(this, "User details not found from firebase", Toast.LENGTH_SHORT).show();
            //finish();
            MyFirebaseUtilityClass.checkIfUserExists( login.this, username, password);

            return;
        }

        //loadCreateNewPasswordView(isUserOrAssistant);
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

