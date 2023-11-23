package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class PasswordRecovery extends AppCompatActivity {


    private Button resend, ok;
    private EditText enteredCode;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        try {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.password_recovery);

            resend = findViewById(R.id.resendCode);
            ok = findViewById(R.id.confirmCode);
            enteredCode = findViewById( R.id.enteredCode);

            user u = new user( this );
            u.open();
            String email = u.getEmail();
            u.close();

            String sentCode = "" + (int) ( 100000 + Math.random() * 900000 );

            if( MyFirebaseUtilityClass.isRoamingNetwork( PasswordRecovery.this ) )
            {
                sendPasswordRecoveryEmail(email, sentCode);
            }
            else
            {
                Toast.makeText(this, "No active internet connection!\nPlease check your internet connection", Toast.LENGTH_SHORT).show();
            }

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String code = "";
                    code = enteredCode.getText().toString();

                    if( code.equals( sentCode ) )
                    {
                        Intent intent = new Intent( PasswordRecovery.this, CreateNewPassword.class );
                        startActivity( intent );
                    }
                    else{
                        Toast.makeText(PasswordRecovery.this, "Incorrect code!\nClick resend to send another code", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newSentCode = "" + (int) ( 100000 + Math.random() * 900000 );

                    if( MyFirebaseUtilityClass.isRoamingNetwork( getApplicationContext() ) )
                    {
                        sendPasswordRecoveryEmail(email, newSentCode);
                    }else
                    {
                        Toast.makeText(PasswordRecovery.this, "No active internet connection!\nPlease check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch( Exception e )
        {
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void sendPasswordRecoveryEmail( String userEmail, String code)
    {
        try{
            if( userEmail.equals("") )
            {
                Toast.makeText(this, "No registered Email!", Toast.LENGTH_SHORT).show();
                return;
            }

            String senderEmail = "dev.frats@gmail.com";
            String senderEmailPassword = "bjvd mkbv eugc qqpk";
            String host = "smtp.gmail.com";

            //Toast.makeText(this, "Setting up properties", Toast.LENGTH_SHORT).show();

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "465");
            //properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.ssl.enable", true);
            properties.put("mail.smtp.auth", true);

            //Toast.makeText(this, "After Setting up Properties", Toast.LENGTH_SHORT).show();

            Session  session = Session.getInstance( properties, new Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderEmailPassword );
                }
            });

           // Toast.makeText(this, "After Creating Session", Toast.LENGTH_SHORT).show();

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail) );
            mimeMessage.setSubject("Subject: Frats Password Recovery");
            mimeMessage.setText(code);

            Thread task = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                        //Transport.send(mimeMessage, senderEmail, senderEmailPassword);
                    }catch( Exception e )
                    {
                        Toast.makeText(PasswordRecovery.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

            task.start();

            //Toast.makeText(this, "Finished sending Email", Toast.LENGTH_SHORT).show();

        }catch( Exception e )
        {
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
