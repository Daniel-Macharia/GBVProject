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

        MyFirebaseUtilityClass.updateAllGroups( getApplicationContext() );
        MyFirebaseUtilityClass.updateAllChats( getApplicationContext() );

        return Result.success();
    }
}
