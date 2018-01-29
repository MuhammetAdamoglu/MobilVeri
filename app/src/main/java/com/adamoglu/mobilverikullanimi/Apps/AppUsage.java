package com.adamoglu.mobilverikullanimi.Apps;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.Adapters.AppAdapter;
import com.adamoglu.mobilverikullanimi.R;
import com.adamoglu.mobilverikullanimi.database;
import com.adamoglu.mobilverikullanimi.getSubscriberId;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppUsage extends AppCompatActivity {

    String Name;

    boolean Control_FirstLoading=true;
    boolean Control_DoInBackground=true;


    double Delta;

    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private AppAdapter listadapter = null;

    ListView lv;
    ProgressBar pb;
    TextView tv;
    ImageView iv_reflesh;
    NumberFormat numberformat = NumberFormat.getInstance();
    ConnectivityManager manager;
    EditText search;
    ConstraintLayout cl_search;
    ConstraintLayout cl_Shadow;
    ImageView iv;

    static Handler handler;

    ArrayList<String> dizi_name = new ArrayList();
    ArrayList<Double> dizi_data = new ArrayList();
    ArrayList<Drawable> dizi_icon = new ArrayList();
    ArrayList<Integer> dizi_color = new ArrayList();
    ArrayList<String> dizi_speed = new ArrayList();

    ArrayList<String> dizi_name2 = new ArrayList();
    ArrayList<Double> dizi_data2 = new ArrayList();
    ArrayList<Drawable> dizi_icon2 = new ArrayList();
    ArrayList<Integer> dizi_color2 = new ArrayList();
    ArrayList<String> dizi_speed2 = new ArrayList();

    ArrayList<String> dizi_name_search = new ArrayList();
    ArrayList<Double> dizi_data_search = new ArrayList();
    ArrayList<Drawable> dizi_icon_search = new ArrayList();
    ArrayList<Integer> dizi_color_search = new ArrayList();
    ArrayList<String> dizi_speed_search = new ArrayList();


    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = getSharedPreferences("",0);
        return prefSettings;
    }

    public static ArrayList<Integer> QuotaApps = new ArrayList<>();
    public void getQuotaApps(){
        database myDb = new database(getApplicationContext());

        Cursor res = myDb.getAllData_Quota();

        QuotaApps.clear();

        if(res.getCount()!=0){
            while (res.moveToNext())
                QuotaApps.add(res.getInt(0));
        }

    }

    public void StopAppUsage(){
        handler.removeMessages(0);
    }

    public void BigToSmall(){
        //Uygulamaları çok veri kullanımından az veri kullanımına sıralar
        double mostBig;
        String Name;
        Drawable Icon;
        Integer Color;
        String Speed;

        try {
            for(int i=0; i<dizi_data.size();i++){
                for (int j=i; j<dizi_data.size();j++ ){
                    if(dizi_data.get(i)<dizi_data.get(j)){

                        mostBig=dizi_data.get(j);
                        dizi_data.remove(j);
                        dizi_data.add(i,mostBig);

                        Name=dizi_name.get(j);
                        dizi_name.remove(j);
                        dizi_name.add(i,Name);

                        Icon=dizi_icon.get(j);
                        dizi_icon.remove(j);
                        dizi_icon.add(i,Icon);

                        Color=dizi_color.get(j);
                        dizi_color.remove(j);
                        dizi_color.add(i,Color);

                        Speed=dizi_speed.get(j);
                        dizi_speed.remove(j);
                        dizi_speed.add(i,Speed);
                    }
                }
            }


        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "BigToSmall()' da hata", Toast.LENGTH_SHORT).show();
        }
    }

    public void BigToSmall_First(){
        //Uygulamaları çok veri kullanımından az veri kullanımına sıralar
        double mostBig;
        String Name;
        Drawable Icon;
        Integer Color;
        String Speed;

        try {
            for(int i=0; i<dizi_data2.size();i++){
                for (int j=i; j<dizi_data2.size();j++ ){
                    if(dizi_data2.get(i)<dizi_data2.get(j)){

                        mostBig=dizi_data2.get(j);
                        dizi_data2.remove(j);
                        dizi_data2.add(i,mostBig);

                        Name=dizi_name2.get(j);
                        dizi_name2.remove(j);
                        dizi_name2.add(i,Name);

                        Icon=dizi_icon2.get(j);
                        dizi_icon2.remove(j);
                        dizi_icon2.add(i,Icon);

                        Color=dizi_color2.get(j);
                        dizi_color2.remove(j);
                        dizi_color2.add(i,Color);

                        Speed=dizi_speed2.get(j);
                        dizi_speed2.remove(j);
                        dizi_speed2.add(i,Speed);
                    }
                }
            }


        }catch (Exception ex){

        }
    }

    boolean Cursor=false;
    public void Cursor(){
        //Arama çubuğu imlecini belirler
        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                search.setCursorVisible(false);
                CloseKeyBoard();
                Cursor=false;
                return false;
            }
        });

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                search.setCursorVisible(true);
                Cursor=true;
                iv.setColorFilter(Color.parseColor("#EE000000"));
                iv_reflesh.setColorFilter(Color.parseColor("#EE000000"));
                search.setHintTextColor(Color.parseColor("#EE000000"));
                search.setTextColor(Color.parseColor("#EE000000"));
                cl_search.setBackgroundColor(Color.parseColor("#CCFFFFFF"));
                return false;
            }
        });
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
            Toast.makeText(getApplicationContext(), "calculateAverageColor(Bitmap,int)'de Hata", Toast.LENGTH_SHORT).show();
            return Color.rgb(96,123,139);
        }

        return Color.rgb(R / n, G / n, B / n);
    }

    public void Search(){
        //Uygulamalar arasında arama yapar



        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(search.getText().toString().trim().equals("")){
                        search.setCursorVisible(false);

                        listadapter = new AppAdapter(getApplicationContext().getApplicationContext(), dizi_name,dizi_data,dizi_icon,dizi_color,dizi_speed);
                        lv.setAdapter(listadapter);

                    }else {


                        dizi_name_search.clear();
                        dizi_data_search.clear();
                        dizi_icon_search.clear();
                        dizi_color_search.clear();
                        dizi_speed_search.clear();

                        for (int i=0; i<dizi_name.size(); i++){

                            if( dizi_name.get(i).toLowerCase().contains(search.getText().toString().toLowerCase())){
                                dizi_name_search.add(dizi_name.get(i));
                                dizi_data_search.add(dizi_data.get(i));
                                dizi_icon_search.add(dizi_icon.get(i));
                                dizi_color_search.add(dizi_color.get(i));
                                dizi_speed_search.add(dizi_speed.get(i));
                            }
                        }

                        listadapter = new AppAdapter(getApplicationContext(), dizi_name_search,dizi_data_search,dizi_icon_search,dizi_color_search,dizi_speed_search);
                        lv.setAdapter(listadapter);


                    }
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Search()'da Hata", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
    public void CloseKeyBoard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(0);

    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadApplications().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        handler.postDelayed(runnable,1000);
    }

    Runnable runnable;
    boolean Exit=false;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeMessages(0);
        Exit=true;
    }

    public String ControlStringLeng(String str, int lengt){

        if(str.length()>=lengt){
            str=str.substring(0,lengt);
            str=str+"..";
        }

        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_usage);


        lv= (ListView) findViewById(R.id.ListView);
        pb= (ProgressBar) findViewById(R.id.progressBar);
        tv= (TextView) findViewById(R.id.textView_message);
        iv= (ImageView) findViewById(R.id.imageView);
        search = (EditText) findViewById(R.id.editText);
        iv_reflesh= (ImageView) findViewById(R.id.button_reflesh);
        cl_search= (ConstraintLayout) findViewById(R.id.cl_search);
        cl_Shadow= (ConstraintLayout) findViewById(R.id.constraintLayout_Shadow);


        listadapter = new AppAdapter(getApplicationContext().getApplicationContext(), dizi_name,dizi_data,dizi_icon,dizi_color,dizi_speed);
        lv.setAdapter(listadapter);

        numberformat.setMinimumFractionDigits(1);
        numberformat.setMaximumFractionDigits(1);

        packageManager = getApplicationContext().getPackageManager();

        getQuotaApps();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Control_DoInBackground)
                    new LoadApplications().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                if(Control_FirstLoading){
                    listadapter = new AppAdapter(getApplicationContext().getApplicationContext(), dizi_name2,dizi_data2,dizi_icon2,dizi_color2,dizi_speed2);
                    lv.setAdapter(listadapter);
                    BigToSmall_First();
                }


                runnable=this;
                handler.postDelayed(runnable,1000);
            }
        },0);

        manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        lv.setVerticalFadingEdgeEnabled(false);

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(dizi_color.size()!=0){
                    if(!Cursor){
                        iv.setColorFilter(dizi_color.get(firstVisibleItem));
                        iv_reflesh.setColorFilter(dizi_color.get(firstVisibleItem));
                        search.setHintTextColor(dizi_color.get(firstVisibleItem));
                        search.setTextColor(dizi_color.get(0));
                        String hexColor = String.format("%06X", (0xFFFFFF & dizi_color.get(firstVisibleItem)));
                        cl_search.setBackgroundColor(Color.parseColor("#50"+hexColor));
                    }

                }
            }
        });


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
            Toast.makeText(getApplicationContext(), "Unit_Converter(Double)'de Hata", Toast.LENGTH_SHORT).show();
            return 0;
        }

        return Data;
    }



    long init;
    long init2;
    long time;
    double newData;
    double Data;
    boolean Control_firs=true;

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

             try {

                 if(Exit)
                     return null;

                    init2=System.currentTimeMillis();
                    time=init2-init;
                    init=System.currentTimeMillis();


                    if(time<=0){
                        time=1;
                    }



                    dizi_name2.clear();
                    dizi_data2.clear();
                    dizi_icon2.clear();
                    dizi_color2.clear();
                    dizi_speed2.clear();

                    if(Control_firs){
                        Control_firs=false;
                        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
                    }

                    for (int i = 0; i<applist.size(); i++){

                        if(Exit)
                            return null;

                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(String.valueOf(finalI)+" / "+String.valueOf(applist.size()));
                            }
                        });

                        ApplicationInfo data = applist.get(i);

                        Data=(getUsage(data.uid))/1048576;

                        if(Data==0)
                            continue;

                        Name = SharedPreferences().getString(String.valueOf(data.loadLabel(packageManager)),"");


                        if(Name.trim().equals("")){

                            Editor().putString("Delta"+String.valueOf(data.loadLabel(packageManager)),"0").commit();
                            Editor().putString("Delta2"+String.valueOf(data.loadLabel(packageManager)),"0").commit();
                            Editor().putString(String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();
                            Editor().putString("2"+String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();
                        }
                        else {



                            Delta=Data-Double.parseDouble(SharedPreferences().getString("Delta"+String.valueOf(data.loadLabel(packageManager)),"0"));

                            if(Delta>0){
                                Editor().putString(String.valueOf(data.loadLabel(packageManager)),
                                        String.valueOf(
                                                Delta+Double.parseDouble(SharedPreferences().getString(String.valueOf(data.loadLabel(packageManager)),"0"))
                                        )).commit();
                            }
                            Editor().putString("Delta"+String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();
                        }
                        newData=Double.parseDouble(SharedPreferences().getString(String.valueOf(data.loadLabel(packageManager)),"0"));

                        if(newData>0){

                            String Quota="";
                            for(int j=0; j<QuotaApps.size(); j++)
                                if(QuotaApps.get(j)==data.uid){
                                    Quota="\n(Kotadan Harcamayan Uygulama)";
                                }else {
                                    Quota="";
                                }


                            String QuotaDataShow="";
                            double QuotaData= Double.parseDouble(SharedPreferences().getString("QuotaApp"+data.uid,"0"));

                            if(QuotaData==0){
                                QuotaDataShow="";
                            }else {
                                QuotaDataShow=" / "+numberformat.format(Unit_Converter(QuotaData))+Data_Str+" Kota Dışı";
                            }

                            dizi_name2.add(ControlStringLeng(String.valueOf(data.loadLabel(packageManager)),13)+"  "+numberformat.format(Unit_Converter(newData))+Data_Str+QuotaDataShow+Quota);
                            dizi_data2.add(newData);

                            Drawable icon = data.loadIcon(packageManager);
                            dizi_icon2.add(icon);
                            dizi_color2.add(calculateAverageColor(((BitmapDrawable) icon).getBitmap(),5));

                            dizi_speed2.add(String.valueOf(numberformat.format(Unit_Converter(1000*Delta/time)))+Data_Str_Second);


                        }

                    }


                    dizi_name.clear();
                    dizi_data.clear();
                    dizi_icon.clear();
                    dizi_color.clear();
                    dizi_speed.clear();

                    for (int j=0; j<dizi_data2.size();j++){

                        dizi_name.add(dizi_name2.get(j));
                        dizi_data.add(dizi_data2.get(j));
                        dizi_icon.add(dizi_icon2.get(j));
                        dizi_color.add(dizi_color2.get(j));
                        dizi_speed.add(dizi_speed2.get(j));

                    }

                    BigToSmall();


                }catch (SecurityException ex){
                    Toast.makeText(getApplicationContext(), "Lütfen Uygulamaya İzin Verin", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);
                }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            if(Control_FirstLoading){
                Control_FirstLoading=false;

                listadapter = new AppAdapter(getApplicationContext().getApplicationContext(), dizi_name,dizi_data,dizi_icon,dizi_color,dizi_speed);
                lv.setAdapter(listadapter);

                Animation animate1 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                animate1.setDuration(200);
                Animation animate2 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                animate1.setDuration(200);

                search.startAnimation(animate1);
                iv.startAnimation(animate1);
                iv_reflesh.startAnimation(animate1);
                lv.startAnimation(animate1);
                pb.startAnimation(animate2);
                tv.startAnimation(animate2);
                cl_Shadow.startAnimation(animate2);


                pb.setVisibility(View.GONE);
                tv.setVisibility(View.GONE);
                cl_Shadow.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                iv.setVisibility(View.VISIBLE);
                iv_reflesh.setVisibility(View.VISIBLE);

                lv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });

                Cursor();
                Search();
            }


            listadapter.notifyDataSetChanged();



            if(dizi_color.size()!=0){

                if(!Cursor){
                    iv.setColorFilter(dizi_color.get(0));
                    iv_reflesh.setColorFilter(dizi_color.get(0));
                    search.setHintTextColor(dizi_color.get(0));
                    search.setTextColor(dizi_color.get(0));
                    String hexColor = String.format("%06X", (0xFFFFFF & dizi_color.get(0)));
                    cl_search.setBackgroundColor(Color.parseColor("#50"+hexColor));
                }

            }

            Control_DoInBackground=true;

            Editor().putBoolean("First_Entr_AppUsage",false).commit();

            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {


            if(Control_FirstLoading){

                Animation animate1 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                animate1.setDuration(200);
                Animation animate2 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                animate1.setDuration(200);

                search.startAnimation(animate2);
                iv.startAnimation(animate2);
                iv_reflesh.startAnimation(animate2);
                lv.startAnimation(animate2);
                pb.startAnimation(animate1);
                tv.startAnimation(animate1);

                pb.setVisibility(View.VISIBLE);
                tv.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                iv.setVisibility(View.GONE);
                iv_reflesh.setVisibility(View.GONE);

                lv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            }

            Control_DoInBackground=false;
            super.onPreExecute();
        }
    }

    HashMap<Integer,Double> Control_App = new HashMap<>();

    getSubscriberId id = new getSubscriberId();

    private double getUsage(int packageUid) {
        NetworkStats networkStatsByApp = null;
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);



        long currentUsage = 0L;
        try {
            networkStatsByApp = networkStatsManager.querySummary(id.getType(),id.getSubscriberId(getApplicationContext(),ConnectivityManager.TYPE_MOBILE), 0, System.currentTimeMillis());

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

    NotificationManager Main_notificationManager1=null;
    public void bildirimGonderTest(String strng,int id){


        String ns = Context.NOTIFICATION_SERVICE;
        Main_notificationManager1 = (NotificationManager) getSystemService(ns);

        Notification notification = new Notification(android.R.drawable.editbox_dropdown_light_frame, null,
                System.currentTimeMillis());


        RemoteViews notificationView = null;


        /*notificationView = new RemoteViews(getPackageName(),
                R.layout.notification_layout_finishdate);*/


        notificationView.setTextViewText(R.id.textView_message,strng);




        notification.contentView = notificationView;
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        notification.priority = Notification.PRIORITY_MAX;


        Main_notificationManager1.notify(id, notification);

    }
}
