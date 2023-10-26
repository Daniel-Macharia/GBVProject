package com.example.frats;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;

public class AboutFRATS extends AppCompatActivity {

    TextView info;

    @Override
    protected void onCreate( Bundle SavedInstanceState)
    {
        super.onCreate( SavedInstanceState );
        setContentView(R.layout.about_frats);

        info = findViewById( R.id.fratsInfo);



        try{
            String fratsInfo = "";
            InputStream readTerms = getAssets().open("aboutFrats.txt");
            int size = readTerms.available();

            byte[] buffer = new byte[size];

            readTerms.read(buffer);
            fratsInfo = new String(buffer);
            info.setText(fratsInfo);
        }catch( Exception e )
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
