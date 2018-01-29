package com.adamoglu.mobilverikullanimi;

import android.content.Context;
import android.database.Cursor;
import android.widget.ListView;

import com.adamoglu.mobilverikullanimi.Adapters.Adapter;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by ABRA on 5.06.2017.
 */

public class getData_SpeedTest {

    Adapter myadapter;


    ArrayList<String> array_mbps=new ArrayList<>();
    ArrayList<String> array_name = new ArrayList<>();
    NumberFormat numberformat = NumberFormat.getInstance();

    private double total=0;

    public boolean Control_Null(Context context){
        database myDb = new database(context);
        Cursor res = myDb.getAllData_SpeedTest();
        if(res.getCount()!=0){
            return false;
        }else {
            return true;
        }
    }

    public getData_SpeedTest(){}

    public getData_SpeedTest(Context context, ListView listView){
        database myDb = new database(context);

        myadapter = new Adapter(context,array_mbps, array_name);
        listView.setAdapter(myadapter);

        numberformat.setMinimumFractionDigits(1);
        numberformat.setMaximumFractionDigits(1);

        Cursor res = myDb.getAllData_SpeedTest();
        if(res.getCount()!=0){

            while (res.moveToNext()){

                    if(array_mbps.size()==0){
                        array_name.add(ControlStringLeng(res.getString(0),15));
                        array_mbps.add(res.getString(1)+" Mbps");
                    }
                    else {
                        array_name.add(0,ControlStringLeng(res.getString(0),15));
                        array_mbps.add(0,res.getString(1)+" Mbps");
                    }
                }
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
}
