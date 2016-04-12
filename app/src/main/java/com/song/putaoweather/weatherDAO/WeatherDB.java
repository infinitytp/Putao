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
        writeWeatherType();
        init();
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

    public void writeWeatherType(){
        if (sqlDB.query("weatherType",null,null,null,null,null,null).getCount()>0){
            return;
        } else {
            sqlDB.beginTransaction();
            sqlDB.execSQL("insert into weatherType values(0,'晴','sunny','drawable');");
            sqlDB.execSQL("insert into weatherType values(1,'多云','cloudy','drawable');");
            sqlDB.execSQL("insert into weatherType values(2,'阴','overcast','drawable');");
            sqlDB.execSQL("insert into weatherType values(3,'阵雨','shower','drawable');");
            sqlDB.execSQL("insert into weatherType values(4,'雷阵雨','thundershower','drawable');");
            sqlDB.execSQL("insert into weatherType values(5,'雷阵雨伴有冰雹','thundershower_with_hail','drawable');");
            sqlDB.execSQL("insert into weatherType values(6,'雨夹雪','sleet','drawable');");
            sqlDB.execSQL("insert into weatherType values(7,'小雨','light_rain','drawable');");
            sqlDB.execSQL("insert into weatherType values(8,'中雨','moderate_rain','drawable');");
            sqlDB.execSQL("insert into weatherType values(9,'大雨','heavy_rain','drawable');");
            sqlDB.execSQL("insert into weatherType values(10,'暴雨','storm','drawable');");
            sqlDB.execSQL("insert into weatherType values(11,'大暴雨','heavy_storm','drawable');");
            sqlDB.execSQL("insert into weatherType values(12,'特大暴雨','severe_storm','drawable');");
            sqlDB.execSQL("insert into weatherType values(13,'阵雪','snow_flurry','drawable');");
            sqlDB.execSQL("insert into weatherType values(14,'小雪','light_snow','drawable');");
            sqlDB.execSQL("insert into weatherType values(15,'中雪','moderate_snow','drawable');");
            sqlDB.execSQL("insert into weatherType values(16,'大雪','heavy_snow','drawable');");
            sqlDB.execSQL("insert into weatherType values(17,'暴雪','snowstorm','drawable');");
            sqlDB.execSQL("insert into weatherType values(18,'雾','foggy','drawable');");
            sqlDB.execSQL("insert into weatherType values(19,'冻雨','ice_rain','drawable');");
            sqlDB.execSQL("insert into weatherType values(20,'沙尘暴','dust_storm','drawable');");
            sqlDB.execSQL("insert into weatherType values(21,'小到中雨','light_to_moderate_rain','drawable');");
            sqlDB.execSQL("insert into weatherType values(22,'中到大雨','moderate_to_heavy_rain','drawable');");
            sqlDB.execSQL("insert into weatherType values(23,'大到暴雨','heavy_rain_to_storm','drawable');");
            sqlDB.execSQL("insert into weatherType values(24,'暴雨到大暴雨','storm_to_heavy_storm','drawable');");
            sqlDB.execSQL("insert into weatherType values(25,'大暴雨到特大暴雨','heavy_to_severe_storm','drawable');");
            sqlDB.execSQL("insert into weatherType values(26,'小到中雪','light_to_moderate_snow','drawable');");
            sqlDB.execSQL("insert into weatherType values(27,'中到大雪','moderate_to_heavy_snow','drawable');");
            sqlDB.execSQL("insert into weatherType values(28,'大到暴雪','heavy_snow_to_snowstorm','drawable');");
            sqlDB.execSQL("insert into weatherType values(29,'浮尘','dust','drawable');");
            sqlDB.execSQL("insert into weatherType values(30,'扬沙','sand','drawable');");
            sqlDB.execSQL("insert into weatherType values(31,'强沙尘暴','sandstorm','drawable');");
            sqlDB.execSQL("insert into weatherType values(53,'霾','haze','drawable');");
            sqlDB.execSQL("insert into weatherType values(99,'无','unknown','drawable');");
            sqlDB.setTransactionSuccessful();
            sqlDB.endTransaction();
        }
    }

    public int getImageId(String type){
        String fileName = "unknown";
        String filepath = "drawable";
        String sql = "select en_name from weatherType where cn_name like ?";
        Cursor cursor = sqlDB.rawQuery(sql,new String[]{type});
        if (cursor.moveToFirst()){
            do {
                fileName = cursor.getString(cursor.getColumnIndex("en_name"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myContext.getResources().getIdentifier(fileName,filepath,myContext.getPackageName());
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
