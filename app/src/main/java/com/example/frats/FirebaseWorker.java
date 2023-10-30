package com.example.frats;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class FirebaseWorker extends Worker {

    public FirebaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        try{
            Context c = getApplicationContext();
            Toast.makeText(c, "Doing work" , Toast.LENGTH_SHORT).show();

            MyFirebaseUtilityClass.postNotification(getApplicationContext(), 2,"working " , "doWork() executing");

            Toast.makeText(getApplicationContext(), "Finished doing work" , Toast.LENGTH_SHORT).show();
        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), "Error in doWork(): " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        return Result.success();
    }

}

class WorkerThread implements Runnable
{
    private Context context;
    WorkerThread(Context context)
    {
        this.context = context;
    }

    @Override
    public void run()
    {
        MyFirebaseUtilityClass.updateAllChats(context);
        MyFirebaseUtilityClass.updateAllGroups(context);
    }
}
