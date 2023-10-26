package com.example.frats;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class homeViewActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    //Button popup;
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
           String userOrAssistant = intent.getStringExtra("isUser");

           if( userOrAssistant.equals("assistant") )
               isUser = false;
           else
               isUser = true;

           anime worker = new anime(tv, text);
           worker.start();

           //makeFirebaseWorkRequest();

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
            //case R.id.report:
            //policeLine("");
            Toast.makeText(getApplicationContext(), "Report", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent( this, report.class);
            startActivity(intent);
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.groups ) {
            //case R.id.report:
            Toast.makeText(getApplicationContext(), "Groups", Toast.LENGTH_SHORT).show();
            Intent groupIntent = new Intent( homeViewActivity.this, groups.class);
            startActivity(groupIntent);
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

            Intent assistantsIntent = new Intent( homeViewActivity.this, assistantsClass.class );
            startActivity( assistantsIntent);
            return true;
        }
        else
        if( menuItem.getItemId() == R.id.facts ) {
            //case R.id.report:
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


    public void makeFirebaseWorkRequest()
    {

        try{
            Toast.makeText(getApplicationContext(), "Setting work request" , Toast.LENGTH_SHORT).show();

            Constraints c = new Constraints.Builder()
                    .setRequiredNetworkType( NetworkType.CONNECTED )
                    .build();

            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder( FirebaseWorker.class )
                  .build();
            //PeriodicWorkRequest request = new PeriodicWorkRequest.Builder( FirebaseWorker.class, 15, TimeUnit.MINUTES)
             //       .build();

           // WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork( "SyncWithFirebase", ExistingPeriodicWorkPolicy.KEEP, request );
            WorkManager.getInstance(getApplicationContext()).enqueue(request);

            WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData( request.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {

                            if( workInfo.getState() != null )
                            {
                                MyFirebaseUtilityClass.postNotification(getApplicationContext(),"Work Request State", new String( workInfo.getState().name() ) );
                                Toast.makeText(getApplicationContext(), "Status changed " + workInfo.getState().name(), Toast.LENGTH_SHORT).show();

                            }

                            //if( workInfo.getState().isFinished() )
                            //  WorkManager.getInstance(getApplicationContext()).enqueue(request);
                        }
                    });
            Toast.makeText(getApplicationContext(), "After setting work request" , Toast.LENGTH_SHORT).show();



        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
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
