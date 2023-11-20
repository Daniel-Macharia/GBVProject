package com.example.frats;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {

    Button login, forgot;
    //Button signup;
    EditText userName,userPassWord;
    TextView password, usernameLabel;

    public static String isUserOrAssistant = null;
    public static boolean found = false;
    public static boolean finishedGettingUser = false;
    public static boolean finishedGettingAssistant = false;

    String[] data = new String[4];
    ArrayList<userData> userList = new ArrayList<>(10);
    ArrayList<userData> assistantList = new ArrayList<>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_xml);

        login = findViewById(R.id.login);
        forgot = findViewById(R.id.forgotPassword);
        userName = findViewById(R.id.user_name);
        userPassWord = findViewById(R.id.user_password);
        password = findViewById( R.id.passLabel);
        usernameLabel = findViewById(R.id.usernameLabel);

        //boolean cancelled = false;
        //cancelled = launchPhoneHintPicker();

        user validateUser = new user(login.this);
        validateUser.open();
        data = validateUser.readData();
        validateUser.close();

        if( data[0].equals("") )
        {
            forgot.setVisibility(View.INVISIBLE);

            if( !MyFirebaseUtilityClass.isConnectedToNetwork( this ) )
            {
                Toast.makeText(this, "Not connected to network.", Toast.LENGTH_SHORT).show();
            }

            userList = MyFirebaseUtilityClass.findUserData();
            assistantList = MyFirebaseUtilityClass.findAssistantData();

            //Toast.makeText(login.this, "User not in local database", Toast.LENGTH_SHORT).show();
            usernameLabel.setText("Phone:     ");
            userName.setInputType(InputType.TYPE_CLASS_PHONE);
            userName.setHint("phone number");

            password.setText("Password: ");
            userPassWord.setHint("password");


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  try{
                      String pass, phone;
                      String username = "";
                      phone = userName.getText().toString();
                      pass = userPassWord.getText().toString();

                      if( !MyFirebaseUtilityClass.isConnectedToNetwork( login.this ) )
                      {
                          Toast.makeText( getApplicationContext(), "Not connected to network.", Toast.LENGTH_SHORT).show();
                      }


                      if( pass.equals("") || phone.equals(""))
                      {
                          Toast.makeText(login.this, "Invalid phone or password", Toast.LENGTH_SHORT).show();
                      }
                      else
                      {
                          if( finishedGettingAssistant && finishedGettingUser)
                          {
                              EncryptMessage em = new EncryptMessage();
                              userData d = getThisUserData( new userData("", em.encrypt( pass ), phone, "") );
                              if( !d.phone.equals("") && !d.password.equals("") )
                              {


                                  user u = new user(login.this);
                                  u.open();
                                  u.createUser( d.username, d.password, d.phone, d.email, isUserOrAssistant, 1);
                                  u.close();

                                  Intent intent = new Intent( login.this, homeViewActivity.class);
                                  //intent.putExtra("username", d.username);
                                  //intent.putExtra("phone", d.phone);
                                  intent.putExtra("isUser", isUserOrAssistant);
                                  startActivity(intent);

                                  initData( d.phone, isUserOrAssistant.equals("users") ? "assistant" : "users" );

                                  Constraints constraints = new Constraints.Builder()
                                          .setRequiredNetworkType(NetworkType.CONNECTED)
                                          .build();

                                  PeriodicWorkRequest request = new PeriodicWorkRequest.Builder( MyNewWorker.class, 15, TimeUnit.MINUTES )
                                          .setConstraints(constraints)
                                          .build();

                                  WorkManager.getInstance( getApplicationContext() ).enqueue(request);

                                  finishAffinity();

                              }
                              else
                              {
                                  Toast.makeText(login.this, "The User does not exist.\n" +
                                          "Please go back and create a new account", Toast.LENGTH_SHORT).show();
                              }
                          }else
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

                        EncryptMessage em = new EncryptMessage();
                        u_name = data[0];
                        u_pass = em.decrypt( data[1] );

                        //validate info
                        if( name.equals(u_name) )
                        {
                            if( pass.equals( u_pass ) )
                            {
                                Dialog d = new Dialog(login.this);
                                d.setTitle("Login Successful!");
                                TextView tv = new TextView(login.this);
                                tv.setBackgroundColor(Color.WHITE);
                                tv.setTextColor(Color.BLUE);
                                tv.setText("Login Successful!");
                                d.setContentView(tv);
                                //d.show();

                                alert( getApplicationContext(),"Login Successful");

                                initData( data[2], data[3].equals("users") ? "assistant" : "users" );

                                Constraints constraints = new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build();

                                PeriodicWorkRequest request = new PeriodicWorkRequest.Builder( MyNewWorker.class, 15, TimeUnit.MINUTES )
                                        .setConstraints(constraints)
                                        .build();

                                WorkManager.getInstance( getApplicationContext() ).enqueue(request);

                                loadHomeView(data[3]);

                            }
                            else {
                                Dialog d = new Dialog(login.this);
                                d.setTitle("Login Failed!");
                                TextView tv = new TextView(login.this);
                                tv.setText("Invalid password!");
                                d.setContentView(tv);
                                //d.show();
                                alert( getApplicationContext(),"Invalid Password");

                                userPassWord.setText("");

                            }
                        }else {
                            Dialog d = new Dialog(login.this);
                            d.setTitle("Login Failed!");
                            TextView tv = new TextView(login.this);
                            tv.setText("Invalid Username!");
                            d.setContentView(tv);
                            //d.show();
                            alert( getApplicationContext(),"Invalid Username");

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

            forgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent( login.this, CreateNewPassword.class);
                    startActivity(intent);
                }
            });

        }

    }

    private userData getThisUserData(userData value)
    {
        userData data = new userData("","","", "");

        for( userData current : assistantList )
        {
            if( current.phone.equals( value.phone) && current.password.equals( value.password ) )
            {
                isUserOrAssistant = "assistant";

                data.username = new String(current.username);
                data.phone = new String(current.phone);
                data.password = new String(current.password);
                data.email = new String( current.email );
                //return true;
            }
            else
            {
                if( !current.phone.equals( value.phone) && current.password.equals( value.password) )
                {
                    Toast.makeText(this, "Invalid Phone", Toast.LENGTH_SHORT).show();
                   // return false;
                }

                if( current.phone.equals( value.phone) && !current.password.equals( value.password) )
                {
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
                   // return false;
                }
            }
        }

        for( userData current : userList )
        {
            if( current.phone.equals( value.phone) && current.password.equals( value.password ) )
            {
                isUserOrAssistant = "users";

                data.username = new String(current.username);
                data.phone = new String(current.phone);
                data.password = new String(current.password);
                data.email = new String( current.email );
               // return true;
            }
            else
            {
                if( !current.phone.equals( value.phone) && current.password.equals( value.password))
                {
                    Toast.makeText(this, "Invalid Phone", Toast.LENGTH_SHORT).show();
                    //return false;
                }

                if( current.phone.equals( value.phone) && !current.password.equals( value.password))
                {
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
                   // return false;
                }
            }
        }

        return data;
    }

    public static void alert(Context context, String message)
    {
        try{
            Toast t = Toast.makeText(context,message,Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER,0,0);
            t.getView().setBackground( context.getDrawable( R.drawable.toast_drawable) );
            t.show();
        }catch(Exception e )
        {
            Toast.makeText(context, "error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean launchPhoneHintPicker()
    {
        try{

            ActivityResultLauncher<IntentSenderRequest> PhoneNumberHintIntentResultLauncher =
                    registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            try{
                                String phoneNumber = Identity.getSignInClient(login.this).getPhoneNumberFromIntent(result.getData());
                                Toast.makeText(login.this, "Phone Number is: " + phoneNumber, Toast.LENGTH_SHORT).show();
                            }catch( Exception e )
                            {
                                Toast.makeText(login.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            GetPhoneNumberHintIntentRequest request = GetPhoneNumberHintIntentRequest.builder().build();

            Identity.getSignInClient(this).getPhoneNumberHintIntent( request )
                    .addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                        @Override
                        public void onSuccess(PendingIntent pendingIntent) {
                            IntentSender intentSender = pendingIntent.getIntentSender();
                            PhoneNumberHintIntentResultLauncher.launch( new IntentSenderRequest.Builder(intentSender).build() );
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(login.this, "Phone Number Hint Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch ( Exception e )
        {
            Toast.makeText(this, "Error Launching Phone Hint Picker: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean userExists( userData value)
    {
        for( userData current : assistantList )
        {
            if( current.phone.equals( value.phone) && current.password.equals( value.password ) )
            {
                isUserOrAssistant = "assistant";
                return true;
            }
            else
            {
                if( !current.phone.equals(( value.phone)) )
                {
                    Toast.makeText(this, "Invalid Phone", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if( !current.password.equals( value.password) )
                {
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }

        for( userData current : userList )
        {
            if( current.phone.equals( value.phone) && current.password.equals( value.password ) )
            {
                isUserOrAssistant = "users";
                return true;
            }
            else
            {
                if( !current.phone.equals( value.phone) )
                {
                    Toast.makeText(this, "Invalid Phone", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if( !current.password.equals( value.password))
                {
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
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
        //MyFirebaseUtilityClass.updateAllChats(login.this);
    }

    public void loadHomeView(String is_user_or_assistant)
    {

        Intent toHome = new Intent( login.this, homeViewActivity.class );
        toHome.putExtra("isUser", is_user_or_assistant);
        startActivity(toHome);
    }

    @Override
    public void onResume()
    {
        try{
            super.onResume();
            userName.setText("");
            userPassWord.setText("");

            user u = new user( login.this );
            u.open();
            data = u.readData();
            u.close();
        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), "error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


}


class userData
{
    public String username = "";
    public String password = "";
    public String phone = "";
    public String email = "";

    public userData( String username, String password, String phone, String email )
    {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
    }
}

