package com.example.frats;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class NotifyUser {
    private Context context;
    private int id;
    private String title;
    private String text;
    public NotifyUser(Context context, int id, String title, String text)
    {
        this.context = context;
        this.id = id;
        this.title = title;
        this.text = text;
    }

    public void postNotification()
    {
        String channelId = "thisChannelId";
        String channelName = "thisChannelName";
        NotificationManager manager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT );
            manager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder( context, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon( R.mipmap.icon );

        if(ActivityCompat.checkSelfPermission( context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED )
        {
            manager.notify(id, builder.build() );
        }else{
            // ActivityCompat.requestPermissions( context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1 );
        }
    }
}
