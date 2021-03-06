package com.sinapsi.android.enginesystem;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.sinapsi.engine.R;
import com.sinapsi.engine.system.NotificationAdapter;

/**
 * NotificationAdapter - implementation for Android platform
 */
public class AndroidNotificationAdapter implements NotificationAdapter {

    private Context context;

    public AndroidNotificationAdapter(Context c){
        context = c;
    }

    @Override
    public void showSimpleNotification(String title, String message) {

        // Build the notification
        Notification n  = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.common_full_open_on_phone) //TODO: change
                .setAutoCancel(true).build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, n);
    }
}
