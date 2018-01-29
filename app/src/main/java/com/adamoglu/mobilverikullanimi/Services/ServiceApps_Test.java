package com.adamoglu.mobilverikullanimi.Services;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.Apps.AppUsage;
import com.adamoglu.mobilverikullanimi.BroadcastNotfy;
import com.adamoglu.mobilverikullanimi.R;
import com.adamoglu.mobilverikullanimi.database;
import com.adamoglu.mobilverikullanimi.getSubscriberId;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by ABRA on 30.08.2017.
 */

public class ServiceApps_Test {

    Context context;

    long init;
    long init2;
    long time;

    double Mobil_Data;
    double getPublicData;
    double OldgetPublicData;
    double Data;
    double NewData;
    double Data_Fark;
    double Fark;
    double Delta;

    int now_uid=0;

    double Array_GetData[];

    int percentiles;
    int App_Uid;

    int sayac=0;
    int sayac1=0;
    static int sayac2=0;
    int sayac3=0;

    boolean Control_Work=true;
    boolean Control_First_getUsage=true;
    boolean Control_First_getUsage_Total=true;
    boolean Control_firs =false;
    boolean Control_Entr;
    boolean Control_Entr2=false;
    boolean Control_Array=false;
    boolean Control_AppLoading=false;
    boolean Control_GetApps=true;

    String Name;
    String speed;
    String AppName;

    Drawable AppIcon;

    HashMap<Integer,ApplicationInfo> apps = new HashMap<>();
    HashMap<Integer, Double> old_arrayList_data = new HashMap();

    ArrayList<Integer> getApps = new ArrayList<>();
    List<ApplicationInfo> applist = null;

    static ApplicationInfo data_save;

    NetworkStats networkStatsByApp = null;
    NetworkStatsManager networkStatsManager;
    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
    NetworkStats networkStatsByApp2 = null;
    NetworkStatsManager networkStatsManager2;
    NetworkStats.Bucket bucket2 = new NetworkStats.Bucket();

    getSubscriberId id = new getSubscriberId();
    NowUsing_Test nowUsing = new NowUsing_Test();

    PackageManager packageManager = null;

    NumberFormat numberformat = NumberFormat.getInstance();




    public SharedPreferences.Editor Editor(){
        final SharedPreferences prefSettings =  context.getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }
    public SharedPreferences SharedPreferences(){
        final SharedPreferences prefSettings =  context.getSharedPreferences("", Context.MODE_PRIVATE);
        return prefSettings;
    }

    static boolean Stop_handler_controlApp=false;
    static boolean Stop_handler=false;

    public static void StopServieApps_Test(){

        data_save=null;
        Stop_handler_controlApp=true;
        Stop_handler=true;
        CloseNotification();

    }
    public static void StartServieApps_Test(){

        Stop_handler_controlApp=false;
        Stop_handler=false;

    }


    public static ArrayList<Integer> QuotaApps = new ArrayList<>();
    public void getQuotaApps(){
        database myDb = new database(context);

        Cursor res = myDb.getAllData_Quota();

        QuotaApps.clear();

        if(res.getCount()!=0){
            while (res.moveToNext())
                QuotaApps.add(res.getInt(0));
        }

    }

    public static void StartOneApp(){
        OneApp=true;
        StartServieApps_Test();
        //GetAppsData_OneApp(SharedPreferences().getInt("ShowOneApp",0));
    }
    public static void FinishOneApp(){
        OneApp=false;
        CloseNotification();
    }

    public static boolean ClosedNotfy=false;
    public static void DoNull_data_save(){
        data_save=null;
        ClosedNotfy=true;
    }

    boolean Control_App=true;



    static Handler handler;
    static Handler handler_controlApp;

    public ServiceApps_Test(Context context){
        this.context=context;
        packageManager=context.getPackageManager();

        numberformat.setMinimumFractionDigits(1);
        numberformat.setMaximumFractionDigits(1);

        getQuotaApps();

        handler = new Handler();
        handler.postDelayed(new  Runnable() {

            @Override
            public void run() {

                handler.removeCallbacksAndMessages(null);

                if(!Control_Entr){
                    new LoadApplications().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                if(!Stop_handler)
                    handler.postDelayed(this,2000);
                else
                    Stop_handler=false;

            }
        },1000);

        handler_controlApp = new Handler();
        handler_controlApp.postDelayed(new Runnable() {
            @Override
            public void run() {

                handler_controlApp.removeCallbacksAndMessages(null);

                if(Control_App){
                    printForegroundTask();
                }


                if(!Stop_handler_controlApp)
                    handler_controlApp.postDelayed(this,100);
                else
                    Stop_handler_controlApp=false;
            }
        },1000);

    }



    public boolean getLockScreen(){

        KeyguardManager manager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return !manager.inKeyguardRestrictedInputMode();

    }

    public static boolean OneApp=false;

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {

                Control_Entr=true;

                if(Control_GetApps){
                    Control_GetApps=false;
                    getApps();
                }

                if(Control_Work){

                    init2=System.currentTimeMillis();
                    time=init2-init;
                    init=System.currentTimeMillis();

                    if(time<=0){
                        time=1;
                    }


                    if(getApps.size()!=0)
                        Control_App_OnOff(getApps);


                    if(SharedPreferences().getInt("ShowOneApp",0)==0){
                        if(Control_Entr2){
                            if(data_save !=null){
                                GetAppsData(data_save);
                            }
                        }
                    }else {
                        OneApp=true;
                        GetAppsData_OneApp(SharedPreferences().getInt("ShowOneApp",0));
                    }

                }

                if(!OneApp){
                    if(getLockScreen()){

                        getPublicData=getUsageTotal();
                        Mobil_Data=(getPublicData- OldgetPublicData);
                        OldgetPublicData =getPublicData;

                        if(Mobil_Data>10){//BYTE
                            Control_Work=true;
                            Control_App=true;
                            sayac1 =0;
                        }else {
                            sayac1++;
                            if(sayac1 >15){
                                Control_Work=false;
                                Control_App=false;
                            }
                        }
                    }

                }else {
                    GetAppsData_OneApp(SharedPreferences().getInt("ShowOneApp",0));
                }

                LockScreen();

                Editor().putBoolean("Control_LoadApplications",false).commit();

            }catch (SecurityException ex){

            }

            return null;
        }{

        }

        @Override
        protected void onPostExecute(Void result) {

            Control_Entr=false;

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {


            super.onPreExecute();
        }
    }

    public void LockScreen(){

        boolean isScreenOn;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        try {
            isScreenOn = pm.isScreenOn();
        } catch (Exception e) {
            isScreenOn = pm.isInteractive();
        }

        if( !isScreenOn) {
            //Kapalı
            CloseNotification();
            Control_Work=false;
            Control_App=false;
        }
    }

    public static void CloseNotification(){
        if (Main_notificationManager != null){
            Main_notificationManager.cancel(8);
            sayac2 =0;
        }
    }

    private double getUsageTotal() {

        if(Control_First_getUsage_Total){
            Control_First_getUsage_Total=false;
            networkStatsManager2 = (NetworkStatsManager) context.getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);

        }

        double Data = 0;

        try {

            networkStatsByApp2 = networkStatsManager2.querySummary(id.getType(),id.getSubscriberId(context, ConnectivityManager.TYPE_MOBILE), 0, System.currentTimeMillis());

            do {

                networkStatsByApp2.getNextBucket(bucket2);

                if(bucket2.getState()==2){
                    Data=Data+(bucket2.getTxBytes()+bucket2.getRxBytes());
                }


            } while (networkStatsByApp2.hasNextBucket());

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        networkStatsByApp2.close();

        return Data;
    }

    private void printForegroundTask() {
        String currentApp = "NULL";

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);

            try {
                if (appList != null && appList.size() > 0) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : appList) {
                        mySortedMap.put(usageStats.getLastTimeStamp(), usageStats);
                    }


                    if (mySortedMap != null && !mySortedMap.isEmpty()) {
                        currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    }


                }
            }catch (Exception ex){}


        } else {
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }


        if(getApps.size()>=7){
            getApps.remove(0);

        }

        if(packageManager.getLaunchIntentForPackage(currentApp) != null) {
            Control_Array=false;
            for (int i = 0; i< getApps.size(); i++){
                try {
                    if(getApps.get(i)==context.getPackageManager().getApplicationInfo(currentApp, 0).uid){
                        Control_Array=true;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }


            if(!Control_Array){
                try {
                    getApps.add(context.getPackageManager().getApplicationInfo(currentApp, 0).uid);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void Control_App_OnOff(ArrayList<Integer> app){


        for (int i =0; i<app.size(); i++){
            App_Uid =app.get(i);

            Array_GetData =(getUsage(App_Uid));
            Data_Fark =Array_GetData[0];
            Data =Array_GetData[1]/1048576;


            try {
                if(old_arrayList_data.get(App_Uid)!=null){
                    Fark= Data_Fark - old_arrayList_data.get(App_Uid);
                }
                else{
                    Fark=0.0;
                    old_arrayList_data.put(App_Uid,Data_Fark);
                }

            }catch (Exception ex){
                Fark=0.0;
            }

            try {
                old_arrayList_data.put(App_Uid,Data_Fark);
            }catch (Exception ex){

            }


            if(Fark>=5){

                sayac++;
                if(sayac >0){
                    sayac =0;

                    if(apps.get(App_Uid)==null){
                        if(!Control_AppLoading){
                            data_save=null;
                            getApps();
                            bildirimGonderTest(String.valueOf(App_Uid),10);
                        }

                    }
                    else if(apps.get(App_Uid).loadLabel(packageManager).equals(context.getPackageManager().getNameForUid(App_Uid))){
                        if(!Control_AppLoading){
                            data_save=null;
                            getApps();
                            bildirimGonderTest(String.valueOf(apps.get(App_Uid).loadLabel(packageManager)),11);
                        }

                    }
                    else{

                        app.remove(i);
                        data_save =apps.get(App_Uid);

                        Control_Entr2=true;
                    }
                }

            }

        }

    }

    private void checkForLaunchIntent(List<ApplicationInfo> list) {

        Control_AppLoading=true;

        for(ApplicationInfo info : list) {



            try{
                if(packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    Name = SharedPreferences().getString(String.valueOf(info.loadLabel(packageManager)),"");

                    if(Name.trim().equals("")){

                        Array_GetData =(getUsage(info.uid));
                        Data =Array_GetData[1]/1048576;

                        Editor().putString("Delta"+String.valueOf(info.loadLabel(packageManager)),"0").commit();
                        Editor().putString("Delta2"+String.valueOf(info.loadLabel(packageManager)),"0").commit();
                        Editor().putString(String.valueOf(info.loadLabel(packageManager)),String.valueOf(Data)).commit();
                        Editor().putString("2"+String.valueOf(info.loadLabel(packageManager)),String.valueOf(Data)).commit();
                    }

                    if(apps.get(info.uid)==null)
                        apps.put(info.uid,info);


                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        Control_AppLoading=false;
    }


    public void getApps(){

        checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

    }

    double Old_DataFark=0;
    public void GetAppsData(ApplicationInfo data){


        Control_firs =true;

        if(now_uid!=data.uid){
            sayac2=0;

            NewData=0.0;
            Delta=0.0;
            speed=(String.valueOf(numberformat.format(0))+" B/s");

            nowUsing.ResetData();

            AppIcon=data.loadIcon(packageManager);
            AppName=ControlStringLeng(String.valueOf(data.loadLabel(packageManager)),13);
        }

        now_uid=data.uid;

        Array_GetData =(getUsage(data.uid));
        Data_Fark =Array_GetData[0]-Old_DataFark;
        Data =Array_GetData[1]/1048576;

        Delta = Data - Double.parseDouble(SharedPreferences().getString("Delta2" + String.valueOf(data.loadLabel(packageManager)), "0"));

        if(Delta>0){
            Editor().putString("2"+String.valueOf(data.loadLabel(packageManager)),
                    String.valueOf(
                            Delta+Double.parseDouble(SharedPreferences().getString("2"+String.valueOf(data.loadLabel(packageManager)),"0"))
                    )).commit();
        }
        Editor().putString("Delta2"+String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();

        if(Data_Fark==0){
            sayac3++;
            if(sayac3>5){
                sayac3=0;
                //CloseNotification();
            }

        }else {
            sayac3=0;
        }

        Old_DataFark=Array_GetData[0];

        if(Delta==0){

            bildirimGonder(now_uid,AppName,String.valueOf(percentiles),NewData,nowUsing.getData(), AppIcon, speed);
            speed=(String.valueOf(numberformat.format(0))+" B/s");

        }else {

            speed=String.valueOf(numberformat.format(Unit_Converter(1000*Delta/time)))+Data_Str;

            NewData= Double.parseDouble(SharedPreferences().getString("2"+String.valueOf(data.loadLabel(packageManager)),"0"));

            percentiles= (int) (NewData*100/(Double.parseDouble(SharedPreferences().getString("USE_DATA_MB","0"))));
            if(percentiles<=0){
                percentiles=0;
            }else if(percentiles>100) {
                percentiles=99;
            }

            nowUsing.Data(NewData);
            bildirimGonder(now_uid,AppName,String.valueOf(percentiles),NewData,nowUsing.getData(), AppIcon, speed);

        }


    }

    public void GetAppsData_OneApp(int uid){


        if(apps.get(uid)!=null){

            if(now_uid!=uid){
                NewData=0.0;
                Delta=0.0;
                speed=(String.valueOf(numberformat.format(0))+" B/s");

                nowUsing.ResetData();

                AppIcon=apps.get(uid).loadIcon(packageManager);
                AppName=ControlStringLeng(String.valueOf(apps.get(uid).loadLabel(packageManager)),13);
            }
            now_uid=uid;
            Array_GetData =(getUsage(uid));
            Data =Array_GetData[1]/1048576;
            Delta = Data - Double.parseDouble(SharedPreferences().getString("Delta2" + String.valueOf(apps.get(uid).loadLabel(packageManager)), "0"));

            if(Delta>0){
                Editor().putString("2"+String.valueOf(apps.get(uid).loadLabel(packageManager)),
                        String.valueOf(
                                Delta+Double.parseDouble(SharedPreferences().getString("2"+String.valueOf(apps.get(uid).loadLabel(packageManager)),"0"))
                        )).commit();
            }
            Editor().putString("Delta2"+String.valueOf(apps.get(uid).loadLabel(packageManager)),String.valueOf(Data)).commit();

            speed=String.valueOf(numberformat.format(Unit_Converter(1000*Delta/time)))+Data_Str;

            NewData= Double.parseDouble(SharedPreferences().getString("2"+String.valueOf(apps.get(uid).loadLabel(packageManager)),"0"));

            percentiles= (int) (NewData*100/(Double.parseDouble(SharedPreferences().getString("USE_DATA_MB","0"))));
            if(percentiles<=0){
                percentiles=0;
            }else if(percentiles>100) {
                percentiles=99;
            }

            nowUsing.Data(NewData);
            bildirimGonder(now_uid,AppName,String.valueOf(percentiles),NewData,nowUsing.getData(), AppIcon, speed);


        }else {

        }

    }

    public String ControlStringLeng(String str, int lengt){

        if(str.length()>=lengt){
            str=str.substring(0,lengt);
            str=str+"..";
        }

        return str;
    }



    private double[] getUsage(int packageUid) {

        if(Control_First_getUsage){
            Control_First_getUsage=false;
            networkStatsManager = (NetworkStatsManager) context.getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);

        }

        double Data;
        double currentUsage[] = new double[2];
        boolean control_Exit=false;
        try {

            networkStatsByApp = networkStatsManager.querySummary(id.getType(),id.getSubscriberId(context, ConnectivityManager.TYPE_MOBILE), 0, System.currentTimeMillis());

            do {

                networkStatsByApp.getNextBucket(bucket);

                Data=(bucket.getTxBytes()+bucket.getRxBytes());

                if (bucket.getUid() == packageUid) {
                    //rajeesh : in some devices this is immediately looping twice and the second iteration is returning correct value. So result returning is moved to the end.
                    if(bucket.getState()==2){
                        currentUsage[0] =Data;
                        control_Exit=true;
                    }
                    currentUsage[1]=currentUsage[1]+Data;

                    if(control_Exit)
                        break;

                }
            } while (networkStatsByApp.hasNextBucket());

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        networkStatsByApp.close();

        return currentUsage;
    }

    String Data_Str="";
    String Data_Str_Second="";

    public double Unit_Converter(Double Data){
        //Verilerin birimlerini dönüştürür
        try {
            if(Data <0.98){
                Data = Data *1024;
                Data_Str =" KB";
                Data_Str_Second = " KB/s";
                if(Data <1){
                    Data = Data *1024;
                    Data_Str =" B";
                    Data_Str_Second = " B/s";
                }
            }
            else if(Data >999){
                Data = Data /1024;
                Data_Str =" GB";
                Data_Str_Second = " GB/s";
                if(Data>999){
                    Data=Data/1024;
                    Data_Str_Second =" TB/s";
                    Data_Str =" TB";
                    if(Data>999){
                        Data=Data/1024;
                        Data_Str_Second =" PB/s";
                        Data_Str =" PB";
                    }
                }
            }
            else{
                Data_Str = " MB";
                Data_Str_Second = " MB/s";
            }

        }catch (Exception ex){
            Toast.makeText(context, "Unit_Converter(Double)'de Hata", Toast.LENGTH_SHORT).show();
            return 0;
        }

        return Data;
    }



    static NotificationManager Main_notificationManager=null;
    public void bildirimGonder(int uid,String AppName, String Percent, double AppData, double NowUsing, Drawable AppIcon, String speed){

        if(!ClosedNotfy){
            sayac2++;
            if(sayac2 >1){

                if(AppData>=997){
                    AppData=AppData/1024;
                }

                String ns = Context.NOTIFICATION_SERVICE;
                Main_notificationManager = (NotificationManager) context.getSystemService(ns);


                Notification notification = new Notification(R.drawable.icon_apps, null,
                        System.currentTimeMillis());


                RemoteViews notificationView = null;

                if(OneApp){
                    notificationView = new RemoteViews(context.getPackageName(),
                            R.layout.notification_layout_oneapp);
                }else {
                    notificationView = new RemoteViews(context.getPackageName(),
                            R.layout.notification_layout_app);
                }


                String QuotaDataShow="";
                double QuotaData= Double.parseDouble(SharedPreferences().getString("QuotaApp"+uid,"0"));

                if(QuotaData==0){
                    QuotaDataShow="";
                }else {
                    QuotaDataShow=" / "+numberformat.format(Unit_Converter(QuotaData))+Data_Str+" Kota Dışı";
                }

                boolean Control=true;


                for(int i=0;i<QuotaApps.size();i++)
                    if(QuotaApps.get(i)==uid){

                        if(OneApp){
                            notificationView = new RemoteViews(context.getPackageName(),
                                    R.layout.notification_layout_noquota_oneapp);
                        }else {

                            notificationView = new RemoteViews(context.getPackageName(),
                                    R.layout.notification_layout_noquota_app);
                        }
                        Control=false;
                        notificationView.setTextViewText(R.id.app_name,AppName+"  "+numberformat.format(Unit_Converter(QuotaData))+Data_Str);
                        notificationView.setTextViewText(R.id.textView_message,speed+" ");
                        notificationView.setImageViewBitmap(R.id.app_icon,((BitmapDrawable) AppIcon).getBitmap());
                        notificationView.setImageViewResource(R.id.imageView_close,R.drawable.icon_close);

                    }



                if(Control){
                    notificationView.setTextViewText(R.id.app_name,AppName+"  "+numberformat.format(Unit_Converter(AppData))+Data_Str+QuotaDataShow);
                    notificationView.setTextViewText(R.id.textView_useNow,"Şuan Kullanılan "+numberformat.format(Unit_Converter(NowUsing))+Data_Str);

                    notificationView.setTextViewText(R.id.textView_message,speed+"  "+Percent+"%  ");
                    notificationView.setImageViewBitmap(R.id.app_icon,((BitmapDrawable) AppIcon).getBitmap());
                    notificationView.setImageViewResource(R.id.imageView_close,R.drawable.icon_close);
                    notificationView.setProgressBar(R.id.progressBar,  100, Integer.parseInt(Percent),false);

                }

                Intent notificationIntent = new Intent(context, AppUsage.class);
                PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0,
                        notificationIntent, 0);
                notification.contentIntent = pendingNotificationIntent;

                Intent switchIntent = new Intent(context, BroadcastNotfy.class);
                PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0,
                        switchIntent, 0);

                notificationView.setOnClickPendingIntent(R.id.imageView_close,
                        pendingSwitchIntent);


                notification.contentView = notificationView;
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                notification.priority = Notification.PRIORITY_DEFAULT;


                Main_notificationManager.notify(8, notification);

            }else {
                if(Main_notificationManager!=null)
                    Main_notificationManager.cancel(8);
            }
        }else
            ClosedNotfy=false;

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
}

class NowUsing_Test{
    static double oldData =0;
    static double Data=0;

    public void Data(double Data){
        if(oldData!=0)
            this.Data=this.Data+(Data- oldData);
        oldData=Data;


    }

    public double getData() {
        if(Data<=0)
            return 0;
        else
            return Data;
    }

    public void ResetData(){
        Data=0;
        oldData=0;
    }
}
