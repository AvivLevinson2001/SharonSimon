package com.example.sharonsimon.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.example.sharonsimon.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;

public class FCMService extends FirebaseMessagingService {

    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(remoteMessage.getNotification() != null){
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Updates")
                    .setSmallIcon(R.drawable.semel_hash)
                    .setColor(getColor(R.color.colorPrimary))
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("Updates", "עדכונים", NotificationManager.IMPORTANCE_HIGH);
                channel.setSound(null, null);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(1,builder.build());
        }
    }
}
