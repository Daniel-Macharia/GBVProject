package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class homeViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    //Button popup;
    private String []text = new String[6];

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_view);

        tv = findViewById(R.id.anime);

        tv.setText("");
        addMessage();

        anime worker = new anime(tv, text);
        worker.start();

      /*  popup = findViewById(R.id.popup);
        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popUp = new PopupMenu( landingViewActivity.this, view);
                MenuInflater inflater = popUp.getMenuInflater();
                inflater.inflate(R.menu.options, popUp.getMenu());
                popUp.show();
            }
        });*/


    }


    private void addMessage()
    {
        text[0] = "What is gender based violence?";
        text[1] = "What are the causes of gender based violence?";
        text[2] = "What are the consequences of gender based violence?";
        text[3] = "Who are the victims of gender based violence?";
        text[4] = "Are men also victims of gender based violence?";
        text[5] = "How can gender based violence be stopped?";

    }

    public void addMenu(View v)
    {
        PopupMenu popUp = new PopupMenu( this, v);
        popUp.setOnMenuItemClickListener(homeViewActivity.this);
        //MenuInflater inflater = popUp.getMenuInflater();
        //inflater.inflate(R.menu.options, popUp.getMenu());
        popUp.inflate(R.menu.options);

        //add a listener for handling the menu click events
        /*popUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {


                if( menuItem.getItemId() == R.id.report ) {
                    //case R.id.report:
                    Toast.makeText(getApplicationContext(), "Reported", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                if( menuItem.getItemId() == R.id.groups ) {
                    //case R.id.report:
                    Toast.makeText(getApplicationContext(), "Groups", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                if( menuItem.getItemId() == R.id.journal ) {
                    //case R.id.report:
                    Toast.makeText(getApplicationContext(), "Journal", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                if( menuItem.getItemId() == R.id.assistant ) {
                    //case R.id.report:
                    Toast.makeText(getApplicationContext(), "Assistants", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                if( menuItem.getItemId() == R.id.facts ) {
                    //case R.id.report:
                    Toast.makeText(getApplicationContext(), "FAQs", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                if( menuItem.getItemId() == R.id.about ) {
                    //case R.id.report:
                    Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Nothing", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });*/

        popUp.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        if( menuItem.getItemId() == R.id.report ) {
            //case R.id.report:
            Toast.makeText(getApplicationContext(), "Report", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( this, report.class);
            startActivity(intent);
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.groups ) {
            //case R.id.report:
            Toast.makeText(getApplicationContext(), "Groups", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.journal ) {
            //case R.id.report:
            Toast.makeText(getApplicationContext(), "Journal", Toast.LENGTH_SHORT).show();
            Intent journalIntent = new Intent( this, journal.class);
            startActivity(journalIntent);
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.assistant ) {
            //case R.id.report:
            Toast.makeText(getApplicationContext(), "Assistants", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.facts ) {
            //case R.id.report:
            Toast.makeText(getApplicationContext(), "FAQs", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.about ) {
            //case R.id.report:
            Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Nothing", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}

class anime extends Thread{
    private String []text = new String[6];
    private TextView tv;
    public anime(TextView tv, String []t)
    {
        for( int i = 0; i < text.length; i++)
            text[i] = t[i];

        this.tv = tv;
    }

    @Override
    public void run()
    {
        while( true )
        {
            for( int i = 0; i < text.length; i++)
            {
                tv.setText(text[i]);
                android.os.SystemClock.sleep(2000);
                //or
                /*

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                 */
            }
        }
    }
}
