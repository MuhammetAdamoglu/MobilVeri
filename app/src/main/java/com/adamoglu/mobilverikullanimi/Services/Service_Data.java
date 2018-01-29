package com.adamoglu.mobilverikullanimi.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.Broadcast;
import com.adamoglu.mobilverikullanimi.Get_TrafficStats;
import com.adamoglu.mobilverikullanimi.OneFragment;
import com.adamoglu.mobilverikullanimi.R;
import com.adamoglu.mobilverikullanimi.Tabs;
import com.adamoglu.mobilverikullanimi.ThreeFragment;
import com.adamoglu.mobilverikullanimi.Time;
import com.adamoglu.mobilverikullanimi.database;
import com.adamoglu.mobilverikullanimi.getData;
import com.adamoglu.mobilverikullanimi.getSubscriberId;
import com.adamoglu.mobilverikullanimi.getWifiData;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Service_Data extends Service {

    private String Data_Str;
    private String Data_Str_Second;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    NumberFormat numberformat = NumberFormat.getInstance();
    database myDb = new database(this);
    com.adamoglu.mobilverikullanimi.getData getData = new getData();

    boolean Control_Notification=true;

    int wifi_max=1;

    double GetMobilData;
    double GetWifiData;
    double Delta;
    double Delta_Wifi;
    double RemainingData;
    double OldData;
    double Remaining;
    String RemainingStr;

    double Daily_UseData;
    double UseData_wifi;
    double Speed;
    double MyData;
    double UseDataPlus;
    double DailyData;

    long CountDay;
    long CountDay2;

    Time time = new Time();

    Context context;

    String WifiName;
    String WifiMac;

    double WifiData;
    int sayac_test=0;

    public SharedPreferences.Editor Editor(){
        final SharedPreferences prefSettings =  getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }
    public SharedPreferences SharedPreferences(){
        final SharedPreferences prefSettings =  getSharedPreferences("", Context.MODE_PRIVATE);
        return prefSettings;
    }


    public long Control_Day(Calendar now, int Year, int Month, int Day){

        Date FirstDay = new GregorianCalendar(Year, Month, Day, 00, 00).getTime();
        Date AtTheMoment = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 00, 00).getTime();


        long Difference = FirstDay.getTime() - AtTheMoment.getTime();

        if(Year==0){
            return 1;
        }
        return Difference / (1000 * 60 * 60 * 24);

    }

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
            Toast.makeText(getApplicationContext(), "Unit_Converter(Double)'de Hata", Toast.LENGTH_SHORT).show();
            return 0;
        }

        return Data;
    }

    public double Speed(){
        if(Delta>0){
            return Delta;
        }
        else {
            return 0;
        }

    }

    public double Speed_Wifi(){
        if(Delta_Wifi>0){
            return Delta_Wifi;
        }
        else {
            return 0;
        }

    }

    static boolean ResApp=false;
    public void ResApp(){
        ResApp=true;
    }


    double Old_ExitQuotaApp=0;
    double New_ExitQuotaApp=0;

    public double ExitQuotaApp(){
        double data = 0;
        double appdata=0;
        double appdelta=0;

        Cursor res =myDb.getAllData_Quota();
        if(res.getCount()!=0){
            while (res.moveToNext()){
                appdata=getUsage(res.getInt(0))/1024/1024;

                if(ResApp){
                    Editor().putString("QuotaApp_Delta"+res.getInt(0), String.valueOf(appdata)).commit();
                }else {
                    if(!SharedPreferences().getString("QuotaApp_Delta"+res.getInt(0),"").equals(""))
                        appdelta= appdata-Double.parseDouble(SharedPreferences().getString("QuotaApp_Delta"+res.getInt(0),""));
                    Editor().putString("QuotaApp_Delta"+res.getInt(0), String.valueOf(appdata)).commit();

                    if(appdelta>0){
                        Editor().putString("QuotaApp"+res.getInt(0), String.valueOf(Double.parseDouble(SharedPreferences().getString("QuotaApp"+res.getInt(0),"0"))+appdelta)).commit();
                    }


                }

                data= data + appdata;
            }

        }

        if(ResApp){
            Editor().putString("Old_ExitQuotaApp", String.valueOf(data)).commit();
            ResApp=false;
            return 0;
        }else {
            Old_ExitQuotaApp= Double.parseDouble(SharedPreferences().getString("Old_ExitQuotaApp","0"));
            if(Old_ExitQuotaApp>0){
                New_ExitQuotaApp=data-Old_ExitQuotaApp;
            }

            Editor().putString("Old_ExitQuotaApp", String.valueOf(data)).commit();
            Editor().putString("New_ExitQuotaApp", String.valueOf(New_ExitQuotaApp)).commit();
        }

        if(New_ExitQuotaApp<=0)
            return 0;
        else
            return New_ExitQuotaApp;
    }

    public void Saves(){
        Editor().putString("USE_DATA_MB",String.valueOf(UseDataPlus)).commit();
        Editor().putString("DAILY_USEDATA_MB",String.valueOf(Daily_UseData)).commit();
        Editor().putString("SPEED",numberformat.format(Unit_Converter(Speed))+Data_Str_Second).commit();
        Editor().putString("DAY", String.valueOf(CountDay)).commit();
    }
    public void Saves_Wifi(){
        Editor().putString("USE_DATA_WIFI",String.valueOf(Unit_Converter(UseData_wifi))).commit();
        Editor().putString("USE_DATA_WIFI_TYPE",Data_Str).commit();
    }

    long init, init2, time_ms;

    Get_TrafficStats get_trafficStats = new Get_TrafficStats(this);


    Calendar now;

    static boolean Setting_SüreDolunca_Hicbirseyyapma =false;
    static boolean Setting_Tekrarla =true;
    static boolean Setting_Durdur =false;

    static boolean Setting_UygulamaGoster =true;
    static boolean Setting_SecilenUygulamaGoster =false;

    static boolean Setting_SürekliKullanim = true;
    static boolean Setting_Günlükveri = true;
    static boolean Setting_Veri = true;
    static boolean Setting_Uygulamatekrarlandi = true;
    static boolean Setting_Baglantikoptu = true;
    static boolean Setting_Baglantigeldi = true;

    public static void SetSetting(Context context){

        final SharedPreferences prefSettings =  context.getSharedPreferences("", Context.MODE_PRIVATE);

        Setting_Tekrarla = prefSettings.getBoolean("Setting_Tekrarla",true);
        Setting_SüreDolunca_Hicbirseyyapma = prefSettings.getBoolean("Setting_Hicbirseyyapma",false);
        Setting_Durdur = prefSettings.getBoolean("Setting_Durdur",false);

        Setting_UygulamaGoster = prefSettings.getBoolean("Setting_UygulamaKullanimi",true);
        Setting_SecilenUygulamaGoster = prefSettings.getBoolean("Setting_SecilenUygulamaGöster",false);

        Setting_SürekliKullanim=prefSettings.getBoolean("Setting_SürekliKullanim",true);
        Setting_Günlükveri=prefSettings.getBoolean("Setting_Günlükveri",true);
        Setting_Veri=prefSettings.getBoolean("Setting_Veri",true);
        Setting_Uygulamatekrarlandi=prefSettings.getBoolean("Setting_Uygulamatekrarlandi",true);
        Setting_Baglantikoptu=prefSettings.getBoolean("Setting_Baglantikoptu",false);
        Setting_Baglantigeldi=prefSettings.getBoolean("Setting_Baglantigeldi",false);

    }

    public void SetDatas(){
        MyData = Double.parseDouble(SharedPreferences().getString("MYDATA","0"));
        DailyData= Double.parseDouble(SharedPreferences().getString("DAILY_DATA_MB","0"));
        First_For_Data = SharedPreferences().getBoolean("First_For_Data",true);
    }

    boolean First_For_Data=true;
    static boolean Exit=false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Exit=true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!inCode)
            new LoadCode().execute();

        return START_STICKY;
    }

    boolean inCode=false;




    private class LoadCode extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {


                inCode=true;

                now= Calendar.getInstance();

                CountDay= Control_Day(now,SharedPreferences().getInt("YIL2",0),SharedPreferences().getInt("AY2",0),SharedPreferences().getInt("GUN2",0));
                CountDay2=CountDay;

                if(CountDay>0){
                    Editor().putBoolean("Viewed_FinishDate",true).commit();
                    Editor().putBoolean("Viewed_StopApp",true).commit();
                }

                if(Setting_SüreDolunca_Hicbirseyyapma){
                    //Süre Doldu,Devam Ediyor

                    if(CountDay2<=0){
                        CountDay=0;
                        if(SharedPreferences().getBoolean("Viewed_FinishDate",true)){
                            Editor().putBoolean("Viewed_FinishDate",false).commit();
                            bildirimGonder(
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    0,0,
                                    0,0,6);
                        }

                    }
                }else if(Setting_Tekrarla) {
                    //Uygulama Tekrarlanacak

                    if(CountDay2<=0){
                        CloseNotification();
                        bildirimGonder(
                                "",
                                "",
                                "",
                                "",
                                "",
                                "",
                                0,0,
                                0, 0,5);

                        new LoadApplications().execute();

                        myDb.DeleteAllData_Traffic();


                        final Calendar now2 = Calendar.getInstance();
                        for(int i=0; i<(int) SharedPreferences().getLong("Fark_Tarih",0); i++){
                            now2.add(Calendar.DATE,i);
                            myDb.Add(0," MB",String.valueOf(time.day[now2.get(Calendar.DATE)-1]+"."+time.month[now2.get(Calendar.MONTH)]+"."+now2.get(Calendar.YEAR)));
                            now2.add(Calendar.DATE,i*-1);
                        }

                        final Calendar now1 = Calendar.getInstance();
                        now1.add(Calendar.DATE, (int) SharedPreferences().getLong("Fark_Tarih",0));
                        Editor().putInt("AY2", now1.get(Calendar.MONTH)).commit();
                        Editor().putInt("GUN2", now1.get(Calendar.DATE)).commit();
                        Editor().putInt("YIL2", now1.get(Calendar.YEAR)).commit();

                        Editor().putString("MOBIL_DOWN", "0").commit();
                        Editor().putBoolean("Viewed_DailyLimit",false).commit();
                        Editor().putBoolean("Viewed_DataLimit",false).commit();

                        CountDay= Control_Day(now,SharedPreferences().getInt("YIL2",0),SharedPreferences().getInt("AY2",0),SharedPreferences().getInt("GUN2",0));
                        CountDay2=CountDay;
                    }
                }else if(Setting_Durdur) {

                    if(CountDay2<=0){
                        NotificationManager NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationManager.cancelAll();

                        try {
                            Broadcast broadcast = new Broadcast();
                            broadcast.Cancel();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            OneFragment oneFragment = new OneFragment();
                            oneFragment.StopOneFragment();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            //ServiceApps_Test serviceApps_test = new ServiceApps_Test(getApplicationContext());
                            ServiceApps_Test.StopServieApps_Test();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        try {
                            ThreeFragment threeFragment=new ThreeFragment();
                            threeFragment.StopThreeFragment();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        Editor().putBoolean("AppStop",true).commit();

                        if(SharedPreferences().getBoolean("Viewed_StopApp",true)){
                            Editor().putBoolean("Viewed_StopApp",false).commit();
                            bildirimGonder(
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    0,0,
                                    0,0,7);
                        }

                        Intent i = new Intent(getApplicationContext(),Service_Data.class);
                        stopService(i);
                        System.exit(-1);

                    }


                }

                Editor().putString("DAY", String.valueOf(CountDay)).commit();


                long Day_Difference=Control_Day(now,SharedPreferences().getInt("YEAR2",0),SharedPreferences().getInt("MONTH2",0),SharedPreferences().getInt("DAY2",0));
                if(Day_Difference!=0 || First_For_Data){

                    Editor().putBoolean("First_For_Data",false).commit();
                    SetDatas();
                    SetSetting(getApplication());

                    if(CountDay<=0){
                        if((MyData-UseDataPlus)>0)
                            DailyData= (MyData-UseDataPlus);
                    }else {
                        if((MyData-UseDataPlus)/CountDay>0)
                            DailyData= (MyData-UseDataPlus)/CountDay;
                    }


                    Editor().putString("DAILY_DATA_MB",String.valueOf(DailyData)).commit();
                    Editor().putBoolean("Viewed_DailyLimit",false).commit();
                    Editor().putString("MOBIL_DOWN","0").commit();

                    RemainingData = getData.getTotal(context);

                    Cursor res = myDb.getAllData();
                    if(res.getCount()!=0){
                        while (res.moveToNext()){
                            if(res.getString(2).equals(String.valueOf(time.day[now.get(Calendar.DATE)-1]+"."+time.month[now.get(Calendar.MONTH)]+"."+now.get(Calendar.YEAR)))) {
                                String Type=res.getString(1);
                                double OldData=res.getDouble(0);
                                if(OldData!=0){
                                    if(Type.trim().equals("B")){
                                        OldData=(OldData/1024)/1024;
                                    }else if(Type.trim().equals("KB")){
                                        OldData=(OldData/1024);
                                    }else if(Type.trim().equals("GB")){
                                        OldData=(OldData*1024);
                                    }
                                    Editor().putString("OLD_DAILY_DATA",String.valueOf(OldData)).commit();
                                }else {
                                    Editor().putString("OLD_DAILY_DATA","0").commit();
                                }
                            }
                        }
                    }
                }

                Editor().putInt("DAY2", now.get(Calendar.DATE)).commit();
                Editor().putInt("MONTH2", now.get(Calendar.MONTH)).commit();
                Editor().putInt("YEAR2", now.get(Calendar.YEAR)).commit();

                while (DailyData<=0) {
                    CountDay= Control_Day(now,SharedPreferences().getInt("YIL2",0),SharedPreferences().getInt("AY2",0),SharedPreferences().getInt("GUN2",0));

                    if(CountDay<=0){
                        if((MyData-UseDataPlus)>0)
                            DailyData= (MyData-UseDataPlus);
                    }else {
                        if((MyData-UseDataPlus)/CountDay>0)
                            DailyData= (MyData-UseDataPlus)/CountDay;
                    }
                }

                GetMobilData=get_trafficStats.Get_Mobile(context);

                if(GetMobilData!=0){



                    if(SharedPreferences().getString("DELTA","").trim().equals("")){
                        Editor().putString("DELTA", String.valueOf(GetMobilData)).commit();
                    }


                    Delta=GetMobilData-Double.parseDouble(SharedPreferences().getString("DELTA","0"));
                    Editor().putString("DELTA", String.valueOf(GetMobilData)).commit();

                    Daily_UseData = Double.parseDouble(SharedPreferences().getString("MOBIL_DOWN","0"));

                    if(Delta>0){
                        Daily_UseData = Daily_UseData+Delta;
                        Daily_UseData=Daily_UseData-ExitQuotaApp();
                        Editor().putString("MOBIL_DOWN", String.valueOf(Daily_UseData)).commit();
                    }




                    OldData= Double.parseDouble(SharedPreferences().getString("OLD_DAILY_DATA","0"));
                    Daily_UseData=Daily_UseData+OldData;
                    Speed = Speed();
                    UseDataPlus = (RemainingData + Daily_UseData);

                    double DoubleMyData=MyData*1024;
                    double DoubleUseData = UseDataPlus*1024;
                    double DoubleDailyUseData = Daily_UseData*1024;
                    double DoubleDailyData = DailyData*1024;



                    if(!SharedPreferences().getBoolean("Viewed_DailyLimit",false)){
                        if(!SharedPreferences().getBoolean("Viewed_DataLimit",false))
                            if(DoubleDailyData<=DoubleDailyUseData){

                                //Günlük Veri Aşıldı
                                bildirimGonder(
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        0,0,
                                        0, 0,2);


                            }
                    }

                    if(!SharedPreferences().getBoolean("Viewed_DataLimit",false)){
                        if(DoubleMyData<=DoubleUseData){

                            CloseNotification();
                            //Veri Aşıldı
                            bildirimGonder(
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    0,0,
                                    0, 0,3);

                        }
                    }


                    init2=System.currentTimeMillis();
                    time_ms=init2-init;
                    init=System.currentTimeMillis();

                    if(time_ms<=0){
                        time_ms=1;
                    }

                    Remaining=MyData-UseDataPlus;
                    RemainingStr="    "+numberformat.format(Unit_Converter(Remaining))+Data_Str+" Kaldı";
                    if(Remaining<0){
                        RemainingStr="    "+numberformat.format(Unit_Converter(Remaining*-1))+Data_Str+" Aşıldı";
                    }

                    //Veri İzleme
                    bildirimGonder(
                            RemainingStr,
                            numberformat.format(Unit_Converter(MyData))+Data_Str,
                            numberformat.format(Unit_Converter(Daily_UseData))+Data_Str,
                            numberformat.format(Unit_Converter(DailyData))+Data_Str,
                            numberformat.format(Unit_Converter(1000*Speed/time_ms))+Data_Str_Second,
                            String.valueOf(CountDay),
                            DoubleMyData,DoubleUseData,
                            DoubleDailyData, DoubleDailyUseData,1);


                    Saves();

                    myDb.UpdateData(Unit_Converter(Daily_UseData), Data_Str,
                            String.valueOf(time.day[now.get(Calendar.DATE)-1]+"."+time.month[now.get(Calendar.MONTH)]+"."+now.get(Calendar.YEAR)));

                    Control_Notification=true;

                }
                else{

                    GetWifiData=get_trafficStats.Get_Wifi(context);

                    if(Control_Notification){
                        Control_Notification=false;
                        Editor().putString("SPEED",numberformat.format(0.0)+" B/s").commit();
                        Editor().putString("DELTA_WIFI", String.valueOf(GetWifiData)).commit();
                    }

                    ConnectivityManager connectivityManager2 =
                            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo wifiInfo2 =
                            connectivityManager2.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    boolean wifiConnected2 = wifiInfo2.getState() == NetworkInfo.State.CONNECTED;

                    if(wifiConnected2){


                        if(SharedPreferences().getString("DELTA_WIFI","").trim().equals("")){
                            Editor().putString("DELTA_WIFI", String.valueOf(GetWifiData)).commit();
                        }

                        Delta_Wifi=GetWifiData-Double.parseDouble(SharedPreferences().getString("DELTA_WIFI","0"));
                        UseData_wifi = Double.parseDouble(SharedPreferences().getString("MOBIL_DOWN_WIFI","0"));


                        if(Delta_Wifi>0){
                            UseData_wifi = UseData_wifi +Delta_Wifi;
                            Editor().putString("MOBIL_DOWN_WIFI", String.valueOf(UseData_wifi)).commit();
                        }
                        Editor().putString("DELTA_WIFI", String.valueOf(GetWifiData)).commit();

                        Speed = Speed_Wifi();



                        init2=System.currentTimeMillis();
                        time_ms=init2-init;
                        init=System.currentTimeMillis();

                        if(time_ms<=0){
                            time_ms=1;
                        }

                        wifi_max=SharedPreferences().getInt("wifi_max",0);
                        if(UseData_wifi>wifi_max*1024/5-0.5){
                            wifi_max=1;
                            wifi_max=wifi_max*2;
                            Editor().putInt("wifi_max",wifi_max).commit();
                        }else {
                            Editor().putInt("wifi_max",1).commit();
                        }

                        String WifiNameNotfy;
                        if(getWifiName(context)!=null){
                            WifiMac =getWifiMacAdress();
                            WifiName=getWifiName(context);
                            sayac_test++;


                            WifiData= Double.parseDouble(SharedPreferences().getString("WifiData","0"));
                            if(!WifiMac.equals(SharedPreferences().getString("OldWifiMac","0"))){

                                WifiData=0;
                                Editor().putString("WifiData","0").commit();

                                Cursor res = myDb.getAllData_Wifi();
                                if(res.getCount()!=0){
                                    while (res.moveToNext()){
                                        if(res.getString(2).equals(WifiMac)){
                                            WifiData=res.getDouble(1);
                                            Toast.makeText(context, numberformat.format(Unit_Converter(res.getDouble(1)))+Data_Str, Toast.LENGTH_SHORT).show();
                                            break;
                                        }

                                    }
                                }

                            }
                            WifiData=WifiData+Delta_Wifi;

                            WifiName=WifiName.substring(1,WifiName.length()-1);

                            if(myDb.UpdateData_Wifi(WifiData,WifiName,getWifiMacAdress())==0){
                                myDb.Add_Wifi(WifiName,WifiData,WifiMac);
                            }

                            Editor().putString("OldWifiMac",WifiMac).commit();
                            Editor().putString("WifiData", String.valueOf(WifiData)).commit();

                            WifiNameNotfy =  WifiName+"  "+RSSI(context)+"   "+numberformat.format(Unit_Converter(WifiData))+Data_Str+" Kullanıldı";

                        }else {
                            WifiNameNotfy="Şuan Bir Ağa Bağlı Değilsiniz";
                        }



                        //WİFİ İzleme
                        bildirimGonder(
                                numberformat.format(Unit_Converter(UseData_wifi))+Data_Str,
                                numberformat.format(Unit_Converter((double) (wifi_max*1024/5)))+Data_Str,
                                WifiNameNotfy,
                                "",
                                numberformat.format(Unit_Converter(1000*Speed/time_ms))+Data_Str_Second,
                                "",
                                wifi_max*1024/5,UseData_wifi,
                                0, 0,4);


                        Saves_Wifi();
                    }
                }


                if(RestartServiceApp){
                    new ServiceApps_Test(getApplication());
                    RestartServiceApp=false;
                }

                SystemClock.sleep(1000);

            }catch (SecurityException ex){

            }

            inCode=true;
            return null;
        }{

        }

        @Override
        protected void onPostExecute(Void result) {
            if(!SharedPreferences().getBoolean("AppStop",false))
                if(!Exit){
                    new LoadCode().execute();
                }else {
                    Exit=false;
                }


            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {


            super.onPreExecute();
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

    public static String RSSI(Context context){
        int Rssi;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        Rssi=wInfo.getRssi();
        Rssi=Rssi*-1;
        Rssi=100-Rssi;
        if(Rssi<=0){
            Rssi=0;
        }
        return Rssi+"%";
    }


    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    return wifiInfo.getSSID();
                }
            }
        }
        return null;
    }
    public String getWifiMacAdress()  {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getBSSID();
        return macAddress;
    }

    static boolean RestartServiceApp =false;
    public void RestartServiceApp(){
        RestartServiceApp =true;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        RemainingData = getData.getTotal(context);

        numberformat.setMinimumFractionDigits(2);
        numberformat.setMaximumFractionDigits(2);

        get_trafficStats.Get_Mobile(context);

        new getWifiData(getApplicationContext());

        SetDatas();
        SetSetting(getApplicationContext());


        if(Setting_SecilenUygulamaGoster || Setting_UygulamaGoster){
            new ServiceApps_Test(getApplication());
        }

    }

    public void CloseNotification(){
        if(Main_notificationManager!=null){
            Main_notificationManager.cancel(1);
            Main_notificationManager.cancel(2);
            Main_notificationManager.cancel(3);
            Main_notificationManager.cancel(4);
            Main_notificationManager.cancel(5);
        }

    }

    public static NotificationManager Main_notificationManager=null;



    public void bildirimGonder(String Use_Data, String Data, String UseDaily, String DailyData,
                               String Speed, String Day,
                               double DoubleMyData, double DoubleUseData, double DoubleDailyData,double DoubleDailyUseData, int View){


        Main_notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);




        if(View==1){

            if(Setting_SürekliKullanim){
                Notification notification = new Notification(R.drawable.image_2, null,
                        System.currentTimeMillis());

                RemoteViews notificationView = null;

                notificationView = new RemoteViews(getPackageName(),
                        R.layout.notification_layout);

                notificationView.setTextViewText(R.id.textView_UseData,Use_Data);
                notificationView.setTextViewText(R.id.textView_Data,Data+"  ");
                notificationView.setTextViewText(R.id.textView_UseDaily,"   Bugün "+UseDaily+" Kullanıldı");
                notificationView.setTextViewText(R.id.textView_DataDaily,DailyData+"  ");
                notificationView.setTextViewText(R.id.textView_Speed,Speed+"   ");
                if(CountDay2<=0){
                    notificationView.setTextViewText(R.id.textView_Date,"Süre Doldu   ");
                }else {
                    notificationView.setTextViewText(R.id.textView_Date,Day+" Gün Kaldı   ");
                }


                notificationView.setProgressBar(R.id.progressBar_Use, (int) DoubleMyData, (int) DoubleUseData,false);
                notificationView.setProgressBar(R.id.progressBar_Daily, (int) DoubleDailyData, (int) DoubleDailyUseData,false);


                if(!SharedPreferences().getBoolean("Tabs_StartOrStop",true)){
                    Intent notificationIntent = new Intent(context, Tabs.class);
                    PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0,
                            notificationIntent, 0);

                    notification.contentIntent = pendingNotificationIntent;
                }


                notification.contentView = notificationView;
                notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
                notification.priority = Notification.PRIORITY_MAX;


                Main_notificationManager.notify(1,notification);
            }

        }else if(View==2){

            if(Setting_Günlükveri){
                Notification notification2 = new Notification(R.drawable.icon_notifications, null,
                        System.currentTimeMillis());

                RemoteViews notificationView2 = null;

                notificationView2 = new RemoteViews(getPackageName(),
                        R.layout.notification_layout_dailydata);

                notificationView2.setImageViewResource(R.id.imageView,R.drawable.image_4);

                notification2.contentView = notificationView2;
                notification2.priority = Notification.PRIORITY_LOW;
                notification2.flags = Notification.FLAG_ONGOING_EVENT;
                notification2.defaults |= Notification.DEFAULT_SOUND;
                notification2.defaults |= Notification.DEFAULT_VIBRATE;
                notification2.defaults |= Notification.DEFAULT_LIGHTS;

                Main_notificationManager.notify(2,notification2);
            }

            Editor().putBoolean("Viewed_DailyLimit",true).commit();

        }else if(View==3){

            if(Setting_Veri){
                Notification notification3 = new Notification(R.drawable.icon_notifications, null,
                        System.currentTimeMillis());

                RemoteViews notificationView3 = null;

                notificationView3 = new RemoteViews(getPackageName(),
                        R.layout.notification_layout_data);

                notificationView3.setImageViewResource(R.id.imageView,R.drawable.image_2);

                notification3.contentView = notificationView3;
                notification3.priority = Notification.PRIORITY_LOW;
                notification3.defaults |= Notification.DEFAULT_SOUND;
                notification3.defaults |= Notification.DEFAULT_VIBRATE;
                notification3.defaults |= Notification.DEFAULT_LIGHTS;

                Main_notificationManager.notify(3,notification3);

            }
            Editor().putBoolean("Viewed_DataLimit",true).commit();

        }else if(View==4){
            Notification notification4 = new Notification(R.drawable.image_2, null,
                    System.currentTimeMillis());

            RemoteViews notificationView4 = null;

            notificationView4 = new RemoteViews(getPackageName(),
                    R.layout.notification_layout_wifi);

            notificationView4.setTextViewText(R.id.textView_UseData_Wifi,"    "+Use_Data+" Kullanıldı");
            notificationView4.setTextViewText(R.id.textView_Data_Wifi,Data+"  ");
            notificationView4.setTextViewText(R.id.textView_WifiNameNotfy,UseDaily);

            notificationView4.setTextViewText(R.id.textView_SpeedWifi,Speed);

            notificationView4.setProgressBar(R.id.progressBar, (int) DoubleMyData, (int) DoubleUseData,false);



            notification4.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
            notification4.contentView = notificationView4;
            notification4.priority = Notification.PRIORITY_MAX;

            Main_notificationManager.notify(1,notification4);
        }else if(View==5){
            if(Setting_Tekrarla){
                Notification notification5 = new Notification(R.drawable.icon_notifications, null,
                        System.currentTimeMillis());


                RemoteViews notificationView5 = null;

                notificationView5 = new RemoteViews(getPackageName(),
                        R.layout.notification_layout_reflesh);

                notificationView5.setImageViewResource(R.id.imageView,R.drawable.image_reflesh);

                notification5.contentView = notificationView5;
                notification5.priority = Notification.PRIORITY_LOW;
                notification5.defaults |= Notification.DEFAULT_SOUND;
                notification5.defaults |= Notification.DEFAULT_VIBRATE;
                notification5.defaults |= Notification.DEFAULT_LIGHTS;

                Main_notificationManager.notify(5,notification5);
            }


        } else if(View==6){

        Notification notification6 = new Notification(R.drawable.icon_notifications, null,
                System.currentTimeMillis());


        RemoteViews notificationView6 = null;

        notificationView6 = new RemoteViews(getPackageName(),
                R.layout.notification_layout_finishdate);

        notificationView6.setImageViewResource(R.id.imageView,R.drawable.image_1);

        notification6.contentView = notificationView6;
        notification6.priority = Notification.PRIORITY_LOW;
        notification6.defaults |= Notification.DEFAULT_SOUND;
        notification6.defaults |= Notification.DEFAULT_VIBRATE;
        notification6.defaults |= Notification.DEFAULT_LIGHTS;

            Main_notificationManager.notify(6,notification6);

    }else if(View==7){

            Notification notification7 = new Notification(R.drawable.icon_notifications, null,
                    System.currentTimeMillis());


            RemoteViews notificationView7 = null;

            notificationView7 = new RemoteViews(getPackageName(),
                    R.layout.notification_layout_stopedapp);

            notificationView7.setImageViewResource(R.id.imageView,R.drawable.image_1);

            notification7.contentView = notificationView7;
            notification7.priority = Notification.PRIORITY_LOW;
            notification7.defaults |= Notification.DEFAULT_SOUND;
            notification7.defaults |= Notification.DEFAULT_VIBRATE;
            notification7.defaults |= Notification.DEFAULT_LIGHTS;

            Main_notificationManager.notify(7,notification7);

        }

    }


    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    double Data;

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


    private class LoadApplications extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            DoZeroApp();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }
    }

    public void DoZeroApp(){

        packageManager = getApplicationContext().getPackageManager();

        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

        for (int i=0; i<applist.size(); i++){
            ApplicationInfo data = applist.get(i);

            Data=getUsage(data.uid)/1048576;

            Editor().putString("Delta"+String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();
            Editor().putString(String.valueOf(data.loadLabel(packageManager)),"0").commit();

            Editor().putString("Delta2"+String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();
            Editor().putString("2"+String.valueOf(data.loadLabel(packageManager)),"0").commit();
        }
    }

    getSubscriberId id = new getSubscriberId();

    public static void CloseOnNotification(int id){
        Main_notificationManager.cancel(id);
    }

    private double getUsage(int packageUid) {
        NetworkStats networkStatsByApp;
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);


        long currentUsage = 0L;
        try {
            networkStatsByApp = networkStatsManager.querySummary(id.getType(),id.getSubscriberId(context,ConnectivityManager.TYPE_MOBILE), 0, System.currentTimeMillis());

            do {

                NetworkStats.Bucket bucket = new NetworkStats.Bucket();

                networkStatsByApp.getNextBucket(bucket);
                if (bucket.getUid() == packageUid) {
                    //rajeesh : in some devices this is immediately looping twice and the second iteration is returning correct value. So result returning is moved to the end.
                    currentUsage =currentUsage+(bucket.getRxBytes() + bucket.getTxBytes());
                }
            } while (networkStatsByApp.hasNextBucket());

            networkStatsByApp.close();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return currentUsage;
    }


}
