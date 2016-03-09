package com.song.putaoweather.weatherDAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.song.putaoweather.model.LocationList;
import com.song.putaoweather.model.LocationName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SongPc on 2016/1/8.
 */
public class WeatherDB implements WeatherDBInterface{

    public static final String DB_NAME = "Weather";

    public static final int VERSION = 1;
    private Context myContext;
    private SQLiteDatabase sqlDB;
    public static WeatherDB weatherDB;
    private List<LocationName> list;

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
            Iterator<LocationName> iterator = list.iterator();
            sqlDB.beginTransaction();
            while (iterator.hasNext()){
                LocationName id = iterator.next();
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

    public List<String> getProvinces(){
        List<String> list = new ArrayList<>();
        String sql = "select distinct province from location";
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst()){
            do {
                String province = cursor.getString(cursor.getColumnIndex("province"));
                list.add(province);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getCity(String province){
        List<String> list = new ArrayList<>();
        String sql = "select distinct province,city from location where province like ?";
        Cursor cursor = sqlDB.rawQuery(sql,new String[]{province});
        if (cursor.moveToFirst()){
            do {
                String city = cursor.getString(cursor.getColumnIndex("city"));
                list.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getCounty(String city){
        List<String> list = new ArrayList<>();
        String sql = "select distinct city,county from location where city like ?";
        Cursor cursor = sqlDB.rawQuery(sql,new String[]{city});
        if (cursor.moveToFirst()){
            do {
                String county = cursor.getString(cursor.getColumnIndex("county"));
                list.add(county);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
