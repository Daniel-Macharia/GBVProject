package com.example.frats;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class WriteStatement extends AppCompatActivity {

    EditText recipient, statement;

    Button sendStatement;
    private BroadcastReceiver messageSent;
    private BroadcastReceiver messageDelivered;
   // private static final AtomicInteger counter = new AtomicInteger();

    @Override
    protected void onCreate( Bundle SavedInstanceState )
    {
        try{
            super.onCreate( SavedInstanceState );
            setContentView( R.layout.write_statement);



            recipient = findViewById(R.id.recipient);
            sendStatement = findViewById(R.id.sendStatement);
            statement = findViewById(R.id.statement);

            messageSent = new SentMessage();
            registerReceiver(messageSent, new IntentFilter("SMS_SENT") );
            messageDelivered = new DeliveredMessage();
            registerReceiver(messageDelivered, new IntentFilter("SMS_DELIVERED") );

            sendStatement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String rec = recipient.getText().toString();
                    String report = statement.getText().toString();

                    if( MyFirebaseUtilityClass.validatePhone( WriteStatement.this, rec ) )
                    {
                        Toast.makeText(WriteStatement.this, "To: " + rec + "\nStatement: \n " + report, Toast.LENGTH_SHORT).show();
                        sendSMS( rec, report);
                    }
                    else
                    {
                        Toast.makeText(WriteStatement.this, "Please ensure the phone number is valid\n\n" +
                                "should be 10 digits long and \n" +
                                "start with either 07... or 01...", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageSent);
        unregisterReceiver(messageDelivered);
    }

    private void sendSMS( String recipient, String textMessage)
    {
        try{
            if( ActivityCompat.checkSelfPermission( WriteStatement.this, android.Manifest.permission.SEND_SMS )
                    != PackageManager.PERMISSION_GRANTED )
            {
                ActivityCompat.requestPermissions( WriteStatement.this, new String[]{Manifest.permission.SEND_SMS}, 0);
            }

            PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED"), 0);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(recipient, null, textMessage, sentIntent, deliveredIntent);

        }catch( Exception e )
        {
            Toast.makeText( WriteStatement.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
