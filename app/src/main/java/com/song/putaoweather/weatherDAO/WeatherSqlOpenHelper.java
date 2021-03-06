package com.song.putaoweather.weatherDAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SongPc on 2016/1/8.
 */
public class WeatherSqlOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_WEATHER_DATABASE = "create table location("
            + "id integer primary key autoincrement,"
            + "countyId text,"
            + "county text,"
            + "cityId text,"
            + "city text,"
            + "provinceId text,"
            + "province text)";

    private static final String CREATE_WEATHER_TYPE = "create table weatherType(" +
            "code integer primary key," +
            "cn_name text," +
            "en_name text," +
            "filepath text)";

    public WeatherSqlOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEATHER_DATABASE);
        db.execSQL(CREATE_WEATHER_TYPE);
    }
}
