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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class createUser extends AppCompatActivity {

    Button create;
    EditText username,password,confirmPassword;

    @Override
    protected void onCreate(Bundle savedStateInstance)
    {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.create_user);

        create = findViewById(R.id.create);
        username = findViewById(R.id.user_name);
        password = findViewById(R.id.pass);
        confirmPassword = findViewById(R.id.confirm);

        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone").toString();
        boolean isUser = intent.getBooleanExtra("isUser", true);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u_name,u_pass,confirm;
                u_name = username.getText().toString();
                u_pass = password.getText().toString();
                confirm = confirmPassword.getText().toString();

                if( invalidDetails(u_name,u_pass,confirm) )
                {
                    return;
                }
                else {//if the details are valid
                    //if neither the username, the password nor the confirm password is blank
                    boolean succeeded = true;
                   try{
                       user newUser = new user(createUser.this );
                       newUser.open();

                       if( u_pass.equals(confirm) )
                       {
                           newUser.createUser(u_name,u_pass, phone);
                           String user_OR_assistant = "";

                           if( isUser )
                               user_OR_assistant = "users";
                           else
                               user_OR_assistant = "assistant";

                           FirebaseDatabase database = FirebaseDatabase.getInstance();
                           DatabaseReference myRef = database.getReference(user_OR_assistant);
                           //myRef.push().child("user").child("username").setValue("u_name");
                           //myRef.child("user/username").setValue(u_name);
                           //myRef.child("user/phone").setValue(phone);
                           //myRef.push().child("username").setValue(u_name);
                           //myRef.push().child(u_name).child("phone").setValue(phone);
                           newUser u = new newUser(u_name,phone);

                           myRef.push().setValue(u);

                           welcomeAndLoadLogin w = new welcomeAndLoadLogin(createUser.this);
                           w.start();
                       }
                       else {
                           Toast.makeText(createUser.this, "Password and the \nconfirm password do not match! ", Toast.LENGTH_SHORT).show();
                           password.setText("");
                           confirmPassword.setText("");
                           return;
                       }


                       newUser.close();
                   }catch(Exception e)
                   {
                       succeeded = false;
                   }finally{
                       if( succeeded )
                       {
                           Dialog d = new Dialog(createUser.this);
                           d.setTitle("Succeeded!");
                           TextView tv = new TextView(createUser.this);
                           tv.setText("Account Created Successfully!");
                           d.setContentView(tv);
                           d.show();
                       }
                   }


                }

               // welcomeAndLoadLogin w = new welcomeAndLoadLogin(createUser.this);
               // w.start();
            }
        });
    }

    private boolean invalidDetails(String name, String password, String confirm)
    {
        if( name.equals("") )
        {
            Toast.makeText(createUser.this, "Username cannot be blank!",Toast.LENGTH_SHORT).show();
            return true;
        }
        else if( password.equals("") )
        {
            Toast.makeText(createUser.this, "Password cannot be blank!",Toast.LENGTH_SHORT).show();
            return true;
        }
        else if( confirm.equals("") )
        {
            Toast.makeText(createUser.this, "Confirm Password cannot be blank!",Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            return false;
        }
    }

    public void loadWelcome()
    {
        Intent welcomeIntent = new Intent( createUser.this, welcome.class);
        startActivity(welcomeIntent);
    }

    public void loadLogin()
    {
        Intent intent = new Intent(createUser.this, login.class);
        startActivity(intent);
    }

}

class welcomeAndLoadLogin extends Thread{

    private createUser user;

    public welcomeAndLoadLogin(createUser user)
    {
        this.user = user;
    }

    @Override
    public void run()
    {

        user.loadWelcome();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        user.loadLogin();
    }
}

class newUser
{
    public String username;
    public String phone;

    public newUser(String name, String phoneNumber)
    {
        username = name;
        phone = phoneNumber;
    }
}
