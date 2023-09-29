package com.example.frats;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class report extends AppCompatActivity {

    Button call,callHotline , sendStatement;
    EditText num,statement;
    static  final int permitPhoneCall = 1;
    TelephonyManager t = null;

    @Override
    protected void onCreate(Bundle savedStateInstance)
    {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.report);

        call = findViewById(R.id.call);
        callHotline = findViewById(R.id.hotline);
        sendStatement = findViewById(R.id.send);
        statement = findViewById(R.id.statementMessage);
        num = findViewById(R.id.num);
       // num.setInputType(InputType.TYPE_CLASS_TEXT);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // String number = policeLine( num.getText().toString() );
                String number = num.getText().toString();
                num.setText(number);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                //Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + number));

                if(ActivityCompat.checkSelfPermission(report.this
                        ,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(),"Permission denied!", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(callIntent);
            }
        });

        callHotline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hotline = "1195";

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:1195"));

                if( ActivityCompat.checkSelfPermission( report.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(intent);
            }
        });

        sendStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = statement.getText().toString();
                Toast.makeText(report.this, msg, Toast.LENGTH_SHORT).show();
                statement.setText("");
            }
        });

        t = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if( isTelephonyEnabled() )
        {
            //register phoneStateListener

            //check for permissions here
            checkForPhonePermission();

        }
        else {
            Toast.makeText(this,"Telephony is not enabled", Toast.LENGTH_SHORT).show();
            //disable calling here
        }

    }

    private boolean isTelephonyEnabled()
    {
        if( t != null)
        {
            if( t.getSimState() == TelephonyManager.SIM_STATE_READY )
            {
                return true;
            }
        }

        return false;
    }
    private void checkForPhonePermission()
    {
        if( ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE )
            != PackageManager.PERMISSION_GRANTED )
        {
            //permission not yet granted
            //ask for permission
           /* Log.d(TAG, "Permission not granted");*/
            ActivityCompat.requestPermissions(this ,
                    new String[] {Manifest.permission.CALL_PHONE}, permitPhoneCall);
        }
        else {
            Toast.makeText(this, "permission to make phone calls is granted", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],int[] grantResult)
    {
        switch( requestCode )
        {
            case permitPhoneCall :
                if( permissions[0].equalsIgnoreCase(Manifest.permission.CALL_PHONE)
                && grantResult[0] ==PackageManager.PERMISSION_GRANTED )
                {
                    //permission granted
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    //permission denied
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
                    //disable calling
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode,permissions,grantResult);
    }

    public String policeLine( String Location)
    {
        String s = "";
        String line = "";

        try {
            InputStream readTerms = getAssets().open("policeLines.txt");

            Scanner in = new Scanner(readTerms);
            while ( in.hasNextLine() )
            {
                String name = "", number = "";
                line = in.nextLine();
                Scanner n = new Scanner( new String(line));
                while( n.hasNext() )
                {
                    String t = n.next();
                    if( t.toLowerCase().equals( t.toUpperCase() ))
                    {
                        //this is a number
                        number = new String( t);
                    }
                    else{
                        name += t + " ";
                    }
                }
                if( name.toLowerCase().contains(Location.toLowerCase()))
                    return number;

                s += name + "  " + number + "\n";

                //s += n.next() + "\n";

            }

            Toast.makeText(this, s, Toast.LENGTH_LONG).show();

        }catch (IOException ioException) {
            Toast.makeText(report.this, ioException.toString(),Toast.LENGTH_SHORT).show();
        }
        return "";
    }


}
