package com.adamoglu.mobilverikullanimi;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ABRA on 17.08.2017.
 */

public class SaveApps {

    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = context.getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = context.getSharedPreferences("",0);
        return prefSettings;
    }

    Context context;
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    database myDb;

    public SaveApps(Context context){
        this.context=context;
        myDb = new database(context);
        packageManager = context.getApplicationContext().getPackageManager();
        new LoadApplications().execute();

    }


    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> appList= new ArrayList<>();

        for(ApplicationInfo info : list) {
            try{
                if(packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    appList.add(info);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return appList;
    }

    String Name;
    Double Data;
    int color;
    String saveIcon;

    String a;

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


                applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));



                for (int i=0; i<applist.size(); i++){
                    ApplicationInfo data = applist.get(i);



                    Name = SharedPreferences().getString(String.valueOf(data.loadLabel(packageManager)),"");

                    if(Name.trim().equals("")){
                        Data=getUsage(data.uid);
                        Editor().putString("Delta"+String.valueOf(data.loadLabel(packageManager)),"0").commit();
                        Editor().putString("Delta2"+String.valueOf(data.loadLabel(packageManager)),"0").commit();
                        Editor().putString(String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();
                        Editor().putString("2"+String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();
                    }


                    Drawable icon = data.loadIcon(packageManager);
                    color = calculateAverageColor(((BitmapDrawable) icon).getBitmap(),5);

                    Bitmap bitmap = ((BitmapDrawable)icon).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();

                    saveIcon  = Base64.encodeToString(bitmapdata, Base64.DEFAULT);

                    Editor().putString(String.valueOf(data.uid)+"AppName",String.valueOf(data.loadLabel(packageManager))).commit();
                    Editor().putString(String.valueOf(data.uid)+"AppIcon",saveIcon).commit();
                    Editor().putInt(String.valueOf(data.uid)+"AppColor",color).commit();

                    a=String.valueOf(data.loadLabel(packageManager));

                }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Toast.makeText(context, a, Toast.LENGTH_SHORT).show();

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {



            super.onPreExecute();
        }
    }


    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        }
        return "";
    }

    private double getUsage(int packageUid) {
        NetworkStats networkStatsByApp = null;
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);


        long currentUsage = 0L;
        try {
            networkStatsByApp = networkStatsManager.querySummary(ConnectivityManager.TYPE_WIFI, getSubscriberId(context.getApplicationContext(), ConnectivityManager.TYPE_WIFI), 0, System.currentTimeMillis());
            do {

                NetworkStats.Bucket bucket = new NetworkStats.Bucket();

                networkStatsByApp.getNextBucket(bucket);

                if (bucket.getUid() == packageUid) {
                    //rajeesh : in some devices this is immediately looping twice and the second iteration is returning correct value. So result returning is moved to the end.


                    currentUsage =currentUsage+(bucket.getRxBytes() + bucket.getTxBytes());
                }
            } while (networkStatsByApp.hasNextBucket());

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        networkStatsByApp.close();

        return currentUsage;
    }


    public int calculateAverageColor(Bitmap bitmap, int pixelSpacing) {
        //Resmin ortak rengini bulur
        int R = 0; int G = 0; int B = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];

        try {
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            for (int i = 0; i < pixels.length; i += pixelSpacing) {
                int color = pixels[i];
                R += Color.red(color);
                G += Color.green(color);
                B += Color.blue(color);
                n++;
            }
        }catch (Exception ex){
            Toast.makeText(context.getApplicationContext(), "calculateAverageColor(Bitmap,int)'de Hata", Toast.LENGTH_SHORT).show();
            return Color.rgb(96,123,139);
        }

        return Color.rgb(R / n, G / n, B / n);
    }
}
