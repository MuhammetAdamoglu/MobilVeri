package com.adamoglu.mobilverikullanimi;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;


public class Add_Information extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    int nowPosition1;
    int nowPosition2;

    ViewPagerAdapter adapter;

    public void CloseNotification(){
        NotificationManager Main_notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Main_notificationManager.cancel(6);
    }

    // Called implicitly when device is about to sleep or application is backgrounded
    protected void onPause(){
        super.onPause();
        CloseNotification();
    }

    // Called implicitly when device is about to wake up or foregrounded
    protected void onResume(){
        super.onResume();
        CloseNotification();
    }


    @Override
    protected void onStart() {
        super.onStart();

        CloseNotification();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CloseNotification();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add__information);

        final SharedPreferences prefSettings =  getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(0);
        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if(nowPosition1==0){
                    if(!Control_AddData()){
                        setCurrentItem(0,true);
                    }
                }

                if(nowPosition2==1){
                    if(!Control_AddDate()){
                        setCurrentItem(1,true);
                    }
                }


            }
            @Override
            public void onPageSelected(int position) {
                if(position<=0)
                    nowPosition1=position;

                if(position<=1)
                    nowPosition2=position;


            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });



    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());



        adapter.addFragment(new Add_Data(), "1");
        adapter.addFragment(new Add_Date(), "2");
        adapter.addFragment(new Option_Fragment(), "3");
        adapter.addFragment(new Option_FragmentApps(), "4");
        adapter.addFragment(new Option_Fragment2(), "5");


        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }


        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public void setCurrentItem (int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }


    int kontrol_radiobutton;
    long Fark_Tarih;

    Get_TrafficStats get_trafficStats = new Get_TrafficStats(this);

    public boolean Control_AddDate(){

        Toast.makeText(this, "Girdi", Toast.LENGTH_SHORT).show();

        final SharedPreferences prefSettings =  getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();

        kontrol_radiobutton=prefSettings.getInt("kontrol_radiobutton",0);
        Fark_Tarih=prefSettings.getLong("Fark_Tarih",0);

        final Calendar now = Calendar.getInstance();

        if (kontrol_radiobutton == 0) {
            return false;
        } else {
            if (Fark_Tarih <= 0) {
              return false;
            } else {



                editor.putInt("COUNT_DAY", (int) Fark_Tarih);
                editor.putString("COUNT_DAY_STR", String.valueOf((int) Fark_Tarih));

                editor.putBoolean("Control_NextDate",true).commit();

                new Data().execute();

                int ay = now.get(Calendar.MONTH);
                int yıl = now.get(Calendar.YEAR);
                int gun = now.get(Calendar.DATE);

                editor.putString("AY", String.valueOf(ay));
                editor.putString("GUN", String.valueOf(gun));
                editor.putString("YIL", String.valueOf(yıl));

                now.add(Calendar.DATE, (int) Fark_Tarih);
                editor.putInt("AY2", now.get(Calendar.MONTH));
                editor.putInt("GUN2", now.get(Calendar.DATE));
                editor.putInt("YIL2", now.get(Calendar.YEAR));


                editor.commit();

                return true;
            }
        }
    }

    String Edittext_Data;
    boolean control_DataType;

    public boolean Control_AddData(){
        final SharedPreferences prefSettings =  getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();

        Edittext_Data = prefSettings.getString("Edittext_Data","");
        control_DataType = prefSettings.getBoolean("control_DataType",false);


        if (Edittext_Data.trim().equals("")){
           return false;
        }
        else {
            if(control_DataType){

                editor.putString("MYDATA", Edittext_Data).commit();
            }
            else {

                editor.putString("MYDATA", String.valueOf(Double.parseDouble(Edittext_Data)*1024)).commit();
            }

            CloseKeyBoard();
            return true;
        }
    }

    public void CloseKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class Data extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {

                final SharedPreferences prefSettings =  getSharedPreferences("", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = prefSettings.edit();

                if(get_trafficStats.Get_Mobile(getApplicationContext()) !=0) {
                    editor.putString("DELTA", String.valueOf(get_trafficStats.Get_Mobile(getApplicationContext()))).commit();
                }

                if(get_trafficStats.Get_Wifi(getApplicationContext()) !=0){
                    editor.putString("DELTA_WIFI",String.valueOf(get_trafficStats.Get_Wifi(getApplicationContext()))).commit();
                }


                editor.putString("SpeedTest",String.valueOf(get_trafficStats.Get_Total(getApplicationContext()))).commit();

            }catch (SecurityException ex){

            }

            return null;
        }{

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

}