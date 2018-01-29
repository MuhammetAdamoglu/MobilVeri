package com.adamoglu.mobilverikullanimi.Adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.adamoglu.mobilverikullanimi.R;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList values_download;
    private final ArrayList values_date;


    private SparseBooleanArray mSelectedItemsIds;

    public Adapter(Context context, ArrayList values_download, ArrayList values_date) {
        super(context, R.layout.listview, values_download);
        this.context = context;
        this.values_download = values_download;
        this.values_date = values_date;
        mSelectedItemsIds = new SparseBooleanArray();

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        rowView = inflater.inflate(R.layout.listview, parent, false);

        TextView textView_mobil = (TextView) rowView.findViewById(R.id.mobil);
        TextView textView_date = (TextView) rowView.findViewById(R.id.date);

        textView_mobil.setText(String.valueOf(values_download.get(position).toString()));
        textView_date.setText(values_date.get(position).toString());


        return rowView;
    }

    public void toggleSelection(int position) {


        selectView(position, !mSelectedItemsIds.get(position));
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