package com.adamoglu.mobilverikullanimi;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.RemoteViews;

import com.adamoglu.mobilverikullanimi.Services.Service_Data;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class Broadcast extends WakefulBroadcastReceiver {

    Context context;
    static AlarmManager mgr;
    static PendingIntent pi;
    public boolean Control_Work=true;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context=context;


        if(!SharedPreferences().getBoolean("AppStop",false)){
            if(LockScreen()){

                Intent i = new Intent(context,Service_Data.class);
                startWakefulService(context,i);

            }

            Intent i = new Intent(context, Broadcast.class);
            pi= PendingIntent.getBroadcast(context, 0, i, 0);
            mgr= (AlarmManager)context.getSystemService(ALARM_SERVICE);
            mgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,5000,pi);

            /*  mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i2=new Intent(context, Broadcast.class);
            pi=PendingIntent.getBroadcast(context, 0, i2, 0);
            mgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,1000,pi);*/


        }

    }


    static NotificationManager Main_notificationManager1=null;
    public void bildirimGonderTest(String strng,int id){


        String ns = Context.NOTIFICATION_SERVICE;
        Main_notificationManager1 = (NotificationManager) context.getSystemService(ns);

        Notification notification = new Notification(android.R.drawable.editbox_dropdown_light_frame, null,
                System.currentTimeMillis());


        RemoteViews notificationView = null;


        notificationView = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout_data);


        notificationView.setTextViewText(R.id.textView_message,strng);



        notification.contentView = notificationView;
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notification.priority = Notification.PRIORITY_MAX;


        Main_notificationManager1.notify(id, notification);

    }

    public void Cancel(){

        mgr.cancel(pi);
        pi.cancel();
    }

    public SharedPreferences SharedPreferences(){
        final SharedPreferences prefSettings =  context.getSharedPreferences("", Context.MODE_PRIVATE);
        return prefSettings;
    }
    public SharedPreferences.Editor Editor(){
        final SharedPreferences prefSettings =  context.getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public boolean LockScreen(){

        boolean isScreenOn;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        try {
            isScreenOn = pm.isScreenOn();
        } catch (Exception e) {
            isScreenOn = pm.isInteractive();
        }

        if( !isScreenOn) {
            //Kapalı
          return false;
        } else {
            //Açık

        return true;
        }
    }

}
