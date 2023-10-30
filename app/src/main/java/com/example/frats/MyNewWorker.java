package com.example.frats;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyNewWorker extends Worker {

    public MyNewWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        //MyFirebaseUtilityClass.postNotification( getApplicationContext(), 1,"Hello I'm the new Worker", " just " +
       //         "doing my job");
        MyFirebaseUtilityClass.updateAllGroups( getApplicationContext() );
        MyFirebaseUtilityClass.updateAllChats( getApplicationContext() );

       // MyFirebaseUtilityClass.postNotification( getApplicationContext(), 101,"Hello I'm the new Worker", " just " +
           //     "doing my job" +
          //      "After Updating All Chats");

        return Result.success();
    }
}
