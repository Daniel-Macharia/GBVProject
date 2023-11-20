package com.example.frats;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SentMessage extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if( getResultCode() == Activity.RESULT_OK )
        {
            String recipient = "" + intent.getStringExtra("recipient");
           // Toast.makeText(context, "Send Result Okay", Toast.LENGTH_SHORT).show();
            NotifyUser n = new NotifyUser(context.getApplicationContext(),
                    100, "sent to " + recipient, "Your Statement has been sent");
            n.postNotification();
            //MyFirebaseUtilityClass.postNotification(context.getApplicationContext(),
                    //100, "sent to " + recipient, "Your Statement has been sent");
        }else
        {
            //Toast.makeText(context, "Send Failed!", Toast.LENGTH_SHORT).show();
            NotifyUser n = new NotifyUser(context.getApplicationContext(),
                    100, "send Failed", "Your Statement has not been sent!");
            n.postNotification();
           // MyFirebaseUtilityClass.postNotification(context.getApplicationContext(),
                   // 100, "send Failed", "Your Statement has not been sent!");
        }
    }
}
