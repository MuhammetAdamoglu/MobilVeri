package com.adamoglu.mobilverikullanimi;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adamoglu.mobilverikullanimi.Services.Service_Data;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView textView_DataUsage;
    TextView textView_Speed;
    TextView textView_Daily;
    SlidingUpPanelLayout UpPanel;
    ArcProgress arcProgress_Use;
    ArcProgress arcProgress_Daily;
    LinearLayout DragView;

    NumberFormat numberformat = NumberFormat.getInstance();

    boolean Startup_ArcUse=true;
    boolean Startup_ArcDaily=true;
    boolean Control_Anime_Open=true;
    boolean Control_Anime_Close=true;
    boolean Control_Notification;

    String styledText;
    String styledText2;

    public void Sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Anime(ArcProgress arcProgress, int Count){

        ObjectAnimator animation = ObjectAnimator.ofInt(arcProgress, "progress", 0, Count);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

    }

    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = getSharedPreferences("", Context.MODE_PRIVATE);
        return prefSettings;
    }

    public void ListViewScrool() {

        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }


    double UseData;
    String UseData_Str;
    double MyData;
    String MyData_Str;


    public void ArcProgress_Use() {

        UseData = Double.parseDouble(SharedPreferences().getString("USE_DATA_MB", "0"));
        MyData = Double.parseDouble(SharedPreferences().getString("MYDATA", "0"));
        int Percent = (int) ((100*UseData)/MyData);

        if(Percent>=90){
            if(Startup_ArcUse){
                Startup_ArcUse=false;
                Anime(arcProgress_Use,Percent);
            }

            arcProgress_Use.setProgress(Percent);
        }else if(Percent>=100){
            if(Startup_ArcUse){
                Startup_ArcUse=false;
                Anime(arcProgress_Use,100);
            }
            arcProgress_Use.setProgress(100);
        }else {
            if(Startup_ArcUse){
                Startup_ArcUse=false;
                Anime(arcProgress_Use,Percent);
            }

            arcProgress_Use.setProgress(Percent);
        }




    }

    double DailyData;
    String DailyData_Str;
    double DailyUseData;
    String DailyUseData_Str;

    public void ArcProgress_Daily(){

        DailyData = Double.parseDouble(SharedPreferences().getString("DAILY_DATA_MB", "0"));
        DailyUseData = Double.parseDouble(SharedPreferences().getString("DAILY_USEDATA_MB","0"));

        if(DailyUseData!=0 && DailyData!=0){
            if(Startup_ArcDaily){
                Startup_ArcDaily=false;
                Anime(arcProgress_Daily, (int) ((100*DailyUseData)/DailyData));
            }

            arcProgress_Daily.setProgress((int) ((100*DailyUseData)/DailyData));

        }

    }

    public void MBorGB(){

        if(UseData <0.98){
            UseData = UseData *1024;
            UseData_Str ="KB";
            if(UseData <1){
                UseData = UseData *1024;
                UseData_Str ="B";
            }
        }
        else if(UseData >999){
            UseData = UseData /1024;
            UseData_Str ="GB";
        }
        else{
            UseData_Str = "MB";
        }

        if(MyData <0.98){
            MyData = MyData *1024;
            MyData_Str ="KB";
            if(MyData <1){
                MyData = MyData *1024;
                MyData_Str ="B";
            }
        }
        else if(MyData >999){
            MyData = MyData /1024;
            MyData_Str ="GB";
        }
        else{
            MyData_Str = "MB";
        }

        if(DailyData <0.98){
            DailyData = DailyData *1024;
            DailyData_Str ="KB";
            if(DailyData <1){
                DailyData = DailyData *1024;
                DailyData_Str ="B";
            }
        }
        else if(DailyData >999){
            DailyData = DailyData /1024;
            DailyData_Str ="GB";
        }
        else{
            DailyData_Str = "MB";
        }



        if(DailyUseData <0.98){
            DailyUseData = DailyUseData *1024;
            DailyUseData_Str ="KB";
            if(DailyUseData <1){
                DailyUseData = DailyUseData *1024;
                DailyUseData_Str ="B";
            }
        }
        else if(DailyUseData >999){
            DailyUseData = DailyUseData /1024;
            DailyUseData_Str ="GB";
        }
        else{
            DailyUseData_Str = "MB";
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.ListView);
        textView_DataUsage = (TextView) findViewById(R.id.textView_DataUse);
        textView_Speed = (TextView) findViewById(R.id.textView_Speed);
        textView_Daily = (TextView) findViewById(R.id.textView_Daily);
        UpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        arcProgress_Daily = (ArcProgress) findViewById(R.id.arc_progress_UseDaily);
        arcProgress_Use = (ArcProgress) findViewById(R.id.arc_progress_UseData);
        DragView = (LinearLayout) findViewById(R.id.dragView);

        ListViewScrool();

        numberformat.setMinimumFractionDigits(1);
        numberformat.setMaximumFractionDigits(1);

        if (SharedPreferences().getInt("COUNT_DAY", -1) == -1) {
            Intent i = new Intent(getApplicationContext(), Add_Date.class);
            startActivity(i);
        } else {
            Intent intent = new Intent(getApplicationContext(), Service_Data.class);
            startService(intent);
        }

        new getData(getApplicationContext(), listView);


        UpPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelCollapsed(View panel) {
                if(Control_Notification){
                    Control_Anime_Open=true;
                    Control_Anime_Close=true;
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
                new getData(getApplicationContext(), listView);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }

        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ArcProgress_Use();
                ArcProgress_Daily();

                MBorGB();

                /////////DATAUSE////////
                 styledText=numberformat.format(MyData)+MyData_Str+" / "+numberformat.format(UseData)+UseData_Str;
                 styledText2 ="\nKULLANILAN";

                textView_DataUsage.setText(TextFont());
                ///////////////////////

                /////////SPEED////////
                 styledText = SharedPreferences().getString("SPEED","0.0B/s") ;
                 styledText2 = "\nİNDİRME HIZI";

                textView_Speed.setText(TextFont());
                ///////////////////////

                ////////DAILYUSE///////
                 styledText = numberformat.format(DailyData)+DailyData_Str+" / "+numberformat.format(DailyUseData)+DailyUseData_Str ;
                 styledText2 = "\nGÜNLÜK KULLANIM";

                textView_Daily.setText(TextFont());
                ///////////////////////

                handler.postDelayed(this, 1000);
            }
        },0);


    }


    public Spannable TextFont(){
        Spannable wordtoSpan = new SpannableString(styledText+styledText2);
        wordtoSpan.setSpan(new RelativeSizeSpan(1.25f), 0,styledText.length(),0);
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)),styledText.length(),wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return wordtoSpan;
    }
}