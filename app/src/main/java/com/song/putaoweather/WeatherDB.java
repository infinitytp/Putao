package com.song.putaoweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * Created by SongPc on 2016/1/8.
 */
public class WeatherDB {

    public static final String DB_NAME = "Weather";

    public static final int VERSION = 1;
    private Context myContext;
    private SQLiteDatabase sqlDB;
    public static WeatherDB weatherDB;
    private List<LocationId> list;

    public WeatherDB(Context context){
        WeatherSqlOpenHelper sqlOpenHelper = new WeatherSqlOpenHelper(context,DB_NAME,null,VERSION);
        sqlDB = sqlOpenHelper.getWritableDatabase();
        myContext = context;
    }

    public synchronized static WeatherDB getInstance(Context context){
        if (weatherDB == null){
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    public int getCount(){
        Cursor cursor = sqlDB.query("location",null,null,null,null,null,null);
        return cursor.getCount();
    }

    public void init(){
        if (getCount()>0){
            return;
        } else {
            list = new LocationList(myContext).getLocationList();
            Log.d("Tag","数据库初始化");
            Iterator<LocationId> iterator = list.iterator();
            sqlDB.beginTransaction();
            while (iterator.hasNext()){
                LocationId id = iterator.next();
                ContentValues values = new ContentValues();
                values.put("countyId",id.getCountyId());
                values.put("county",id.getCounty());
                values.put("cityId",id.getCityId());
                values.put("city",id.getCity());
                values.put("provinceId",id.getProvinceId());
                values.put("province",id.getProvince());
                sqlDB.insert("location",null,values);
            }
            sqlDB.setTransactionSuccessful();
            if (sqlDB!=null){
                sqlDB.endTransaction();
            }
            Log.d("Tag","数据库初始化完毕");
        }
    }
}
