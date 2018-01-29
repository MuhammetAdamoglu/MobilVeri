package com.adamoglu.mobilverikullanimi;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamoglu.mobilverikullanimi.R;

public class Add_Data extends Fragment {

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    EditText Edittext_Data;
    TextView Textview_DataType;
    ConstraintLayout cl;
    ImageView iv;

    int sayac=0;

    boolean control_DataType=true;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.add__data,container,false);

        final SharedPreferences prefSettings =  getContext().getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();




        Edittext_Data = (EditText) view.findViewById(R.id.Edittext_EnterData);
        Textview_DataType = (TextView) view.findViewById(R.id.textView_dataType);
        cl= (ConstraintLayout) view.findViewById(R.id.Layout);
        iv= (ImageView) view.findViewById(R.id.imageView);

        iv.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));

        cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CloseKeyBoard();
                Edittext_Data.setCursorVisible(false);
                return false;
            }
        });

        Edittext_Data.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Edittext_Data.setCursorVisible(true);
                return false;
            }
        });
        editor.putBoolean("control_DataType",true).commit();
        Textview_DataType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sayac++;
                if(sayac%2==1){
                    Textview_DataType.setText("GB");
                    control_DataType=false;
                    editor.putBoolean("control_DataType",control_DataType).commit();
                }
                else {
                    Textview_DataType.setText("MB");
                    control_DataType=true;
                    editor.putBoolean("control_DataType",control_DataType).commit();
                }
            }
        });

        Edittext_Data.addTextChangedListener(wacher);

        return view;
    }

    public void CloseKeyBoard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    TextWatcher wacher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final SharedPreferences prefSettings =  getContext().getSharedPreferences("", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = prefSettings.edit();
            editor.putString("Edittext_Data",Edittext_Data.getText().toString()).commit();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
