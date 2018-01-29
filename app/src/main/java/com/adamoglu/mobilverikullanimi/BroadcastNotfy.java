package com.adamoglu.mobilverikullanimi;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.Services.ServiceApps_Test;

/**
 * Created by ABRA on 19.09.2017.
 */
public class BroadcastNotfy extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager Main_notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Main_notificationManager.cancel(8);

        ServiceApps_Test.DoNull_data_save();
    }


}