package com.example.frats;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class entryLayout extends AppCompatActivity {

    public TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_layout);

        tv = findViewById(R.id.entry);

    }

    public void clicked(View v)
    {

    }
}
