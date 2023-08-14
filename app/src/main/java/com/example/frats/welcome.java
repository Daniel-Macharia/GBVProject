package com.example.frats;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_view);

        //android.os.SystemClock.sleep(3000);

        loadHomeView();

    }

    private void sleep()
    {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadHomeView()
    {
        Intent intent = new Intent(this, homeViewActivity.class);
        startActivity(intent);
        //setContentView(R.layout.home_view);
    }
}
