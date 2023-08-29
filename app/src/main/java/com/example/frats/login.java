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
                String data[] = new String[3];
                name = userName.getText().toString();
                pass = userPassWord.getText().toString();

                try {
                    user validateUser = new user(login.this);
                    validateUser.open();
                    data = validateUser.readData();

                    u_name = data[0];
                    u_pass = data[1];

                    //validata info
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

                            //Toast.makeText(login.this,data[2],Toast.LENGTH_SHORT).show();
                            Intent toHome = new Intent(login.this, homeViewActivity.class);
                            startActivity(toHome);
                            finish();

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

                    validateUser.close();
                }
                catch(Exception e)
                {

                }
                finally
                {

                }

                //Intent toHome = new Intent(login.this, homeViewActivity.class);
                //startActivity(toHome);

            }
        });

       /* signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toSignUp = new Intent( login.this, MainActivity.class);
                startActivity(toSignUp);

            }
        });*/



    }
}
