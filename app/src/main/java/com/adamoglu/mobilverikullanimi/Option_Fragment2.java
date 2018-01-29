package com.adamoglu.mobilverikullanimi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamoglu.mobilverikullanimi.database;
import com.adamoglu.mobilverikullanimi.Tabs;
import com.adamoglu.mobilverikullanimi.R;
import com.adamoglu.mobilverikullanimi.Time;
import com.adamoglu.mobilverikullanimi.getSubscriberId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Option_Fragment2 extends Fragment{

    SwitchCompat aSwitch;
    TextView tv_ortak, tv_indirilen;
    ImageView iv;
    TextView tv_whatisthis,tv_start;

    Time time = new Time();

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {

        ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();

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

    private List<ApplicationInfo> applist = null;
    private PackageManager packageManager = null;

    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = this.getActivity().getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = this.getActivity().getSharedPreferences("",0);
        return prefSettings;
    }

    double Data=0;
    public void DoZeroApp(){

        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));

        for (int i=0; i<applist.size(); i++){
            ApplicationInfo data = applist.get(i);

            Data=getUsage(data.uid)/1048576;

            Editor().putString("Delta"+String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();
            Editor().putString("Delta2"+String.valueOf(data.loadLabel(packageManager)),String.valueOf(Data)).commit();
            Editor().putString(String.valueOf(data.loadLabel(packageManager)),"0").commit();
            Editor().putString("2"+String.valueOf(data.loadLabel(packageManager)),"0").commit();
        }
    }

    getSubscriberId id = new getSubscriberId();

    private double getUsage(int packageUid) {
        NetworkStats networkStatsByApp = null;
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getActivity().getSystemService(Context.NETWORK_STATS_SERVICE);



        long currentUsage = 0L;
        try {
            networkStatsByApp = networkStatsManager.querySummary(id.getType(), id.getSubscriberId(getContext(), ConnectivityManager.TYPE_MOBILE), 0, System.currentTimeMillis());

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


    public void Option(final SwitchCompat switchCompat, final TextView tv1, final TextView tv2){

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tv2.setTextColor(Color.parseColor("#BB41627E"));
                    tv1.setTextColor(Color.parseColor("#40606060"));
                    Editor().putBoolean("Setting_Indirmeveyükleme",true).commit();

                }else {
                    tv2.setTextColor(Color.parseColor("#40606060"));
                    tv1.setTextColor(Color.parseColor("#BB41627E"));
                    Editor().putBoolean("Setting_Yükleme",false).commit();
                }
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCompat.setChecked(false);
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCompat.setChecked(true);
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;
    int Fark_Tarih;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.option_fragment2, container, false);

        aSwitch= (SwitchCompat) view.findViewById(R.id.switch2);
        tv_indirilen= (TextView) view.findViewById(R.id.textView_indirme);
        tv_ortak= (TextView) view.findViewById(R.id.textView_indveyük);
        iv= (ImageView) view.findViewById(R.id.imageView);
        tv_whatisthis= (TextView) view.findViewById(R.id.textView_whatisthis);
        tv_start= (TextView) view.findViewById(R.id.textView_start);

        Fark_Tarih = SharedPreferences().getInt("COUNT_DAY",0);
        packageManager = getContext().getPackageManager();

        iv.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));

        tv_whatisthis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getLayoutInflater(getArguments());
                View dialoglayout = inflater.inflate(R.layout.whatisthis2, null);

                builder.setView(dialoglayout);

                TextView tv1 = (TextView) dialoglayout.findViewById(R.id.textView1);
                TextView tv2 = (TextView) dialoglayout.findViewById(R.id.textView2);
                TextView tv3 = (TextView) dialoglayout.findViewById(R.id.textView3);
                TextView tv4 = (TextView) dialoglayout.findViewById(R.id.textView4);
                TextView tv5 = (TextView) dialoglayout.findViewById(R.id.textView5);

                tv1.setText("Burda, Uygulamanın indirme ve yükleme kullanımının nasıl hesaplanması gerektiğini söylemeniz gerekiyor.");
                tv2.setText("Ortak Hesaplama seçeneğini seçerseniz:");
                tv3.setText("Bu seçenekte uygulamamız indirilen ve yüklenen veri kullanımını hesaplar, operatörler genellikle yükleme ve indirme kullanımınızı hesaplarlar.");
                tv4.setText("Sadece İndirilen seçeneğini seçerseniz:");
                tv5.setText("Bu seçenekte uygulamamız sadece indirilen veri kullanımını hesaplar, yüklenen veri kullanımını göz ardı eder.");

                builder.setCancelable(true);
                builder.show();
            }
        });

        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Load().execute();
                Editor().putLong("currentTimeMillis", System.currentTimeMillis()).commit();
            }
        });

        Option(aSwitch,tv_ortak,tv_indirilen);

        return view;
    }


    private class Load extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected Void doInBackground(Void... params) {

            Calendar now = Calendar.getInstance();

            database myDb = new database(getActivity());


            for(int i=0; i<Fark_Tarih; i++){
                now.add(Calendar.DATE,i);
                myDb.Add(0," MB",String.valueOf(time.day[now.get(Calendar.DATE)-1]+"."+time.month[now.get(Calendar.MONTH)]+"."+now.get(Calendar.YEAR)));
                now.add(Calendar.DATE,i*-1);
            }
            DoZeroApp();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Editor().putBoolean("Control_StartApp",true).commit();
            Editor().putBoolean("Stop_App",false).commit();

            Intent i = new Intent(getActivity(),Tabs.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            this.dialog.setMessage("Uygulama Açılıyor..");
            this.dialog.show();

            super.onPreExecute();
        }
    }

}

