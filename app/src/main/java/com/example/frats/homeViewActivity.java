package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class homeViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private String []text = new String[6];

    private TextView tv;

    private boolean isUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
       try{
           super.onCreate(savedInstanceState);

           setContentView(R.layout.home_view);

           tv = findViewById(R.id.anime);

           tv.setText("");
           addMessage();

           Intent intent = getIntent();
           String userOrAssistant = "";
           userOrAssistant = intent.getStringExtra("isUser");

           assert userOrAssistant != null;
           if( userOrAssistant.equals("assistant") )
               isUser = false;

           anime worker = new anime(tv, text);
           worker.start();


       }catch( Exception e )
       {
           Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
       }

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
        popUp.inflate(R.menu.options);

        popUp.show();

        if( !isUser )
        {
            MenuItem m = popUp.getMenu().findItem(R.id.assistant);
            m.setTitle("My Survivors");
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        if( menuItem.getItemId() == R.id.report ) {
            Toast.makeText(getApplicationContext(), "Report", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( this, report.class);
            startActivity(intent);
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.groups ) {
            Toast.makeText(getApplicationContext(), "Groups", Toast.LENGTH_SHORT).show();
            Intent groupIntent = new Intent( homeViewActivity.this, groups.class);
            startActivity(groupIntent);
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.journal ) {
            Toast.makeText(getApplicationContext(), "Journal", Toast.LENGTH_SHORT).show();
            Intent journalIntent = new Intent( this, journal.class);
            startActivity(journalIntent);
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.assistant ) {
            Toast.makeText(getApplicationContext(), "Assistants", Toast.LENGTH_SHORT).show();
            Intent assistantsIntent = new Intent( homeViewActivity.this, assistantsClass.class );
            startActivity( assistantsIntent);
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.facts ) {
            Toast.makeText(getApplicationContext(), "FAQs", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( homeViewActivity.this, AboutFRATS.class );
            startActivity(intent);
            return true;
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Nothing", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    public void loadProfile(View view)
    {
        Intent intent = new Intent(homeViewActivity.this, userProfile.class);
        startActivity(intent);
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
            }
        }
    }
}
