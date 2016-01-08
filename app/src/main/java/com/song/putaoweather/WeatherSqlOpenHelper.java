package com.song.putaoweather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SongPc on 2016/1/8.
 */
public class WeatherSqlOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_WEATHERDATABASE = "create table location("
            + "id integer primary key autoincrement,"
            + "countyId text,"
            + "county text,"
            + "cityId text,"
            + "city text,"
            + "provinceId text,"
            + "province text)";

    public WeatherSqlOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEATHERDATABASE);
    }
}
