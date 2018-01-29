package com.adamoglu.mobilverikullanimi;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.Apps.Quota;
import com.adamoglu.mobilverikullanimi.Apps.ShowOneApp;
import com.adamoglu.mobilverikullanimi.Services.ServiceApps_Test;
import com.adamoglu.mobilverikullanimi.Services.Service_Data;

import java.util.HashMap;

public class Settings extends AppCompatActivity {

    Switch switch_uygulamakullanimi,switch_secilenuygulamabildirim;
    Switch switch_sureklikullanim,switch_günlükveri,switch_veri,switch_uygulamatekrarlandi,switch_baglantikoptu,switch_baglantigeldi;
    Switch switch_indirmeveyükleme,switch_indirme,switch_yükleme;
    Switch switch_tekrarla,switch_hicbirseyyapma,switch_durdur;

    TextView textView_KotadanHarcamayanUygulamalar, textView_SürekliBildirimdeGösterilecekUygulama;
    TextView textView_Sıfırla, textView_Durdur;

    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = getSharedPreferences("",0);
        return prefSettings;
    }

    HashMap<Switch,String> Names = new HashMap<>();

    Animation animate1;

        @Override
    protected void onResume() {
        super.onResume();

            if(textView_KotadanHarcamayanUygulamalar!=null)
                textView_KotadanHarcamayanUygulamalar.setText("  Kotadan Harcamayan Uygulamalar"+" ("+SharedPreferences().getString("QuataAppSize","Yok")+")");


            if(SharedPreferences().getInt("ShowOneApp",0)==0){
                Editor().putBoolean("Setting_SecilenUygulamaGöster",false).commit();
                switch_secilenuygulamabildirim.setChecked(SharedPreferences().getBoolean("Setting_SecilenUygulamaGöster",false));
            }

        }
    ServiceApps_Test serviceApps_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        animate1 = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        animate1.setDuration(500);

        textView_KotadanHarcamayanUygulamalar = (TextView) findViewById(R.id.textView_kotadanharcamayan);
        textView_KotadanHarcamayanUygulamalar.setText("  Kotadan Harcamayan Uygulamalar"+" ("+SharedPreferences().getString("QuataAppSize","")+")");
        textView_KotadanHarcamayanUygulamalar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Quota.class);
                startActivity(i);
            }
        });


        textView_SürekliBildirimdeGösterilecekUygulama = (TextView) findViewById(R.id.textView_uygulamasüreklibildirim);
        if(SharedPreferences().getBoolean(Names.get(switch_secilenuygulamabildirim),false))
            textView_SürekliBildirimdeGösterilecekUygulama.setVisibility(View.VISIBLE);
        else
            textView_SürekliBildirimdeGösterilecekUygulama.setVisibility(View.GONE);

        textView_SürekliBildirimdeGösterilecekUygulama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ShowOneApp.class);
                startActivity(i);
            }
        });

        switch_secilenuygulamabildirim= (Switch) findViewById(R.id.switch_uygulamasüreklibildirim);
        switch_uygulamakullanimi= (Switch) findViewById(R.id.switch_uygulamalar);

        Names.put(switch_uygulamakullanimi,"Setting_UygulamaKullanimi");
        SwichListener(switch_uygulamakullanimi,3.1);
        switch_uygulamakullanimi.setChecked(SharedPreferences().getBoolean(Names.get(switch_uygulamakullanimi),true));

        Names.put(switch_secilenuygulamabildirim,"Setting_SecilenUygulamaGöster");
        SwichListener(switch_secilenuygulamabildirim,3.2);
        switch_secilenuygulamabildirim.setChecked(SharedPreferences().getBoolean(Names.get(switch_secilenuygulamabildirim),false));



        switch_sureklikullanim= (Switch) findViewById(R.id.switch_sureklikullanim);
        Names.put(switch_sureklikullanim,"Setting_SürekliKullanim");
        SwichListener(switch_sureklikullanim,0.1);
        switch_sureklikullanim.setChecked(SharedPreferences().getBoolean(Names.get(switch_sureklikullanim),true));

        switch_günlükveri= (Switch) findViewById(R.id.switch_gunlukveri);
        Names.put(switch_günlükveri,"Setting_Günlükveri");
        SwichListener(switch_günlükveri,0.2);
        switch_günlükveri.setChecked(SharedPreferences().getBoolean(Names.get(switch_günlükveri),true));

        switch_veri= (Switch) findViewById(R.id.switch_veri);
        Names.put(switch_veri,"Setting_Veri");
        SwichListener(switch_veri,0.3);
        switch_veri.setChecked(SharedPreferences().getBoolean(Names.get(switch_veri),true));

        switch_uygulamatekrarlandi= (Switch) findViewById(R.id.switch_uygulamatekrarlandi);
        Names.put(switch_uygulamatekrarlandi,"Setting_Uygulamatekrarlandi");
        SwichListener(switch_uygulamatekrarlandi,0.4);
        switch_uygulamatekrarlandi.setChecked(SharedPreferences().getBoolean(Names.get(switch_uygulamatekrarlandi),true));

        switch_baglantikoptu= (Switch) findViewById(R.id.switch_baglantikoptu);
        Names.put(switch_baglantikoptu,"Setting_Baglantikoptu");
        SwichListener(switch_baglantikoptu,0.5);
        switch_baglantikoptu.setChecked(SharedPreferences().getBoolean(Names.get(switch_baglantikoptu),false));

        switch_baglantigeldi= (Switch) findViewById(R.id.switch_baglantigeldi);
        Names.put(switch_baglantigeldi,"Setting_Baglantigeldi");
        SwichListener(switch_baglantigeldi,0.6);
        switch_baglantigeldi.setChecked(SharedPreferences().getBoolean(Names.get(switch_baglantigeldi),false));



        switch_indirmeveyükleme= (Switch) findViewById(R.id.switch_indirmeveyükleme);
        switch_indirme= (Switch) findViewById(R.id.switch_sadeceindirilen);
        switch_yükleme= (Switch) findViewById(R.id.switch_sadeceyüklenen);

        Names.put(switch_indirmeveyükleme,"Setting_Indirmeveyükleme");
        SwichListener(switch_indirmeveyükleme,1.1);
        switch_indirmeveyükleme.setChecked(SharedPreferences().getBoolean(Names.get(switch_indirmeveyükleme),true));

        Names.put(switch_indirme,"Setting_Indirme");
        SwichListener(switch_indirme,1.2);
        switch_indirme.setChecked(SharedPreferences().getBoolean(Names.get(switch_indirme),false));

        Names.put(switch_yükleme,"Setting_Yükleme");
        SwichListener(switch_yükleme,1.3);
        switch_yükleme.setChecked(SharedPreferences().getBoolean(Names.get(switch_yükleme),false));



        switch_tekrarla= (Switch) findViewById(R.id.switch_tekrarla);
        switch_hicbirseyyapma= (Switch) findViewById(R.id.switch_hicbirseyyapma);
        switch_durdur= (Switch) findViewById(R.id.switch_uygulamayidurdur);

        Names.put(switch_tekrarla,"Setting_Tekrarla");
        SwichListener(switch_tekrarla,2.1);
        switch_tekrarla.setChecked(SharedPreferences().getBoolean(Names.get(switch_tekrarla),true));

        Names.put(switch_hicbirseyyapma,"Setting_Hicbirseyyapma");
        SwichListener(switch_hicbirseyyapma,2.2);
        switch_hicbirseyyapma.setChecked(SharedPreferences().getBoolean(Names.get(switch_hicbirseyyapma),false));

        Names.put(switch_durdur,"Setting_Durdur");
        SwichListener(switch_durdur,2.3);
        switch_durdur.setChecked(SharedPreferences().getBoolean(Names.get(switch_durdur),false));



        textView_Sıfırla = (TextView) findViewById(R.id.sifirla);

        textView_Sıfırla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

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
                        //serviceApps_test = new ServiceApps_Test(getApplicationContext());
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

                    Intent i = new Intent(getApplicationContext(),Service_Data.class);
                    stopService(i);

                    Editor().clear().commit();

                    Editor().putBoolean("AppStop",true).commit();

                    database myDb = new database(getApplicationContext());
                    myDb.DeleteAllData();


                    Intent intent = new Intent(getApplicationContext().getApplicationContext(), Startup.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);


                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Sıfırlanamadı", Toast.LENGTH_SHORT).show();
                }

            }
        });

        textView_Durdur = (TextView) findViewById(R.id.durdur);


        textView_Durdur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
                    //serviceApps_test = new ServiceApps_Test(getApplicationContext());
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

                Intent i = new Intent(getApplicationContext(),Service_Data.class);
                stopService(i);


            }
        });

        switch_secilenuygulamabildirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch_secilenuygulamabildirim.isChecked()){
                    if(SharedPreferences().getInt("ShowOneApp",0)==0){
                        Intent i = new Intent(getApplicationContext(), ShowOneApp.class);
                        startActivity(i);
                    }
                }
            }
        });

    }


    public void SwichListener(final Switch Switch, final double ID){

        Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(Switch.isChecked()){

                    if(ID==1.1){
                        switch_indirme.setChecked(false);
                        switch_yükleme.setChecked(false);
                    }else if(ID==1.2){
                        switch_indirmeveyükleme.setChecked(false);
                        switch_yükleme.setChecked(false);
                    }else if(ID==1.3){
                        switch_indirmeveyükleme.setChecked(false);
                        switch_indirme.setChecked(false);
                    }

                    if(ID==2.1){
                        switch_hicbirseyyapma.setChecked(false);
                        switch_durdur.setChecked(false);
                    }else if(ID==2.2){
                        switch_tekrarla.setChecked(false);
                        switch_durdur.setChecked(false);
                    }else if(ID==2.3){
                        switch_tekrarla.setChecked(false);
                        switch_hicbirseyyapma.setChecked(false);
                    }

                    if(ID==3.1){
                        switch_secilenuygulamabildirim.setChecked(false);
                    }else if(ID==3.2){
                        switch_uygulamakullanimi.setChecked(false);
                    }


                    Editor().putBoolean(Names.get(Switch),true).commit();
                    //Bu Özellik Çok Yakında//

                    if(ID==0.5){
                        if(Switch.isChecked()){
                            Toast.makeText(getApplicationContext(), "Bu Özellik Çok Yakında", Toast.LENGTH_SHORT).show();
                            Switch.setChecked(false);
                            Editor().putBoolean(Names.get(Switch),false).commit();
                        }
                    }

                    if(ID==0.6){
                        if(Switch.isChecked()){
                            Toast.makeText(getApplicationContext(), "Bu Özellik Çok Yakında", Toast.LENGTH_SHORT).show();
                            Switch.setChecked(false);
                            Editor().putBoolean(Names.get(Switch),false).commit();
                        }
                    }


                }else {


                    if(ID==0.1){
                        Service_Data.CloseOnNotification(1);
                    }else if(ID==0.2){
                        Service_Data.CloseOnNotification(2);
                    }
                    else if(ID==0.3){
                        Service_Data.CloseOnNotification(5);
                    }


                    if(ID==1.1){
                        if(!switch_indirme.isChecked() && !switch_yükleme.isChecked()){
                            Switch.setChecked(true);return;
                        }
                    }else if(ID==1.2){
                        if(!switch_indirmeveyükleme.isChecked() && !switch_yükleme.isChecked()){
                            Switch.setChecked(true);return;
                        }
                    }else if(ID==1.3){
                        if(!switch_indirmeveyükleme.isChecked() && !switch_indirme.isChecked()){
                            Switch.setChecked(true);return;
                        }
                    }

                    if(ID==2.1){
                        if(!switch_hicbirseyyapma.isChecked() && !switch_durdur.isChecked()){
                            Switch.setChecked(true);return;
                        }
                    }else if(ID==2.2){
                        if(!switch_tekrarla.isChecked() && !switch_durdur.isChecked()){
                            Switch.setChecked(true);return;
                        }
                    }else if(ID==2.3){
                        if(!switch_tekrarla.isChecked() && !switch_hicbirseyyapma.isChecked()){
                            Switch.setChecked(true);return;
                        }
                    }

                    if(ID==3.1){
                        ServiceApps_Test.CloseNotification();
                    }else if(ID==3.2){
                        ServiceApps_Test.CloseNotification();
                    }

                    Editor().putBoolean(Names.get(Switch),false).commit();
                }

                if(ID==3.1){
                    if(Switch.isChecked()){
                        new ServiceApps_Test(getApplicationContext());
                        ServiceApps_Test.FinishOneApp();
                        ServiceApps_Test.StartServieApps_Test();
                    }else {
                        ServiceApps_Test.StopServieApps_Test();
                    }
                }

                if(ID==3.2){

                    if(Switch.isChecked()){
                        new ServiceApps_Test(getApplicationContext());
                        ServiceApps_Test.StartOneApp();
                        ServiceApps_Test.StartServieApps_Test();
                    }else {
                        ServiceApps_Test.StopServieApps_Test();
                    }

                    if(Switch.isChecked()){
                        textView_SürekliBildirimdeGösterilecekUygulama.setVisibility(View.VISIBLE);
                        textView_SürekliBildirimdeGösterilecekUygulama.startAnimation(animate1);
                        if(SharedPreferences().getInt("SaveShowOneApp",0)!=0)
                            Editor().putInt("ShowOneApp",SharedPreferences().getInt("SaveShowOneApp",0)).commit();
                        Editor().putInt("SaveShowOneApp",0).commit();


                    }else {
                        textView_SürekliBildirimdeGösterilecekUygulama.setVisibility(View.GONE);
                        Editor().putInt("SaveShowOneApp",SharedPreferences().getInt("ShowOneApp",0)).commit();
                        Editor().putInt("ShowOneApp",0).commit();
                    }


                }


                Service_Data.SetSetting(getApplicationContext());


            }
        });

    }
}
