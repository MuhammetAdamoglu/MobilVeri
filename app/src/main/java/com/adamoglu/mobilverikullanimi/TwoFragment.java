package com.adamoglu.mobilverikullanimi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;


public class TwoFragment extends Fragment implements ViewSwitcher.ViewFactory {


    public TwoFragment() {
        // Required empty public constructor
    }

    public static void onBack(){
        ((Tabs)context).setCurrentItem(1,true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_two, container, false);

        context=getContext();

        return view;
    }

    @Override
    public View makeView() {
        TextView t = new TextView(getActivity());
        t.setGravity(Gravity.CENTER);
        t.setTextColor(Color.parseColor("#659EC7"));
        t.setTypeface(null, Typeface.BOLD);
        t.setTextSize(14);
        return t;
    }
}
