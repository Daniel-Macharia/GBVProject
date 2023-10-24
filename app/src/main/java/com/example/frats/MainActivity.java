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
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button user, assistant, loginButton;
    EditText username, phone, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        try{
            com.example.frats.user checkUser = new user( MainActivity.this );
            checkUser.open();
            String[] check = checkUser.readData();
            checkUser.close();

            //makeFirebaseWorkRequest();

            if( !check[0].equals("") )//if a user account exists
            {                             //load the login page
                //setContentView(R.layout.login_xml);
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish();

                return;
            }

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

        setContentView(R.layout.create_user);

        user = findViewById(R.id.user);
        assistant = findViewById(R.id.assistant);
        loginButton = findViewById(R.id.login);
        phone = findViewById(R.id.phone);
        username = findViewById(R.id.user_name);
        password = findViewById(R.id.pass);
        confirmPassword = findViewById(R.id.confirm);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phone.getText().toString();
                Toast.makeText(MainActivity.this, "Phone = " + phoneNumber, Toast.LENGTH_SHORT).show();
                boolean isUser = true;
                createUserOrAssistant(phoneNumber, isUser);
            }
        });

        assistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phone.getText().toString();
                Toast.makeText(MainActivity.this, "Phone = " + phoneNumber, Toast.LENGTH_SHORT).show();
                boolean isUser = false;
                createUserOrAssistant(phoneNumber, isUser);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainActivity.this, login.class );
                startActivity( intent );
            }
        });
    }

    private void createUserOrAssistant(String phoneNumber, boolean isUser) {
        String u_name, u_phone, u_pass, confirm;
        u_name = username.getText().toString();
        u_pass = password.getText().toString();
        confirm = confirmPassword.getText().toString();

        if (!invalidDetails(u_name, phoneNumber, u_pass, confirm)) {//if the details are valid
            //if neither the username, the password nor the confirm password is blank
            boolean succeeded = true;
            try {
                user newUserAccount = new user(MainActivity.this);
                newUserAccount.open();

                if (u_pass.equals(confirm)) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    newUserAccount.createUser(u_name, u_pass, phoneNumber, (isUser ? "users" : "assistant"), 1);
                    String user_OR_assistant = "";

                    if (isUser) {
                        user_OR_assistant = "users";

                        newUser u = new newUser(u_name, phoneNumber);
                        MyFirebaseUtilityClass.addNewUser(user_OR_assistant, u);

                    } else {
                        user_OR_assistant = "assistant";

                        //checkNetworkConnection();
                        MyFirebaseUtilityClass.addToListOfParticipantsOfAllGroups("group", getApplicationContext(), u_name, phoneNumber);

                        newUser u = new newUser(u_name, phoneNumber);
                        MyFirebaseUtilityClass.addNewUser(user_OR_assistant, u);

                    }

                    launchTermsAndConditions();

                } else {
                    Toast.makeText(MainActivity.this, "Password and the \nconfirm password do not match! ", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    confirmPassword.setText("");
                    return;
                }


                newUserAccount.close();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }


        }

        // welcomeAndLoadLogin w = new welcomeAndLoadLogin(createUser.this);
        // w.start();

    }

    private void launchTermsAndConditions() {
        Intent intent = new Intent(MainActivity.this, termsAndConditions.class);
        startActivity(intent);
        finish();
    }

    private boolean invalidDetails(String name, String phoneNumber, String password, String confirm) {
        if (name.equals("")) {
            Toast.makeText(MainActivity.this, "Username cannot be blank!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (phoneNumber.equals("")) {
            Toast.makeText(MainActivity.this, "Phone cannot be blank!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (password.equals("")) {
            Toast.makeText(MainActivity.this, "Password cannot be blank!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (confirm.equals("")) {
            Toast.makeText(MainActivity.this, "Confirm Password cannot be blank!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
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
