package com.example.frats;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

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
                       user newUserAccount = new user(createUser.this );
                       newUserAccount.open();

                       if( u_pass.equals(confirm) )
                       {
                           FirebaseDatabase database = FirebaseDatabase.getInstance();

                           newUserAccount.createUser(u_name,u_pass, phone,( isUser ? "users" : "assistant"), 1);
                           String user_OR_assistant = "";

                           if( isUser )
                           {
                               user_OR_assistant = "users";

                               DatabaseReference myRef = database.getReference(user_OR_assistant);
                               newUser u = new newUser(u_name,phone);
                               myRef.push().setValue(u);

                           }
                           else
                           {
                               user_OR_assistant = "assistant";

                               //checkNetworkConnection();
                               addToListOfParticipantsOfAllGroups(database.getReference("group"), u_name, phone);

                               DatabaseReference myRef = database.getReference(user_OR_assistant);
                               newUser u = new newUser(u_name,phone);
                               myRef.push().setValue(u);

                           }

                           welcomeAndLoadLogin w = new welcomeAndLoadLogin(createUser.this);
                           w.start();

                       }
                       else {
                           Toast.makeText(createUser.this, "Password and the \nconfirm password do not match! ", Toast.LENGTH_SHORT).show();
                           password.setText("");
                           confirmPassword.setText("");
                           return;
                       }


                       newUserAccount.close();
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

    private void checkNetworkConnection()
    {

        ConnectivityManager c = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo n = c.getActiveNetworkInfo();
        if( !n.isAvailable() )
        {
            Toast.makeText(this, "No Internet Connection! ", Toast.LENGTH_SHORT).show();
        }

    }

    private void addToListOfParticipantsOfAllGroups(DatabaseReference dbRef, String username, String phone)
    {
        groupParticipant p = new groupParticipant( new String( username), new String(phone) );

        dbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

               // Toast.makeText(createUser.this, "getting groups", Toast.LENGTH_SHORT).show();

                DataSnapshot groups = task.getResult();

                if( groups.hasChildren() )
                {
                    for( DataSnapshot group : groups.getChildren() )
                    {
                        String thisGroupKey = group.getKey();

                        if( group.hasChild("member" ) )
                        {
                            //Toast.makeText(createUser.this, "there exists members in this group", Toast.LENGTH_SHORT).show();
                            boolean isInGroup = false;

                            if( group.child("member").hasChildren() )
                            {
                                //Toast.makeText(createUser.this, "getting groups", Toast.LENGTH_SHORT).show();

                                ArrayList<String> membersPhones = new ArrayList<>(10);
                                for( DataSnapshot member : group.child("member").getChildren() )
                                {
                                    String name = "", contact = "";

                                    if( member.hasChild("usename") )
                                    {
                                        name = member.child("username").getValue().toString();
                                    }
                                    if( member.hasChild("phone") )
                                    {
                                        contact = member.child("phone").getValue().toString();
                                    }


                                    membersPhones.add( new String( contact ) );
                                }

                                if( !membersPhones.contains( p.phone ) )
                                {
                                    //add participant p to the group
                                    assert  thisGroupKey != null;
                                    dbRef.child(thisGroupKey).child("member").push().setValue(p);

                                }

                            }
                        }else {
                            //Toast.makeText(createUser.this, "Creating new members list\n"
                              //      + "key value is " + thisGroupKey, Toast.LENGTH_SHORT).show();
                            //create member node in the chat
                            //dbRef.child(thisGroupKey).push().setValue(new participants() );
                           // if( thisGroupKey == null )
                           //     Toast.makeText(createUser.this, "Group key is null", Toast.LENGTH_SHORT).show();
                           // else
                           try{
                               dbRef.child(thisGroupKey).child("member").push().setValue(p);
                           }catch( Exception e )
                           {
                               Toast.makeText(createUser.this, e.toString(), Toast.LENGTH_SHORT).show();
                           }
                        }




                    }
                }

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
