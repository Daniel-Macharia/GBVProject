package com.example.frats;

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

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //Thread task = new Thread(new Runnable() {
                    //    @Override
                     //   public void run() {
                            Toast.makeText(PasswordRecovery.this, "Sending mail", Toast.LENGTH_SHORT).show();
                            sendPasswordRecoveryEmail("", "Hello Email");
                            Toast.makeText(PasswordRecovery.this, "After Sending mail", Toast.LENGTH_SHORT).show();
                     //   }
                   // });

                   // task.start();
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

            String senderEmail = "dev.frats@gmail.com";
            String recipientEmail = "ndungudaniel2261@gmail.com";
            //String senderEmailPassword = "FratsDevSupportTeam";
            String senderEmailPassword = "bjvd mkbv eugc qqpk";

            String host = "smtp.gmail.com";

            Toast.makeText(this, "Setting up properties", Toast.LENGTH_SHORT).show();

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "465");
            //properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.ssl.enable", true);
            properties.put("mail.smtp.auth", "true");

            Toast.makeText(this, "After Setting up Properties", Toast.LENGTH_SHORT).show();

            Session  session = Session.getInstance( properties, new Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderEmailPassword );
                }
            });

            Toast.makeText(this, "After Creating Session", Toast.LENGTH_SHORT).show();

            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail) );
            mimeMessage.setSubject("Subject: Frats Password Recovery");
            mimeMessage.setText(code);

            Thread task = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    }catch( Exception e )
                    {
                        Toast.makeText(PasswordRecovery.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            Toast.makeText(this, "Finished sending Email", Toast.LENGTH_SHORT).show();

        }catch( Exception e )
        {
            Toast.makeText(this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
