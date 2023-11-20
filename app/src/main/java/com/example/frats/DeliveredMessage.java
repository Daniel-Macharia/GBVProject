package com.example.frats;

import static android.nfc.NfcAdapter.EXTRA_ID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeliveredMessage extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //long id = intent.getLongExtra( EXTRA_ID, -1);
        //long protocal_id = intent.getLongExtra(EXTRA_PROTOCAL, -1)

        if( getResultCode() == Activity.RESULT_OK )
        {
            String recipient = "" + intent.getStringExtra("recipient");
            //Toast.makeText(context, "Delivered Result Ok", Toast.LENGTH_SHORT).show();
            NotifyUser n = new NotifyUser( context.getApplicationContext(),
                    110, "Delivered to " + recipient, "Your Statement has been delivered");
            n.postNotification();
            //MyFirebaseUtilityClass.postNotification(context.getApplicationContext(),
            //        110, "Delivered to " + recipient, "Your Statement has been delivered");
        }else {
            //Toast.makeText(context, "Delivered Result not okay", Toast.LENGTH_SHORT).show();
            NotifyUser n = new NotifyUser(context.getApplicationContext(),
                    110, "Delivery Failure", "Your Statement has not been delivered!");
            n.postNotification();
            //MyFirebaseUtilityClass.postNotification(context.getApplicationContext(),
               //     110, "Delivery Failure", "Your Statement has not been delivered!");
        }

    }
}
