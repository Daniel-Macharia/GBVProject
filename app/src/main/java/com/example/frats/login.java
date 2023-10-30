package com.example.frats;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class login extends AppCompatActivity {

    Button login;
    //Button signup;
    EditText userName,userPassWord;
    TextView password;

    public static String isUserOrAssistant = null;
    public static boolean found = false;
    public static boolean finishedGettingUser = false;
    public static boolean finishedGettingAssistant = false;
    ArrayList<Pair<String, String>> userList = new ArrayList<>(10);
    ArrayList<Pair<String, String>> assistantList = new ArrayList<>(10);

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

            if( !MyFirebaseUtilityClass.isConnectedToNetwork( this ) )
            {
                Toast.makeText(this, "Not connected to network.", Toast.LENGTH_SHORT).show();
            }

            userList = MyFirebaseUtilityClass.findUserData();
            assistantList = MyFirebaseUtilityClass.findAssistantData();

            //Toast.makeText(login.this, "User not in local database", Toast.LENGTH_SHORT).show();
            password.setText("phone:");
            userPassWord.setInputType(InputType.TYPE_CLASS_PHONE);
            userPassWord.setHint("phone number");

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  try{
                      String name, phone;
                      name = userName.getText().toString();
                      phone = userPassWord.getText().toString();

                      if( !MyFirebaseUtilityClass.isConnectedToNetwork( login.this ) )
                      {
                          Toast.makeText( getApplicationContext(), "Not connected to network.", Toast.LENGTH_SHORT).show();
                      }


                      if( name.equals("") || phone.equals(""))
                      {
                          Toast.makeText(login.this, "Invalid username or phone number", Toast.LENGTH_SHORT).show();
                      }
                      else
                      {
                          if( !userList.isEmpty() && !assistantList.isEmpty())
                          {
                              if( userExists(new Pair<String, String>( name, phone) ) )
                              {
                                  Intent intent = new Intent( login.this, CreateNewPassword.class);
                                  intent.putExtra("username", name);
                                  intent.putExtra("phone", phone);
                                  intent.putExtra("isUser", isUserOrAssistant.equals("assistant") ? false : true );
                                  startActivity(intent);
                                  return;
                              }
                              else{
                                  return;
                              }
                          }

                          if( finishedGettingAssistant && finishedGettingUser )
                          {
                              Toast.makeText(login.this, "The User does not exist.\n" +
                                      "Please go back and create a new account", Toast.LENGTH_SHORT).show();
                          }
                          else
                          {
                              Toast.makeText(login.this, "Loading, please wait ...", Toast.LENGTH_SHORT).show();
                          }

                      }
                  }catch( Exception e )
                  {
                      Toast.makeText(login.this, e.toString(), Toast.LENGTH_SHORT).show();
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

    private boolean userExists( Pair<String, String> value)
    {
        for( Pair<String, String> current : assistantList )
        {
            if( current.first.equals( value.first) && current.second.equals( value.second ) )
            {
                isUserOrAssistant = "assistant";
                return true;
            }
            else
            {
                if( !current.first.equals(( value.first)) && current.second.equals( value.second) )
                {
                    Toast.makeText(this, "Invalid Username", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else
                if( current.first.equals(( value.first)) && !current.second.equals( value.second) )
                {
                    Toast.makeText(this, "Invalid Phone", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        for( Pair<String, String> current : userList )
        {
            if( current.first.equals( value.first) && current.second.equals( value.second ) )
            {
                isUserOrAssistant = "users";
                return true;
            }
            else
            {
                if( !current.first.equals(( value.first)) && current.second.equals( value.second) )
                {
                    Toast.makeText(this, "Invalid Username", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else
                if( current.first.equals(( value.first)) && !current.second.equals( value.second) )
                {
                    Toast.makeText(this, "Invalid Phone", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        return false;
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


}


