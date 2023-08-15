package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class createUser extends AppCompatActivity {

    Button create;

    @Override
    protected void onCreate(Bundle savedStateInstance)
    {
        super.onCreate(savedStateInstance);
        setContentView(R.layout.create_user);

        create = findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                welcomeAndLoadLogin w = new welcomeAndLoadLogin(createUser.this);
                w.start();
            }
        });
    }

    public void loadWelcome()
    {
        Intent welcomeIntent = new Intent( createUser.this, welcome.class);
        startActivity(welcomeIntent);
    }

    public void loadLogin()
    {
        Intent intent = new Intent(createUser.this, login.class);
        startActivity(intent);
    }

}

class welcomeAndLoadLogin extends Thread{

    private createUser user;

    public welcomeAndLoadLogin(createUser user)
    {
        this.user = user;
    }

    @Override
    public void run()
    {

        user.loadWelcome();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        user.loadLogin();
    }
}

