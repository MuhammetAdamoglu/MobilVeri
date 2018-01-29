package com.adamoglu.mobilverikullanimi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

public class getSubscriberId {

    public String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        }
        return "";
    }

    public int getType(){
        return ConnectivityManager.TYPE_MOBILE;
    }
}
