package com.adamoglu.mobilverikullanimi;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.RemoteException;
import android.widget.Toast;


public class Get_TrafficStats {

    public SharedPreferences SharedPreferences(){
        final SharedPreferences prefSettings =  context.getSharedPreferences("", Context.MODE_PRIVATE);
        return prefSettings;
    }

    Context context;
    public Get_TrafficStats(Context context){
        this.context=context;
    }

    public double Get_Mobile(Context context){

        //return  ((double)(TrafficStats.getMobileRxBytes()+TrafficStats.getMobileTxBytes())/1048576);
        return ((double) (TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes())/1048576);

    }

    public double Get_Total(Context context){

        //return ((double) getAllRxBytesWifi(context)/1024/1024 + getAllRxBytesMobile(context)/1024/1024);
        return ((double) (TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes())/1048576);
    }
    public double Get_Wifi(Context context){

        //return (double) getAllRxBytesWifi(context)/1024/1024;
        return ((double)Get_Total(context)-Get_Mobile(context));
    }


    getSubscriberId id = new getSubscriberId();
    public long getAllRxBytesMobile(Context context) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);

        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    id.getSubscriberId(context,ConnectivityManager.TYPE_MOBILE),
                    SharedPreferences().getLong("currentTimeMillis",0L),
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return 0;
        }
        return bucket.getRxBytes()+bucket.getTxBytes();
    }

    public long getAllRxBytesWifi(Context context) {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);

        NetworkStats.Bucket bucket;
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return 0;
        }
        return bucket.getRxBytes()+bucket.getTxBytes();
    }
}
