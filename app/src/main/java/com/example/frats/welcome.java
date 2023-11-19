package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_view);


        LoadLoginTask loginTask = new LoadLoginTask(this);
        Thread t = new Thread(loginTask);
        t.start();


    }

    public void loadLogin()
    {
        Intent intent = new Intent(welcome.this, login.class);
        startActivity(intent);
    }

    class LoadLoginTask extends Thread{
        private welcome w;
        public LoadLoginTask( welcome w )
        {
            this.w = w;
        }

        @Override
        public void run()
        {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            w.loadLogin();
            w.finishAffinity();

        }

    }

}

