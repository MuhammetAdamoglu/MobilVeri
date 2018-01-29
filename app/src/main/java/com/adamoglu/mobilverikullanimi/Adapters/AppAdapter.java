package com.adamoglu.mobilverikullanimi.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.R;

import java.text.NumberFormat;
import java.util.ArrayList;

public class AppAdapter extends ArrayAdapter<String> {
    private final Context context;

    private final ArrayList<String> values_name;
    private final ArrayList<Double> values_data;
    private final ArrayList<Drawable> values_icon;
    private final ArrayList<Integer> values_color;
    private final ArrayList<String> values_speed;

    String styledText_first,styledText_second;


    private SparseBooleanArray mSelectedItemsIds;

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = context.getSharedPreferences("",0);
        return prefSettings;
    }

    public AppAdapter(Context context, ArrayList values_name, ArrayList values_data, ArrayList values_icon, ArrayList color,ArrayList speed) {
        super(context, R.layout.list_item, values_name);
        this.context = context;
        this.values_name = values_name;
        this.values_data = values_data;
        this.values_icon = values_icon;
        values_color=color;
        values_speed=speed;
        mSelectedItemsIds = new SparseBooleanArray();

    }
    NumberFormat numberformat = NumberFormat.getInstance();
    double percentiles;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;


        rowView = inflater.inflate(R.layout.list_item, parent, false);

        numberformat.setMinimumFractionDigits(1);
        numberformat.setMaximumFractionDigits(1);

        TextView appName = (TextView) rowView.findViewById(R.id.app_name);
        TextView percentiles = (TextView) rowView.findViewById(R.id.textView_message);
        ProgressBar progressBar = (ProgressBar) rowView.findViewById(R.id.progressBar);
        ImageView iconView = (ImageView) rowView.findViewById(R.id.app_icon);

        this.percentiles=values_data.get(position)*100/(Double.parseDouble(SharedPreferences().getString("USE_DATA_MB","0"))+Double.parseDouble(SharedPreferences().getString("New_ExitQuotaApp","0")));

        appName.setText(values_name.get(position));
        appName.setTextColor(values_color.get(position));

        String a = "\n(Kotadan Harcamayan Uygulama)";
        String b="";
        try {
             b = values_name.get(position).substring(values_name.get(position).length()-a.length(),values_name.get(position).length());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(b.equals(a)){
            progressBar.setVisibility(View.GONE);
            percentiles.setVisibility(View.GONE);
        }else {
            percentiles.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(values_color.get(position)));
            progressBar.setProgressTintList(ColorStateList.valueOf(values_color.get(position)));
            progressBar.setProgress((int) this.percentiles );
        }


        if(this.percentiles<0.1){

            styledText_first =values_speed.get(position);
            styledText_second ="  <0.1 %";

            percentiles.setText(TextFont());

        }else {

            if(this.percentiles>=100){
                this.percentiles=99;
            }

            styledText_first =values_speed.get(position)+"  ";
            styledText_second =numberformat.format(this.percentiles)+" %";

            percentiles.setText(TextFont());
        }
        iconView.setImageDrawable(values_icon.get(position));


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