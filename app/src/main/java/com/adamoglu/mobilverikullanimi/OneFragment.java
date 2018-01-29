package com.adamoglu.mobilverikullanimi;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.adamoglu.mobilverikullanimi.Apps.AppUsage;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.NumberFormat;


public class OneFragment extends Fragment implements ViewSwitcher.ViewFactory {


    ListView listView;
    TextSwitcher textView_DataUsage;
    TextSwitcher textView_Speed;
    TextSwitcher textView_Daily;
    TextSwitcher textView_Day;
    TextView textView_SpeedTest;
    TextView textView_Apps;
    static SlidingUpPanelLayout UpPanel;
    ArcProgress arcProgress_Use;
    CircleProgress circleProgress_Daily;
    LinearLayout DragView;
    ConstraintLayout constraintLayout_App;
    ConstraintLayout constraintLayout_SpeedTest;
    ScrollView scrollView;

    NumberFormat numberformat = NumberFormat.getInstance();
    static Handler handler;

    boolean Startup_ArcUse=true;
    boolean Control_Anime_Open=true;
    boolean Control_Anime_Close=true;
    boolean Control_Notification;

    double UseData;
    double MyData;
    double DailyData;
    double DailyUseData;

    String styledText_first;
    String styledText_second;
    String Data_Str;
    String Data_Str_Second;


    public boolean Exit = false;
    public void StopOneFragment(){
        if(handler!=null)
            handler.removeMessages(0);
        Exit=true;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(handler!=null)
            handler.removeMessages(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(handler!=null)
            handler.removeMessages(0);
    }

    public void Anime(ArcProgress arcProgress, int data){
        //ArcProgrss için animasyon
        ObjectAnimator animation = ObjectAnimator.ofInt(arcProgress, "progress", 0, data);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = this.getActivity().getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = this.getActivity().getSharedPreferences("",0);
        return prefSettings;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void ListViewScrool() {
        //ListView ile SlidingLayoud Karışmasını önleme
        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
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
                }catch (Exception ex){
                    Toast.makeText(getContext(), "ListViewScrool()'da Hata!", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    public void ArcProgress_Use() {
        //ArcProgressi ayarlama
        try {
            UseData = Double.parseDouble(SharedPreferences().getString("USE_DATA_MB", "0"));
            MyData = Double.parseDouble(SharedPreferences().getString("MYDATA", "0"));
            int Percent = (int) ((100*UseData)/MyData);//Yüzdelik Alınıyor

            if(Percent>100){
                Percent=100;
            }

            if(Startup_ArcUse){
                Startup_ArcUse=false;
                Anime(arcProgress_Use,Percent);
            }
            arcProgress_Use.setProgress(Percent);

        }catch (Exception ex){
            Toast.makeText(getContext(), "ArcProgress_Use()'de Hata", Toast.LENGTH_SHORT).show();
        }

    }

    public void ArcProgress_Daily(){
        //ArcProgressi ayarlama
        try {
            DailyData = Double.parseDouble(SharedPreferences().getString("DAILY_DATA_MB", "0"));
            DailyUseData = Double.parseDouble(SharedPreferences().getString("DAILY_USEDATA_MB","0"));
            int Percent = (int) ((100*DailyUseData)/DailyData);

            if(Percent>100){
                Percent=100;
            }
            circleProgress_Daily.setProgress(Percent);

        }catch (Exception ex){
            Toast.makeText(getContext(), "ArcProgress_Daily()'de Hata", Toast.LENGTH_SHORT).show();
        }
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
            Toast.makeText(getContext(), "Unit_Converter(Double)'de Hata", Toast.LENGTH_SHORT).show();
            return 0;
        }

        return Data;
    }

    public void finish(){
        getActivity().finish();
    }

    public Spannable TextFont(){
        Spannable wordtoSpan = null;
        try {
            wordtoSpan = new SpannableString(styledText_first + styledText_second);
            wordtoSpan.setSpan(new RelativeSizeSpan(1.35f), 0, styledText_first.length(),0);
            wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Text)), styledText_first.length(),wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }catch (Exception ex){
            Toast.makeText(getContext(), "TextFont()'da Hata", Toast.LENGTH_SHORT).show();
        }
        return wordtoSpan;
    }

    public OneFragment() {
        // Required empty public constructor
    }

    public static boolean CloseUpSliding(){

        if(UpPanel.getPanelState()!=SlidingUpPanelLayout.PanelState.COLLAPSED){
            UpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }else {
            return true;
        }
        return false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.activity_main, container, false);


        try {
            listView = (ListView) view.findViewById(R.id.ListView);
            textView_DataUsage = (TextSwitcher) view.findViewById(R.id.textView_DataUse);
            textView_SpeedTest = (TextView) view.findViewById(R.id.textView_SpeedTest);
            textView_Speed = (TextSwitcher) view.findViewById(R.id.textView_Speed);
            textView_Day = (TextSwitcher) view.findViewById(R.id.textView_Day);
            textView_Daily = (TextSwitcher) view.findViewById(R.id.textView_Daily);
            UpPanel = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);
            circleProgress_Daily = (CircleProgress) view.findViewById(R.id.arc_progress_UseDaily);
            arcProgress_Use = (ArcProgress) view.findViewById(R.id.arc_progress_UseData);
            DragView = (LinearLayout) view.findViewById(R.id.dragView);
            constraintLayout_App= (ConstraintLayout) view.findViewById(R.id.constraintLayout_Apps);
            constraintLayout_SpeedTest= (ConstraintLayout) view.findViewById(R.id.constraintLayout_SpeedTest);
            textView_Apps= (TextView) view.findViewById(R.id.textView_App);
            scrollView= (ScrollView) view.findViewById(R.id.scrollView);
        }catch (Exception ex){
            Toast.makeText(getContext(), "findViewById'lerde Hata", Toast.LENGTH_SHORT).show();
        }


        constraintLayout_App.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),AppUsage.class);
                startActivity(i);
            }
        });

        constraintLayout_SpeedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),SpeedTest.class);
                startActivity(intent);
            }
        });

        scrollView.post(new Runnable() {
            public void run() {
                scrollView.scrollTo(0,scrollView.getBottom());
            }
        });

        scrollView.postDelayed(new Runnable() {
            public void run() {
                scrollView.fullScroll(scrollView.FOCUS_UP);
            }
        },700);


        UpPanel.setVisibility(UpPanel.VISIBLE);

        Animation animate1 = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        animate1.setDuration(500);

        UpPanel.startAnimation(animate1);

        ListViewScrool();

        numberformat.setMinimumFractionDigits(1);
        numberformat.setMaximumFractionDigits(1);

        new getData(getActivity(), listView);

        UpPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {}
            @Override
            public void onPanelCollapsed(View panel) {
                if(Control_Notification){
                    Control_Anime_Open=true;
                    Control_Anime_Close=true;}
            }
            @Override
            public void onPanelExpanded(View panel) {
                new getData(getActivity(), listView);
            }
            @Override
            public void onPanelAnchored(View panel) {}
            @Override
            public void onPanelHidden(View panel) {}
        });


        textView_Speed.setFactory(OneFragment.this);
        textView_DataUsage.setFactory(OneFragment.this);
        textView_Daily.setFactory(OneFragment.this);
        textView_Day.setFactory(OneFragment.this);

        final Animation in = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        in.setDuration(150);
        final Animation out = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        out.setDuration(100);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                if(!Exit){
                    ArcProgress_Use();
                    ArcProgress_Daily();

                    try {


                        /////////SPEEDTEST//////
                        styledText_first ="Speed Test";
                        styledText_second ="\n";

                        textView_SpeedTest.setText(TextFont());
                        ////////////////////////

                        /////////DATAUSE////////
                        styledText_first =numberformat.format(Unit_Converter(MyData))+Data_Str+" / "+numberformat.format(Unit_Converter(UseData))+Data_Str;
                        styledText_second ="\nKULLANILAN";

                        textView_DataUsage.setInAnimation(in);
                        textView_DataUsage.setOutAnimation(out);


                        textView_DataUsage.setText(TextFont());
                        ///////////////////////

                        /////////SPEED////////
                        styledText_first = SharedPreferences().getString("SPEED","0.0B/s") ;
                        styledText_second = "\nİNDİRME HIZI";

                        textView_Speed.setInAnimation(in);
                        textView_Speed.setOutAnimation(out);

                        textView_Speed.setText(TextFont());


                        ///////////////////////

                        ////////DAILYUSE///////
                        styledText_first = numberformat.format(Unit_Converter(DailyData))+Data_Str+" / "+numberformat.format(Unit_Converter(DailyUseData))+Data_Str ;
                        styledText_second = "\nGÜNLÜK KULLANIM";

                        textView_Daily.setInAnimation(in);
                        textView_Daily.setOutAnimation(out);

                        textView_Daily.setText(TextFont());
                        ///////////////////////

                        //////////DAY//////////
                        if(SharedPreferences().getString("DAY",SharedPreferences().getString("COUNT_DAY_STR","---")).trim().equals("0")){
                            styledText_first = "Süre Doldu";
                        }else {
                            styledText_first = SharedPreferences().getString("DAY",SharedPreferences().getString("COUNT_DAY_STR","---"))+" GÜN";
                        }

                        styledText_second = "\nKALAN GÜN";

                        textView_Day.setInAnimation(in);
                        textView_Day.setOutAnimation(out);

                        textView_Day.setText(TextFont());
                        ///////////////////////

                        ////////APP////////////
                        if(SharedPreferences().getInt("AppSize",0)!=0 && SharedPreferences().getInt("AppSize_UseData",0)!=0){
                            styledText_first = String.valueOf(SharedPreferences().getInt("AppSize",0))+" Uygulamanız Var";
                            styledText_second = "\n"+String .valueOf(SharedPreferences().getInt("AppSize_UseData",0))+" Uygulamanız Veri Kullanıyor";
                        }else {
                            styledText_first = "Uygulamalar";
                            styledText_second = "\n";
                        }
                        textView_Apps.setText(TextFont());
                        ///////////////////////

                    }catch (Exception ex){
                        Toast.makeText(getContext(), "Bilgi Oluşturulurken Hata", Toast.LENGTH_SHORT).show();
                    }


                    handler.postDelayed(this, 3000);
                }


            }
        },0);

        return view;
    }

    @Override
    public View makeView() {
        TextView t = new TextView(getActivity());
        t.setGravity(Gravity.CENTER);
        t.setTextColor(Color.parseColor("#659EC7"));
        t.setTypeface(null, Typeface.BOLD);
        t.setTextSize(12);
        return t;
    }


}
