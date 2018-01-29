package com.adamoglu.mobilverikullanimi;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adamoglu.mobilverikullanimi.R;
import com.adamoglu.mobilverikullanimi.Tabs;
import com.adamoglu.mobilverikullanimi.database;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Add_Date extends Fragment {

    RadioButton rb_aylik,rb_yillik,rb_haftalik,rb_ozel;
    RadioGroup Radiogroup;
    TextView Textview_Tarih_Mesaj;
    ImageView iv;
    ConstraintLayout cl;

    final Calendar now = Calendar.getInstance();



    int kontrol_radiobutton=0;
    int yıl;
    int ay;
    int gun;

    int kayitay;
    int kayityıl;
    int kayitgun;

    long Fark_Tarih;

    public void idler(View view){

        rb_aylik= (RadioButton) view.findViewById(R.id.rb_aylik);
        rb_haftalik= (RadioButton) view.findViewById(R.id.rb_haftalik);
        rb_yillik= (RadioButton) view.findViewById(R.id.rb_yillik);
        rb_ozel= (RadioButton) view.findViewById(R.id.ozel);
        Radiogroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        Textview_Tarih_Mesaj = (TextView) view.findViewById(R.id.Textview_tarih_mesaj);
        cl = (ConstraintLayout) view.findViewById(R.id.Layout);
        iv= (ImageView) view.findViewById(R.id.imageView);

        iv.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));

    }




    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.add_date,container,false);

        final SharedPreferences prefSettings =  getContext().getSharedPreferences("", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefSettings.edit();

        idler(view);

        database myDb = new database(getContext());
        myDb.DeleteAllData();


        if(!prefSettings.getString("mobil_data","").trim().equals("")){

            Intent intent = new Intent(getContext(), Tabs.class);
            startActivity(intent);
        }

        editor.putBoolean("exit",false).commit();

        Radiogroup.setVisibility(Radiogroup.VISIBLE);

        ay = now.get(Calendar.MONTH);
        yıl = now.get(Calendar.YEAR);
        gun = now.get(Calendar.DATE);

        kayitay = now.get(Calendar.MONTH);
        kayityıl = now.get(Calendar.YEAR);
        kayitgun = now.get(Calendar.DATE);


        rb_ozel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatePickerDialog datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int yıl1, int ay1, int gun1) {

                        kayitay = ay1;
                        kayityıl = yıl1;
                        kayitgun = gun1;

                        Date IlkGun = new GregorianCalendar(yıl, ay, gun, 00, 00).getTime();
                        Date SonGun = new GregorianCalendar(kayityıl, kayitay, kayitgun, 00, 00).getTime();

                        Fark_Tarih = SonGun.getTime() - IlkGun.getTime();
                        Fark_Tarih = Fark_Tarih / (1000 * 60 * 60 * 24);

                        if (Fark_Tarih < 0) {
                            Textview_Tarih_Mesaj.setText("İleri Bir Tarih Seçiniz");
                        } else if (Fark_Tarih == 0) {
                            Textview_Tarih_Mesaj.setText("");
                        } else {
                            Textview_Tarih_Mesaj.setText(String.valueOf(Fark_Tarih) + " Gün");
                        }

                        editor.putLong("Fark_Tarih",Fark_Tarih).commit();

                    }
                }, kayityıl, kayitay, kayitgun);//başlarken set edilcek değerlerimizi atıyoruz
                datePicker.setTitle("Tarih Seç");
                datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE,"Tarih Ayarla", datePicker);

                datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        rb_haftalik.setChecked(false);

                    }
                });
                datePicker.setCancelable(false);
                datePicker.show();

                kontrol_radiobutton = 1;

                editor.putInt("kontrol_radiobutton",kontrol_radiobutton).commit();


            }
        });

        Radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                int rgid = Radiogroup.getCheckedRadioButtonId();

                Fark_Tarih=prefSettings.getLong("Fark_Tarih",0);

                if (rgid == R.id.rb_yillik) {

                    Date IlkGun = new GregorianCalendar(yıl, ay, gun, 00, 00).getTime();
                    Date SonGun = new GregorianCalendar(yıl + 1, ay, gun, 00, 00).getTime();

                    Fark_Tarih = SonGun.getTime() - IlkGun.getTime();
                    Fark_Tarih = Fark_Tarih / (1000 * 60 * 60 * 24);

                    kontrol_radiobutton = 1;

                    editor.putInt("kontrol_radiobutton",kontrol_radiobutton).commit();
                    editor.putLong("Fark_Tarih",Fark_Tarih).commit();


                } else if (rgid == R.id.rb_aylik) {

                    Date IlkGun = new GregorianCalendar(yıl, ay, gun, 00, 00).getTime();
                    Date SonGun = new GregorianCalendar(yıl, ay + 1, gun, 00, 00).getTime();

                    Fark_Tarih = SonGun.getTime() - IlkGun.getTime();
                    Fark_Tarih = Fark_Tarih / (1000 * 60 * 60 * 24);
                    kontrol_radiobutton= 1;

                    editor.putInt("kontrol_radiobutton",kontrol_radiobutton).commit();
                    editor.putLong("Fark_Tarih",Fark_Tarih).commit();


                } else if (rgid == R.id.rb_haftalik) {

                    Date IlkGun = new GregorianCalendar(yıl, ay, gun, 00, 00).getTime();
                    Date SonGun = new GregorianCalendar(yıl, ay, gun + 7, 00, 00).getTime();

                    Fark_Tarih = SonGun.getTime() - IlkGun.getTime();
                    Fark_Tarih = Fark_Tarih / (1000 * 60 * 60 * 24);
                    kontrol_radiobutton = 1;

                    editor.putInt("kontrol_radiobutton",kontrol_radiobutton).commit();
                    editor.putLong("Fark_Tarih",Fark_Tarih).commit();

                }

                if (Fark_Tarih < 0) {
                    Textview_Tarih_Mesaj.setText("İleri Bir Tarih Seçiniz");
                } else if (Fark_Tarih == 0) {
                    Textview_Tarih_Mesaj.setText("");
                } else {
                    Textview_Tarih_Mesaj.setText(String.valueOf(Fark_Tarih) + " Gün");
                }
            }
        });




        return view;
    }

}

