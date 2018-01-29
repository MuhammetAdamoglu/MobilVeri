package com.adamoglu.mobilverikullanimi;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.text.NumberFormat;

public class Startup extends AppCompatActivity {

    public SharedPreferences SharedPreferences(){
        final SharedPreferences prefSettings =  getSharedPreferences("", Context.MODE_PRIVATE);
        return prefSettings;
    }
    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    boolean control=true;
    boolean control2=true;
    Handler handler;

    @Override
    protected void onResume() {
        super.onResume();
        control=true;
    }


    NumberFormat numberformat = NumberFormat.getInstance();
    PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);

        packageManager = getApplicationContext().getPackageManager();

        numberformat.setMinimumFractionDigits(1);
        numberformat.setMaximumFractionDigits(1);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                if (ContextCompat.checkSelfPermission(Startup.this,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if(control2){
                        ActivityCompat.requestPermissions(Startup.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                1);
                    }

                    handler.postDelayed(this, 500);
                }else {


                    AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                    int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                            android.os.Process.myUid(), getPackageName());
                    if (mode == AppOpsManager.MODE_ALLOWED) {

                        if(SharedPreferences().getBoolean("Control_StartApp",false)){
                            Intent i = new Intent(getApplicationContext(),Tabs.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }else {
                            Editor().clear().commit();


                            Intent i = new Intent(getApplicationContext(),Add_Information.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }


                    }else {
                        if (control){
                            control=false;
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                        }


                        handler.postDelayed(this, 500);
                    }
                }


            }
        }, 500);

    }

}
