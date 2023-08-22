package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class assistantsClass extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assistants);

    }

    public void loadChat(View view)
    {
        Intent chatIntent = new Intent( assistantsClass.this, chat.class);
        startActivity(chatIntent);
    }
}
