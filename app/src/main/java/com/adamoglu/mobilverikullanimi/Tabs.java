
package com.adamoglu.mobilverikullanimi;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;


import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;


public class Tabs extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private static ViewPager viewPager;
    private ImageView ımageView;


    public SharedPreferences SharedPrefences() {
        final SharedPreferences prefSettings = getSharedPreferences("",0);
        return prefSettings;
    }
    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    static int nowPosition=0;
    @Override
    public void onBackPressed() {
        if(nowPosition==0){
            if(OneFragment.CloseUpSliding()){
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        }

        if(nowPosition==1)
            ThreeFragment.CloseUpSliding();
    }

    public static void setCurrentItem (int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }
    public SharedPreferences SharedPreferences(){
        final SharedPreferences prefSettings =  getSharedPreferences("", Context.MODE_PRIVATE);
        return prefSettings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        if(!SharedPrefences().getBoolean("AppStop",false)){
            Intent serviceLauncher = new Intent(getApplicationContext(), Broadcast.class);
            sendBroadcast(serviceLauncher);

            Intent serviceLauncher1 = new Intent(getApplicationContext(), Broadcast.class);
            sendBroadcast(serviceLauncher1);
        }



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                nowPosition=position;

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ımageView= (ImageView) findViewById(R.id.imageView);
        ımageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Settings.class);
                startActivity(i);
            }
        });

    }


    private void setupTabIcons() {
            //tabLayout.getTabAt(2).setIcon(R.drawable.icon_settings);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "MOBİL");
        adapter.addFragment(new ThreeFragment(), "WIFI");
        //adapter.addFragment(new TwoFragment(), "");

        viewPager.setAdapter(adapter);
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
            if(position==2){
                return null;
            }
            return mFragmentTitleList.get(position);
        }
    }

}