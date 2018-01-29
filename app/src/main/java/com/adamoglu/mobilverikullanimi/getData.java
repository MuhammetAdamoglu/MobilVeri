package com.adamoglu.mobilverikullanimi;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.ListView;

import com.adamoglu.mobilverikullanimi.Adapters.Adapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ABRA on 5.06.2017.
 */

public class getData {

    Adapter myadapter;


    ArrayList<String> array_download=new ArrayList<>();
    ArrayList<String> array_date = new ArrayList<>();
    NumberFormat numberformat = NumberFormat.getInstance();

    private double total=0;


    Time time = new Time();

    public double getTotal(Context context){
        total=0;
        database myDb = new database(context);
        final Calendar now = Calendar.getInstance();
        Cursor res = myDb.getAllData();
        if(res.getCount()!=0){

            while (res.moveToNext()){

                if(res.getString(2).trim().equals(String.valueOf(time.day[now.get(Calendar.DATE)-1]+"."+time.month[now.get(Calendar.MONTH)]+"."+now.get(Calendar.YEAR)))) {
                    break;
                }

                if (res.getString(1).trim().equals("B")){
                    total+=(res.getDouble(0)/1024)/1024;
                }
                else if (res.getString(1).trim().equals("KB")){
                    total+=res.getDouble(0)/1024;
                }
                else if (res.getString(1).trim().equals("GB")){
                    total+=res.getDouble(0)*1024;
                }
                else {
                    total+=res.getDouble(0);
                }

            }

        }


        return total;
    }
    public getData(){}

    Context context;
    public getData(Context context, ListView listView){
        database myDb = new database(context);
        this.context=context;

        myadapter = new Adapter(context,array_download,array_date);
        listView.setAdapter(myadapter);

        final Calendar now = Calendar.getInstance();

        numberformat.setMinimumFractionDigits(2);
        numberformat.setMaximumFractionDigits(2);

        Cursor res = myDb.getAllData();
        if(res.getCount()!=0){

            while (res.moveToNext()){

                    if(array_download.size()==0){
                        array_download.add(ControlStringLeng(String.valueOf(numberformat.format(res.getDouble(0)))+res.getString(1),15));
                        array_date.add(res.getString(2));
                    }
                    else {
                        array_download.add(0,ControlStringLeng(String.valueOf(numberformat.format(res.getDouble(0)))+res.getString(1),15));
                        array_date.add(0,res.getString(2));
                    }
                if(res.getString(2).trim().equals(String.valueOf(time.day[now.get(Calendar.DATE)-1]+"."+time.month[now.get(Calendar.MONTH)]+"."+now.get(Calendar.YEAR)))) {
                    break;
                }


                }

            try {
                array_date.set(0,"BUGÜN");
                array_date.set(1,"DÜN");
            }catch (Exception ex){}

            myadapter.notifyDataSetChanged();
        }

    }

    public String ControlStringLeng(String str, int lengt){

        if(str.length()>=lengt){
            str=str.substring(0,lengt);
            str=str+"..";
        }

        return str;
    }

    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = context.getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = context.getSharedPreferences("",0);
        return prefSettings;
    }



}
