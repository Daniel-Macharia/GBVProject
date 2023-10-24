package com.example.frats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class termsAndConditions extends AppCompatActivity {
    Button toLogin;
    TextView termsOfUse;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_and_conditions);

        toLogin = findViewById(R.id.accept);
        termsOfUse = findViewById(R.id.termsOfUse);

        String terms = "";

        try {
            InputStream readTerms = getAssets().open("frats_terms_and_conditions.txt");
            int size = readTerms.available();

            byte[] buffer = new byte[size];

            readTerms.read(buffer);
            terms = new String(buffer);

        }catch (IOException ioException) {
            Toast.makeText(termsAndConditions.this, ioException.toString(),Toast.LENGTH_SHORT).show();
        }

        termsOfUse.setText(terms);


        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadWelcomeView();
            }
        });
    }

    public void makeFirebaseWorkRequest()
    {

        try{
            Toast.makeText(termsAndConditions.this, "Setting work request" , Toast.LENGTH_SHORT).show();

            Constraints c = new Constraints.Builder()
                    .setRequiredNetworkType( NetworkType.CONNECTED )
                    .build();

            //OneTimeWorkRequest request = new OneTimeWorkRequest.Builder( FirebaseWorker.class )
              //      .build();
            PeriodicWorkRequest request = new PeriodicWorkRequest.Builder( FirebaseWorker.class, 15, TimeUnit.MINUTES)
                    .build();

            //WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork( "SyncWithFirebase", ExistingPeriodicWorkPolicy.KEEP, request );
            WorkManager.getInstance(getApplicationContext()).enqueue(request);

            WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData( request.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {

                            if( workInfo.getState() != null )
                            {
                                MyFirebaseUtilityClass.postNotification(getApplicationContext(),"Work Request State", workInfo.getState().name());
                                Toast.makeText(termsAndConditions.this, "Status changed " + workInfo.getState().name(), Toast.LENGTH_SHORT).show();

                            }

                            //if( workInfo.getState().isFinished() )
                              //  WorkManager.getInstance(getApplicationContext()).enqueue(request);
                        }
                    });
            Toast.makeText(termsAndConditions.this, "After setting work request" , Toast.LENGTH_SHORT).show();



        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }


    public void loadWelcomeView()
    {
       // makeFirebaseWorkRequest();

       // FirebaseWorkRequestTask task = new FirebaseWorkRequestTask(this);
        //Thread t = new Thread(task);
        //t.start();

        Intent welcomeIntent = new Intent( termsAndConditions.this, welcome.class);
        startActivity(welcomeIntent);
        finish();
    }


}
