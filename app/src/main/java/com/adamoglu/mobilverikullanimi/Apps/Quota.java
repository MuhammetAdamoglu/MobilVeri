package com.adamoglu.mobilverikullanimi.Apps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.Adapters.Adapter_Quota;
import com.adamoglu.mobilverikullanimi.R;

import java.util.ArrayList;
import java.util.List;

public class Quota extends AppCompatActivity {

    ListView listView;
    EditText search;
    TextView textView_baslik,textView_pb;
    ConstraintLayout constraintLayout_Shadow;
    ProgressBar progressBar;

    private Adapter_Quota listadapter = null;

    ArrayList<String> dizi_name = new ArrayList();
    ArrayList<Integer> dizi_uid = new ArrayList();
    ArrayList<Drawable> dizi_icon = new ArrayList();
    ArrayList<Integer> dizi_color = new ArrayList();


    ArrayList<String> dizi_name_search = new ArrayList();
    ArrayList<Integer> dizi_uid_search = new ArrayList();
    ArrayList<Drawable> dizi_icon_search = new ArrayList();
    ArrayList<Integer> dizi_color_search = new ArrayList();

    PackageManager packageManager;

    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = getSharedPreferences("",0);
        return prefSettings;
    }

    public void Search(){
        //Uygulamalar arasında arama yapar

        final EditText search = (EditText) findViewById(R.id.editText);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(search.getText().toString().trim().equals("")){
                        search.setCursorVisible(false);

                        listadapter = new Adapter_Quota(getApplication(), dizi_name,dizi_uid,dizi_icon,dizi_color);
                        listView.setAdapter(listadapter);

                    }else {

                        dizi_name_search.clear();
                        dizi_uid_search.clear();
                        dizi_icon_search.clear();
                        dizi_color_search.clear();

                        for (int i=0; i<dizi_name.size(); i++){

                            if( dizi_name.get(i).toLowerCase().contains(search.getText().toString().toLowerCase())){
                                dizi_name_search.add(dizi_name.get(i));
                                dizi_uid_search.add(dizi_uid.get(i));
                                dizi_icon_search.add(dizi_icon.get(i));
                                dizi_color_search.add(dizi_color.get(i));
                            }
                        }

                        listadapter = new Adapter_Quota(getApplicationContext(), dizi_name_search,dizi_uid_search,dizi_icon_search,dizi_color_search);
                        listView.setAdapter(listadapter);


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option__fragment_apps);


        listView= (ListView) findViewById(R.id.listView);
        search= (EditText) findViewById(R.id.editText);
        textView_baslik= (TextView) findViewById(R.id.textView_message);
        textView_pb= (TextView) findViewById(R.id.textView2);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        constraintLayout_Shadow= (ConstraintLayout) findViewById(R.id.constraintLayout_Shadow);

        packageManager = getPackageManager();


        listadapter = new Adapter_Quota(getApplicationContext(), dizi_name,dizi_uid,dizi_icon,dizi_color);
        listView.setAdapter(listadapter);

        new LoadApplications().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    private List<ApplicationInfo> applist = null;

    private class LoadApplications extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {


                dizi_name.clear();
                dizi_uid.clear();
                dizi_icon.clear();
                dizi_color.clear();

                applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

                for (int i = 0; i<applist.size(); i++){

                    ApplicationInfo data = applist.get(i);


                    dizi_name.add(ControlStringLeng(String.valueOf(data.loadLabel(packageManager)),30));
                    dizi_uid.add(data.uid);
                    Drawable icon = data.loadIcon(packageManager);

                    dizi_icon.add(icon);
                    dizi_color.add(calculateAverageColor(((BitmapDrawable) icon).getBitmap(),5));



                }


            }catch (SecurityException ex){
                Toast.makeText(getApplicationContext(), "Lütfen Uygulamaya İzin Verin", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            constraintLayout_Shadow.setVisibility(View.GONE);

            listadapter.notifyDataSetChanged();

            Cursor();
            Search();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            textView_pb.setText("Bir Saniye...");
            super.onPreExecute();
        }
    }

    public void Cursor(){
        //Arama çubuğu imlecini belirler
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                search.setCursorVisible(false);
                CloseKeyBoard();
                return false;
            }
        });

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                search.setCursorVisible(true);
                return false;
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

    public String ControlStringLeng(String str, int lengt){

        if(str.length()>=lengt){
            str=str.substring(0,lengt);
            str=str+"..";
        }

        return str;
    }
}
