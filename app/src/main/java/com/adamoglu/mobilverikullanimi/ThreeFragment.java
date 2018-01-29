package com.adamoglu.mobilverikullanimi;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.Adapters.Adapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.NumberFormat;
import java.util.ArrayList;


public class ThreeFragment extends Fragment{


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

    public ThreeFragment() {
        // Required empty public constructor
    }


    public SharedPreferences.Editor Editor(){
        final SharedPreferences prefSettings =  getContext().getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }
    public SharedPreferences SharedPreferences(){
        final SharedPreferences prefSettings =  getContext().getSharedPreferences("", Context.MODE_PRIVATE);
        return prefSettings;
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

    public static String GetWifiMacAdress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getBSSID();
        return macAddress;
    }

    static int Rssi;

    public static String RSSI( WifiInfo wInfo){
        Rssi=wInfo.getRssi();
        Rssi=Rssi*-1;
        Rssi=100-Rssi;
        if(Rssi<=0){
            Rssi=0;
        }
        return Rssi+"%";
    }

    public static String GetWifiFeatures(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();

        String macAddress = Formatter.formatIpAddress(wInfo.getIpAddress())+"\n"+wInfo.getFrequency()+"\n"+wInfo.getLinkSpeed()+" Mbps\n"+
                wInfo.getNetworkId()+"\n"+RSSI(wInfo);
        return macAddress;
    }


    ArrayList<String> Name = new ArrayList<>();
    ArrayList<String> Data = new ArrayList<>();


    boolean Scrool=true;
    boolean ScroolFirst =false;
    String WifiName;

    public static void CloseUpSliding(){

        if(UpPanel.getPanelState()!=SlidingUpPanelLayout.PanelState.COLLAPSED){
            UpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }else {
            ((Tabs) TwoFragment.context).setCurrentItem(0,true);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    NumberFormat numberformat = NumberFormat.getInstance();
    View view;
    TextView textView_wifi,textView_wifi_type,textView_WifiName,textView_WifiFrequency;
    ListView listView;
    static SlidingUpPanelLayout UpPanel;

    Adapter myadapter;

    static Handler handler;

    public void StopThreeFragment(){
        if(handler!=null)
            handler.removeMessages(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(handler!=null)
            handler.removeMessages(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(handler!=null)
            handler.removeMessages(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_three, container, false);


        textView_wifi= (TextView) view.findViewById(R.id.textView_Wifi);
        textView_wifi_type= (TextView) view.findViewById(R.id.textView_WifiType);
        textView_WifiName= (TextView) view.findViewById(R.id.textView_WifiName);
        textView_WifiFrequency= (TextView) view.findViewById(R.id.textView_WifiFrequency);
        listView = (ListView) view.findViewById(R.id.ListView);
        UpPanel = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);

        numberformat.setMinimumFractionDigits(1);
        numberformat.setMaximumFractionDigits(1);

        ListViewScrool();

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return Scrool;
            }
        });

        new getData(getActivity(), listView);

        myadapter = new Adapter(getContext(),Name,Data);
        listView.setAdapter(myadapter);

        handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                textView_wifi.setText(numberformat.format(Double.parseDouble(SharedPreferences().getString("USE_DATA_WIFI","0"))));
                textView_wifi_type.setText(SharedPreferences().getString("USE_DATA_WIFI_TYPE"," MB"));

                WifiName=getWifiName(getContext());
                if(WifiName!=null){
                    WifiName=WifiName.substring(1,WifiName.length()-1)+"\n"+GetWifiMacAdress(getContext())+"\n"+GetWifiFeatures(getContext());
                    textView_WifiName.setText(WifiName);
                    textView_WifiFrequency.setText("Ağ İsmi \nMac Adress \nIP Adress \nSıklık \nLink Hızı  \nAğ ID'si \nSinyal Gücü ");
                }else {
                    textView_WifiName.setText("Şuan Bağlı Değil");
                    textView_WifiFrequency.setText("");
                }

                new Load().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                handler.postDelayed(this,2000);
            }
        },0);



        UpPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
           @Override
           public void onPanelSlide(View panel, float slideOffset) {
               if(slideOffset!=1){
                   Scrool=true;
                   ScroolFirst =false;
                   listView.smoothScrollToPosition(0);

                   listView.setOnTouchListener(new View.OnTouchListener() {
                       @Override
                       public boolean onTouch(View v, MotionEvent event) {
                           return Scrool;
                       }
                   });
               }else {
                   Scrool=false;
                   ListViewScrool();
               }
           }

           @Override
           public void onPanelCollapsed(View panel) {

           }

           @Override
           public void onPanelExpanded(View panel) {

           }

           @Override
           public void onPanelAnchored(View panel) {

           }

           @Override
           public void onPanelHidden(View panel) {

           }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem==0){
                    if(ScroolFirst)
                    UpPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                }else {
                    ScroolFirst=true;
                }
            }
        });

        return view;
    }

    public String ControlStringLeng(String str, int lengt){

        if(str.length()>=lengt){
            str=str.substring(0,lengt);
            str=str+"..";
        }

        return str;
    }

    private class Load extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            database myDb = new database(getActivity());

            Name.clear();
            Data.clear();

            Cursor res = myDb.getAllData_Wifi();
            if(res.getCount()!=0){
                while(res.moveToNext()){
                    Name.add(ControlStringLeng(res.getString(0),10));
                    Data.add(numberformat.format(Unit_Converter(res.getDouble(1)))+Data_Str);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            myadapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }
    }

    String Data_Str;
    String Data_Str_Second;
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

}

