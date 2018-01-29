package com.adamoglu.mobilverikullanimi.Adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.R;
import com.adamoglu.mobilverikullanimi.Services.ServiceApps_Test;
import com.adamoglu.mobilverikullanimi.Services.Service_Data;
import com.adamoglu.mobilverikullanimi.database;

import java.text.NumberFormat;
import java.util.ArrayList;

public class Adapter_OneApp extends ArrayAdapter<String> {
    private final Context context;

    private final ArrayList<String> values_name;
    private final ArrayList<Integer> values_uid;
    private final ArrayList<Drawable> values_icon;
    private final ArrayList<Integer> values_color;


    String styledText_first,styledText_second;


    private SparseBooleanArray mSelectedItemsIds;

    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = getContext().getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = getContext().getSharedPreferences("",0);
        return prefSettings;
    }

    public Adapter_OneApp(Context context, ArrayList values_name, ArrayList values_uid, ArrayList values_icon, ArrayList color) {
        super(context, R.layout.listview_selectapp, values_name);
        this.context = context;
        this.values_name = values_name;
        this.values_uid = values_uid;
        this.values_icon = values_icon;
        values_color=color;
        mSelectedItemsIds = new SparseBooleanArray();

    }
    NumberFormat numberformat = NumberFormat.getInstance();

    ArrayList<CheckBox> array_checkbox = new ArrayList<>();

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;


        rowView = inflater.inflate(R.layout.listview_selectapp, parent, false);

        numberformat.setMinimumFractionDigits(1);
        numberformat.setMaximumFractionDigits(1);

        TextView appName = (TextView) rowView.findViewById(R.id.app_name);
        ImageView iconView = (ImageView) rowView.findViewById(R.id.imageView);
        LinearLayout linearlayout = (LinearLayout) rowView.findViewById(R.id.linearLayout);

        final CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.checkBox);


        appName.setText(values_name.get(position));
        appName.setTextColor(values_color.get(position));
        iconView.setImageDrawable(values_icon.get(position));


        if(values_uid.get(position)!=0)
            if(values_uid.get(position)==SharedPreferences().getInt("ShowOneApp",0)){
                checkbox.setChecked(true);
                array_checkbox.add(checkbox);
            }

        linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkbox.isChecked()){
                    checkbox.setChecked(false);
                }else {
                    checkbox.setChecked(true);
                }

            }
        });

        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkbox.isChecked()){
                    checkbox.setChecked(false);
                }else {
                    checkbox.setChecked(true);
                }
            }
        });



        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(checkbox.isChecked()){

                    for(int i=0; i<array_checkbox.size(); i++){
                        if(checkbox!=array_checkbox.get(i))
                            if(array_checkbox.get(i).isChecked()){
                                array_checkbox.get(i).setChecked(false);
                            }
                    }

                    array_checkbox.add(checkbox);

                    Editor().putInt("ShowOneApp",values_uid.get(position)).commit();

                    if(!SharedPreferences().getBoolean("Adapter_OneApp_checked",false)){

                        Editor().putBoolean("Adapter_OneApp_checked",true).commit();

                        new ServiceApps_Test(context);
                        ServiceApps_Test.FinishOneApp();
                        ServiceApps_Test.StartServieApps_Test();
                    }

                }else{

                    Editor().putBoolean("Adapter_OneApp_checked",false).commit();

                    Editor().putInt("ShowOneApp",0).commit();

                    ServiceApps_Test.StopServieApps_Test();
                }


            }
        });

        return rowView;
    }

    public void toggleSelection(int position) {


        selectView(position, !mSelectedItemsIds.get(position));
    }

    public Spannable TextFont(){
        Spannable wordtoSpan = null;
        try {

            wordtoSpan = new SpannableString(styledText_first+styledText_second);
            wordtoSpan.setSpan(new RelativeSizeSpan(0.90f), 0, styledText_first.length(),0);
            wordtoSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), styledText_first.length(),wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }catch (Exception ex){
            Toast.makeText(getContext(), "TextFont()'da Hata", Toast.LENGTH_SHORT).show();
        }
        return wordtoSpan;
    }

    public void selectView(int position, boolean value) {

        try {
            if (value)
                mSelectedItemsIds.put(position, value);
            else
                mSelectedItemsIds.delete(position);
            notifyDataSetChanged();
        }catch (Exception ex){
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

}