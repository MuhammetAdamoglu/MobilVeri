package com.adamoglu.mobilverikullanimi;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamoglu.mobilverikullanimi.R;


public class Option_Fragment extends Fragment{

    SwitchCompat aSwitch1;
    ImageView iv;
    TextView tv_tekrarli,tv_birdefalik;
    TextView tv_whatisthis;

    public SharedPreferences.Editor Editor() {
        final SharedPreferences prefSettings = this.getActivity().getSharedPreferences("",0);
        final SharedPreferences.Editor editor = prefSettings.edit();
        return editor;
    }


    public void Option(final SwitchCompat switchCompat, final TextView tv1, final TextView tv2){

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tv2.setTextColor(Color.parseColor("#BB41627E"));
                    tv1.setTextColor(Color.parseColor("#40606060"));
                    Editor().putBoolean("Setting_Hicbirseyyapma",true).commit();
                }else {
                    tv2.setTextColor(Color.parseColor("#40606060"));
                    tv1.setTextColor(Color.parseColor("#BB41627E"));
                    Editor().putBoolean("Setting_Hicbirseyyapma",false).commit();
                }

            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCompat.setChecked(false);
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCompat.setChecked(true);
            }
        });

    }

    public SharedPreferences SharedPreferences() {
        final SharedPreferences prefSettings = this.getActivity().getSharedPreferences("",0);
        return prefSettings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View view;
    View view2;
    int Date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.option_fragment, container, false);
        view2= inflater.inflate(R.layout.option_fragment,container,false);

        aSwitch1= (SwitchCompat) view.findViewById(R.id.switch_sureklikullanim);
        tv_birdefalik= (TextView) view.findViewById(R.id.textView_birdefalik);
        tv_tekrarli= (TextView) view.findViewById(R.id.textView_tekrarli);
        tv_whatisthis= (TextView) view.findViewById(R.id.textView_whatisthis);
        iv= (ImageView) view.findViewById(R.id.imageView);

        iv.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));


        tv_whatisthis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getLayoutInflater(getArguments());
                View dialoglayout = inflater.inflate(R.layout.whatisthis1, null);

                builder.setView(dialoglayout);

                TextView tv1 = (TextView) dialoglayout.findViewById(R.id.textView1);
                TextView tv2 = (TextView) dialoglayout.findViewById(R.id.textView2);
                TextView tv3 = (TextView) dialoglayout.findViewById(R.id.textView3);
                TextView tv4 = (TextView) dialoglayout.findViewById(R.id.textView4);
                TextView tv5 = (TextView) dialoglayout.findViewById(R.id.textView5);

                tv1.setText("Burda, belirlediğiniz süre sonunda ne olacağını uygulamamıza belirtmeniz gerekiyor.");
                tv2.setText("Tekrarlansın seçeneğini seçerseniz:");
                tv3.setText("Belirlediğiniz süre sonunda uygulama veri kullanımınızı sıfırlayıp tekrardan belirlediğiniz kadar süreniz olacaktır.\n" +
                        "Siz yetkinizi değiştirene kadar uygulamamız bu şekilde devam edecektir.");
                tv4.setText("Hiç Birşey Yapma seçeneğini seçerseniz:");
                tv5.setText("Belirlediğiniz süre sonunda uygulama hiç bişey yapmayıp ölçüm yapmaya devam eder."+
                        "Uygulamaya tekrar veri miktarı ve gün vererek uygulamayı başlatabilirsiniz.");

                builder.setCancelable(true);
                builder.show();
            }
        });

        Option(aSwitch1,tv_tekrarli,tv_birdefalik);

        return view;
    }
}

