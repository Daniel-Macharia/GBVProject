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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button user, assistant, loginButton;
    EditText username, phone, password, confirmPassword, email;

    public static boolean hasFinishedSearchingUser, hasFinishedSearchingAssistant;
    public static boolean userFound, assistantFound;
    ArrayList<String> userList = new ArrayList<>(10);
    ArrayList<String> assistantList = new ArrayList<>(10);

    @Override
    protected void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        try{

            loadCurrentUsersAndAssistantsFromFirebase();

            com.example.frats.user checkUser = new user( MainActivity.this );
            checkUser.open();
            String[] check = checkUser.readData();
            checkUser.close();

            hasFinishedSearchingUser = false;
            hasFinishedSearchingAssistant = false;
            userFound = false;
            assistantFound = false;

            if( !check[0].equals("") )//if a user account exists
            {                             //load the login page
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
        email = findViewById( R.id.email );

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = phone.getText().toString();
                if( hasFinishedSearchingUser )
                {

                    String u_name, u_pass, confirm;
                    u_name = username.getText().toString();
                    u_pass = password.getText().toString();
                    confirm = confirmPassword.getText().toString();
                    String emailAddress = "";
                    emailAddress = email.getText().toString();

                    createNewUser(phoneNumber, emailAddress, u_name, u_pass, confirm, true);


                }else{


                    if( !MyFirebaseUtilityClass.isConnectedToNetwork( getApplicationContext() ) )
                    {
                        Toast.makeText(MainActivity.this, "No Internet Connection\nPlease connect to a network", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        loadCurrentUsersAndAssistantsFromFirebase();
                        //MyFirebaseUtilityClass.findUser( getApplicationContext(), phoneNumber);
                        Toast.makeText(MainActivity.this, "Searching for user...", Toast.LENGTH_SHORT).show();
                    }


                }

                //Toast.makeText(MainActivity.this, "Phone = " + phoneNumber, Toast.LENGTH_SHORT).show();
            }
        });

        assistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phone.getText().toString();
                if( hasFinishedSearchingAssistant && hasFinishedSearchingUser)
                {
                    String u_name, u_pass, confirm;
                    u_name = username.getText().toString();
                    u_pass = password.getText().toString();
                    confirm = confirmPassword.getText().toString();
                    String emailAddress = "";
                    emailAddress = email.getText().toString();

                    createNewUser(phoneNumber, emailAddress, u_name, u_pass, confirm, false);

                }else {

                    if(! MyFirebaseUtilityClass.isConnectedToNetwork(getApplicationContext()) )
                    {
                        Toast.makeText(MainActivity.this, "No internet connection\nPlease connect to network", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        loadCurrentUsersAndAssistantsFromFirebase();
                       // MyFirebaseUtilityClass.findAssistant( getApplicationContext(), phoneNumber );
                        Toast.makeText(MainActivity.this, "Searching for Assistant...", Toast.LENGTH_SHORT).show();
                    }

                }


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

    private void createNewUser( String phoneNumber, String emailAddress, String username, String password, String confirm, boolean userBtnClicked)
    {
        if( assistantExists(phoneNumber) )
        {
            Toast.makeText(MainActivity.this, "An assistant with this phone exists\nClick login to log in", Toast.LENGTH_SHORT).show();
        }
        else
        if( userExists( phoneNumber ) )
        {
            Toast.makeText(MainActivity.this, "A user with this phone exists\nclick login to log in", Toast.LENGTH_SHORT).show();

        }
        else
        if(userBtnClicked){
            Toast.makeText(MainActivity.this, "Creating New User...", Toast.LENGTH_SHORT).show();
            createUserOrAssistant(phoneNumber, emailAddress, username, password, confirm, true);
        }
        else{
            Toast.makeText(MainActivity.this, "Creating New Assistant", Toast.LENGTH_SHORT).show();
            createUserOrAssistant(phoneNumber, emailAddress, username, password, confirm, false);
        }
    }

    private boolean assistantExists( String phone )
    {

        if( assistantList.contains(phone) )
            return true;

        return false;
    }

    private boolean userExists( String phone )
    {
        if( userList.contains( phone ) )
            return true;

        return false;
    }

    private void loadCurrentUsersAndAssistantsFromFirebase()
    {
        if( MyFirebaseUtilityClass.isConnectedToNetwork(this) )
        {
            userList = MyFirebaseUtilityClass.findUser();
            assistantList = MyFirebaseUtilityClass.findAssistant();
        }
        else{
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void createUserOrAssistant(String phoneNumber, String emailAddress, String u_name, String u_pass, String confirm, boolean isUser) {


        if( !MyFirebaseUtilityClass.validatePhone(this, phoneNumber ) )
        {
            Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!invalidDetails(u_name, phoneNumber, u_pass, confirm)) {//if the details are valid
            //if neither the username, the password nor the confirm password is blank
            try {
                if (u_pass.equals(confirm)) {

                    //newUserAccount.createUser(u_name, u_pass, phoneNumber, (isUser ? "users" : "assistant"), 1);
                    String user_OR_assistant = "";

                    if (isUser)
                    {
                        user_OR_assistant = "users";

                    }
                    else
                    {
                        user_OR_assistant = "assistant";
                    }

                    launchTermsAndConditions( u_name, u_pass, phoneNumber, emailAddress, user_OR_assistant);

                } else {
                    Toast.makeText(MainActivity.this, "Password and the \nconfirm password do not match! ", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    confirmPassword.setText("");
                }


            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }


        }

        // welcomeAndLoadLogin w = new welcomeAndLoadLogin(createUser.this);
        // w.start();

    }

    private void launchTermsAndConditions(String username, String password, String phone, String emailAddress, String isUser) {

        Bundle data = new Bundle();
        data.putString("username", username);
        data.putString("password", password);
        data.putString("phone", phone);
        data.putString("isUser", isUser);
        data.putString("email", emailAddress);

        Intent intent = new Intent(MainActivity.this, termsAndConditions.class);
        intent.putExtra("data", data);
        startActivity(intent);
        //finish();
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
        } else if( !MyFirebaseUtilityClass.validatePassword(password) )
        {
            Toast.makeText(this, "Password is weak!\n" +
                    "password must be at least 8 characters long " +
                    "and contain numbers, upper and lowercase letters", Toast.LENGTH_SHORT).show();
            return true;
        } else{
            return false;
        }
    }

}

class newUser
{
    public String username;
    public String phone;
    public String password;

    public String email;

    public newUser(String name, String phoneNumber, String password, String email)
    {
        username = name;
        phone = phoneNumber;
        this.password = password;
        this.email = email;
    }
}
