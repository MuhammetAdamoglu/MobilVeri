package com.adamoglu.mobilverikullanimi;

import android.content.Context;
import android.database.Cursor;

import java.util.HashMap;

/**
 * Created by ABRA on 2.09.2017.
 */

public class getWifiData {

    HashMap<String,Double> data = new HashMap<>();
    Context context;
    public getWifiData(Context context){
        this.context=context;
        database myDb = new database(context);

        data.clear();

        Cursor res = myDb.getAllData_Wifi();
        if(res.getCount()!=0){
            while (res.moveToNext()){
               data.put(ControlStringLeng(res.getString(2),15),res.getDouble(1));
            }
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
