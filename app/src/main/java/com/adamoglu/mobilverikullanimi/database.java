package com.adamoglu.mobilverikullanimi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class database extends SQLiteOpenHelper {

    private static final String VERITABANI = "Data_Traffic";
    private static final String TABLE = "traffic";
    private static final String TABLE_2 = "speedtest";
    private static final String TABLE_3 = "wifis";
    private static final String TABLE_4 = "quota";


    private static String Data_Download = "download";
    private static String Type = "type";
    private static String Date = "date";
    private static String ID = "ID";

    private static String WifiName = "wifiname";
    private static String WifiMac = "wifimac";
    private static String WifiData = "wifidata";

    private static String Mbps = "mbps";

    private static String QuotaName = "quotaname";



    public database(Context context) {
        super(context, VERITABANI, null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE + " ( "

                        + Data_Download + " TEXT, "
                        + Type + " TEXT, "
                        + Date + " TEXT, "
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
                        + " )"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_2 + " ( "

                        + WifiName + " TEXT, "
                        + Mbps + " TEXT, "
                        + WifiMac + " TEXT, "
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
                        + " )"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_3 + " ( "

                        + WifiName + " TEXT, "
                        + WifiData + " TEXT, "
                        + WifiMac + " TEXT, "
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
                        + " )"
        );
        db.execSQL(
                "CREATE TABLE " + TABLE_4 + " ( "

                        + QuotaName + " TEXT, "
                        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
                        + " )"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXITS" + TABLE);
        db.execSQL("DROP TABLE IF EXITS" + TABLE_2);
        db.execSQL("DROP TABLE IF EXITS" + TABLE_3);
        db.execSQL("DROP TABLE IF EXITS" + TABLE_4);
        onCreate(db);
    }

    public boolean Add(double download, String type, String date) {


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues veriler = new ContentValues();

        veriler.put(Data_Download, download);
        veriler.put(Type, type);
        veriler.put(Date, date);


        long result = db.insert(TABLE, null, veriler);
        if (result == -1)
            return false;
        else
            return true;
    }

    public void Add_SpeedTest(String  wifiName, String mbps, String wifiMac){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues veriler = new ContentValues();

        veriler.put(WifiName, wifiName);
        veriler.put(Mbps, mbps);
        veriler.put(WifiMac, wifiMac);

        db.insert(TABLE_2, null, veriler);
    }

    public void Add_Wifi(String  wifiName, double wifiData, String wifiMac){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues veriler = new ContentValues();

        veriler.put(WifiName, wifiName);
        veriler.put(WifiData, wifiData);
        veriler.put(WifiMac, wifiMac);

        db.insert(TABLE_3, null, veriler);
    }

    public void Add_Quota(int  QuotaName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues veriler = new ContentValues();

        veriler.put(this.QuotaName, QuotaName);

        db.insert(TABLE_4, null, veriler);
    }

    public Cursor getAllData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE, null);

        return result;
    }

    public Cursor getAllData_SpeedTest() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_2, null);

        return result;
    }
    public Cursor getAllData_Wifi() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_3, null);

        return result;
    }

    public Cursor getAllData_Quota() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_4, null);

        return result;
    }

    public Integer DeleteData_Quota( String id) {

        //Günlük ve kalıcı verileri silmek için

        SQLiteDatabase db = this.getWritableDatabase();


        return  db.delete(TABLE_4, "quotaname = ?", new String[]{id});
    }



    public void UpdateData(double data, String type, String id) {

        //Değişen açıklamayı güncellemek için
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Data_Download, data);
        contentValues.put(Type, type);

        db.update(TABLE, contentValues, "date = ?", new String[]{id});

    }

    public long UpdateData_SpeedTest(String mpbs,String Name, String Mac) {

        //Değişen açıklamayı güncellemek için
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Mbps, mpbs);
        contentValues.put(WifiName, Name);

        long id =db.update(TABLE_2, contentValues, "wifimac = ?", new String[]{Mac});
        return id;
    }

    public long UpdateData_Wifi(Double data,String Name, String Mac) {

        //Değişen açıklamayı güncellemek için
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(WifiData, data);
        contentValues.put(WifiName, Name);

        long id =db.update(TABLE_3, contentValues, "wifimac = ?", new String[]{Mac});
        return id;
    }

    public void DeleteAllData(){
        //
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE,null,null);
        db.delete(TABLE_2,null,null);
        db.delete(TABLE_3,null,null);
        db.delete(TABLE_4,null,null);
    }

    public void DeleteAllData_Traffic(){
        //
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE,null,null);
    }

    public void DeleteAllData_Quota(){
        //
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_4,null,null);
    }

}