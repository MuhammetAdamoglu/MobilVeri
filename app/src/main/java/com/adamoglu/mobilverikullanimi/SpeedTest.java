package com.adamoglu.mobilverikullanimi;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.NumberFormat;
import java.util.ArrayList;

public class SpeedTest extends AppCompatActivity implements ViewSwitcher.ViewFactory {



    String Data_Str_Second;
    String Data_Str;
    String styledText;
    String styledText_second;
    String WifiName,WifiMac;

    double Speed=0;
    double Mpbs=0;
    double Speed_Old=0;
    double save_for_anime=0;
    double progressBar_Old=0;
    double total=0;
    double max_speed;

    int save_for_anime_timer =0;
    int max=6;
    int control_zero=0;
    int sayac=0;
    int sayac_timer =0;
    int count_maxSpeed=0;
    int count_wv=0;

    boolean control=true;
    boolean control_finish=false;
    boolean control_startTimer=true;
    double total_use =0;
    double best_speed =0;
    double old_best_speed =0;

    long init, now_ms, time_ms;

    Time time = new Time();

    WebView webView1;
    WebView webView2;
    WebView webView3;
    WebView webView4;

    ArcProgress arcProgress;
    ArcProgress arcProgress2;
    TextView button;
    TextView tv,tv_use, tv_down;
    TextView tv_1,tv_2,tv_3,tv_4,tv_5;
    TextView tv_best_Speed,tv_best_DonwSpeed;
    ListView listView;
    static SlidingUpPanelLayout UpPanel;


    NumberFormat numberformat = NumberFormat.getInstance();
    ArrayList<Double> dizi = new ArrayList<>();
    database myDb;


    static Handler handler;
    static Handler handler_timer;

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
                    Toast.makeText(getApplicationContext(), "ListViewScrool()'da Hata!", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }

    public void Anime(ArcProgress arcProgress,int time, int first, int second){
        //ArcProgress Animasyonu
        try {
            ObjectAnimator animation = ObjectAnimator.ofInt(arcProgress, "progress", first, second);
            animation.setDuration(time);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
            tv.setText(numberformat.format((double) this.arcProgress.getProgress()/1000)+" Mbps");
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Anime(ArcProgress,int,int,int)'de Hata", Toast.LENGTH_SHORT).show();
        }
    }

    public void finish(){
        //Hız Testi bitince yapılacaklar
        try {
            handler_timer.removeMessages(0);
            handler.removeMessages(0);
            button.setText("TEST ET");
            Control_Finish_Timer=false;
            tv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            this.control = false;
            webView1.stopLoading();
            webView2.stopLoading();
            webView3.stopLoading();
            webView4.stopLoading();
        }catch (Exception ex){
            //Toast.makeText(getApplicationContext(), "finish()'de Hata", Toast.LENGTH_SHORT).show();
        }
    }


    public Spannable TextFont(){
        Spannable wordtoSpan=null;
        try {
            wordtoSpan = new SpannableString(styledText+ styledText_second);
            wordtoSpan.setSpan(new RelativeSizeSpan(1.25f), 0,styledText.length(),0);
            wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.Text)),
                    styledText.length(),
                    wordtoSpan.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "TextFont()'da Hata", Toast.LENGTH_SHORT).show();
        }
        return wordtoSpan;
    }

    public void WebViews(){
        //İnternetin kullanımını maximuma çıkartmak için 4 adet webView kullanılıyor.
        try {
            webView1 = (WebView) findViewById(R.id.WebWiew1);
            webView1.getSettings().setLoadsImagesAutomatically(true);
            webView1.getSettings().setJavaScriptEnabled(true);
            webView1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView1.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    /*count_wv+=1;
                    stop_handler();*/
                }
            });
            webView1.loadUrl("https://upload.wikimedia.org/wikipedia/commons/2/2c/A_new_map_of_Great_Britain_according_to_the_newest_and_most_exact_observations_%288342715024%29.jpg");


            webView2 = (WebView) findViewById(R.id.WebWiew2);
            webView2.getSettings().setLoadsImagesAutomatically(true);
            webView2.getSettings().setJavaScriptEnabled(true);
            webView2.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView2.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    /*count_wv+=1;
                    stop_handler();*/
                }
            });
            webView2.loadUrl("https://upload.wikimedia.org/wikipedia/commons/2/2c/A_new_map_of_Great_Britain_according_to_the_newest_and_most_exact_observations_%288342715024%29.jpg");


            webView3 = (WebView) findViewById(R.id.WebWiew3);
            webView3.getSettings().setLoadsImagesAutomatically(true);
            webView3.getSettings().setJavaScriptEnabled(true);
            webView3.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView3.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    /*count_wv+=1;
                    stop_handler();*/
                }
            });
            webView3.loadUrl("https://upload.wikimedia.org/wikipedia/commons/2/2c/A_new_map_of_Great_Britain_according_to_the_newest_and_most_exact_observations_%288342715024%29.jpg");


            webView4 = (WebView) findViewById(R.id.WebWiew4);
            webView4.getSettings().setLoadsImagesAutomatically(true);
            webView4.getSettings().setJavaScriptEnabled(true);
            webView4.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView4.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                   /*count_wv+=1;
                    stop_handler();*/
                }
            });
            webView4.loadUrl("https://upload.wikimedia.org/wikipedia/commons/2/2c/A_new_map_of_Great_Britain_according_to_the_newest_and_most_exact_observations_%288342715024%29.jpg");

        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "WebViews(View)'de Hata", Toast.LENGTH_SHORT).show();
        }
    }

    boolean Control_Finish_Timer=false;

    public void Timer(){
        //Geriye sayım başlatılıyor
        try {
            handler_timer = new Handler();
            handler_timer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sayac_timer +=1;
                    if(sayac_timer >=499){
                        sayac_timer =500;
                        Control_Finish_Timer=true;

                    }

                    Anime(arcProgress2,10, save_for_anime_timer, sayac_timer);
                    save_for_anime_timer = sayac_timer;

                    handler_timer.postDelayed(this, 10);
                }
            }, 0);
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Timer()'de Hata", Toast.LENGTH_SHORT).show();
        }
    }


    public double MBorGB( double data){
        if(dizi.size()!=0 && Mpbs!=0){

            if(data<0.98){
                data=data*1024;
                Data_Str_Second =" KB/s";
                Data_Str = " KB";
                if(data<1){
                    data=data*1024;
                    Data_Str_Second =" B/s";
                    Data_Str =" B";
                }
            }
            else if(data>999){
                data=data/1024;
                Data_Str_Second =" GB/s";
                Data_Str =" GB";
                if(data>999){
                    data=data/1024;
                    Data_Str_Second =" TB/s";
                    Data_Str =" TB";
                    if(data>999){
                        data=data/1024;
                        Data_Str_Second =" PB/s";
                        Data_Str =" PB";
                    }
                }
            }
            else{
                Data_Str_Second = " MB/s";
                Data_Str =" MB";
            }
            if(data<=0){
                data=0;
                Data_Str_Second ="B/s";
                Data_Str =" B";
            }
        }else {
            data=0.0;
            Data_Str_Second =" B/s";
            Data_Str =" B";
        }

        return data;
    }

    @Override
    public void onBackPressed() {


        if(UpPanel.getPanelState()!=SlidingUpPanelLayout.PanelState.COLLAPSED){
            UpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }else {

            finish();
            Intent i = new Intent(getApplication(),Tabs.class);
            startActivity(i);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);

        webView2 = (WebView) findViewById(R.id.WebWiew2);
        webView3 = (WebView) findViewById(R.id.WebWiew3);
        webView4 = (WebView) findViewById(R.id.WebWiew4);
        tv = (TextView) findViewById(R.id.textView3);
        arcProgress = (ArcProgress) findViewById(R.id.arc_progress);
        arcProgress2 = (ArcProgress) findViewById(R.id.arc_progress_2);
        button = (TextView) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.ListView);
        UpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        tv_1 = (TextView) findViewById(R.id.textView_1);
        tv_2 = (TextView) findViewById(R.id.textView_2);
        tv_3 = (TextView) findViewById(R.id.textView_3);
        tv_4 = (TextView) findViewById(R.id.textView_4);
        tv_5 = (TextView) findViewById(R.id.textView_5);
        tv_use = (TextView) findViewById(R.id.textView_Use);
        tv_down = (TextView) findViewById(R.id.textView_download);
        tv_best_DonwSpeed = (TextView) findViewById(R.id.textView6);
        tv_best_Speed = (TextView) findViewById(R.id.textView7);
        myDb = new database(getApplicationContext());

        try {
            OneFragment oneFragment = new OneFragment();
            oneFragment.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListViewScrool();

        tv.setText("MBPS");
        tv_down.setText("İndirme Hızı");
        tv_down.setTextColor(Color.parseColor("#80659EC7"));
        tv_use.setText("Hız Testinde Kullanılan");
        tv_use.setTextColor(Color.parseColor("#60659EC7"));

        new getData_SpeedTest(getApplicationContext(), listView);

        UpPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {}
            @Override
            public void onPanelCollapsed(View panel) {}
            @Override
            public void onPanelExpanded(View panel) {
                new getData_SpeedTest(getApplicationContext(), listView);}
            @Override
            public void onPanelAnchored(View panel) {}
            @Override
            public void onPanelHidden(View panel) {}
        });


        final Animation in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        in.setDuration(500);
        final Animation out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        out.setDuration(500);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Speed=0;
                Speed_Old=0;
                max=6;
                total=0;
                control=true;
                sayac=0;
                sayac_timer =0;
                total_use =0;
                control_finish=false;
                max_speed=0;
                count_maxSpeed=0;
                count_wv=0;
                Mpbs=0;
                progressBar_Old=0;
                best_speed =0;
                control_zero=0;
                old_best_speed =0;
                control_startTimer=true;

                dizi.clear();

                try {
                    if(button.getText().toString().trim().equals("TEST ET")){


                        if(CheckNetwork_.isInternetAvailable(getApplicationContext())) //returns true if internet available
                        {

                            button.setText("DURDUR");
                            button.setClickable(false);
                            tv.setTextColor(getResources().getColor(R.color.Text));

                            tv_best_DonwSpeed.startAnimation(out);
                            tv_best_Speed.startAnimation(out);

                            tv_best_DonwSpeed.setText("");
                            tv_best_Speed.setText("");

                            Anime(arcProgress,900, (int) save_for_anime,0);
                            Anime(arcProgress2,900, save_for_anime_timer,0);
                            save_for_anime=0;
                            save_for_anime_timer =0;

                            WebViews();

                            numberformat.setMinimumFractionDigits(1);
                            numberformat.setMaximumFractionDigits(1);

                            Speed_Old =  ((double) TrafficStats.getTotalRxBytes()) / 1048576;



                            handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    button.setClickable(true);

                                    if(control_startTimer){
                                        control_startTimer=false;
                                        Timer();
                                    }


                                    init=System.currentTimeMillis();

                                    sayac += 1;


                                    Speed = ((double) (TrafficStats.getTotalRxBytes()) / 1048576) - Speed_Old;
                                    Speed_Old = ((double) (TrafficStats.getTotalRxBytes())) / 1048576;

                                    if (control) {
                                        if(sayac<9){
                                            if(dizi.size()>=4){
                                                dizi.remove(0);
                                            }

                                            if((Speed * 8)>0){
                                                dizi.add(Speed * 8);
                                                control_zero=0;
                                            }else {
                                                control_zero+=1;
                                                if(control_zero==3) {
                                                    total=0;
                                                }
                                            }

                                            for (int a = 0; a < dizi.size(); a++) {
                                                total += dizi.get(a);
                                            }


                                            if(dizi.size()!=0){
                                                Mpbs=total / dizi.size();
                                                Anime(arcProgress, 800, (int) save_for_anime,(int) (Mpbs * 1000));
                                                save_for_anime = (total * 1000 / dizi.size());
                                            }
                                            if(Mpbs>best_speed){
                                                best_speed=Mpbs;
                                            }
                                        }

                                    }


                                    if (Mpbs * 1000 > max*1000-1000) {

                                        max = (int) ((Mpbs)*2);

                                        arcProgress.setMax(max*1000);

                                        tv_1.setText("0");
                                        tv_2.setText(String.valueOf(max/4));
                                        tv_3.setText(String.valueOf(max*3/4));
                                        tv_4.setText(String.valueOf(max));
                                        tv_5.setText(String.valueOf(max/2));
                                    }

                                    total_use = total_use +Speed;

                                    tv_down.setText(numberformat.format(MBorGB(Mpbs/8))+""+ Data_Str);
                                    tv_use.setText(numberformat.format(MBorGB(total_use))+ Data_Str_Second);



                                    if(Control_Finish_Timer){
                                        finish();
                                        handler_timer.removeMessages(0);

                                        tv_best_DonwSpeed.startAnimation(in);
                                        tv_best_Speed.startAnimation(in);


                                        styledText=numberformat.format((best_speed/8)*1024)+" KB/s";
                                        styledText_second ="\nEn Yüksek İndirme Hızı";
                                        tv_best_DonwSpeed.setText(TextFont());

                                        styledText=numberformat.format(best_speed)+" Mbps";
                                        styledText_second ="\nGörülen En Yüksek Hız";
                                        tv_best_Speed.setText(TextFont());

                                        tv.setText(numberformat.format((double) arcProgress.getProgress()/1000)+" Mbps");

                                        WifiName=getWifiName(getApplicationContext());
                                        WifiMac=getWifiMacAdress();

                                        if(WifiName==null){
                                            WifiName="Mobil";
                                            WifiMac="0";
                                        }

                                        if(myDb.UpdateData_SpeedTest(numberformat.format(Mpbs),WifiName.substring(1,WifiName.length()-1),getWifiMacAdress())==0){
                                            myDb.Add_SpeedTest(WifiName.substring(1,WifiName.length()-1),numberformat.format(Mpbs),WifiMac);
                                        }

                                    }else {
                                        now_ms =System.currentTimeMillis();
                                        time_ms = now_ms -init;

                                        handler.postDelayed(this,1000-time_ms);
                                    }

                                    total = 0;

                                }
                            }, 1000);



                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"INTERNETİNİZ YOK",1000).show();
                        }

                    }else {

                        handler_timer.removeMessages(0);
                        finish();
                    }

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public View makeView() {
        TextView t = new TextView(getApplicationContext());
        t.setGravity(Gravity.CENTER);
        t.setTextColor(Color.parseColor("#659EC7"));
        t.setTypeface(null, Typeface.BOLD);
        t.setTextSize(14);
        return t;
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

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getBSSID();
        return macAddress;
    }


}

class CheckNetwork_ {




    public static boolean isInternetAvailable(Context context)
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null)
        {
            return false;
        }
        else
        {
            if(info.isConnected())
            {
                return true;
            }
            else
            {
                return true;
            }

        }
    }
}