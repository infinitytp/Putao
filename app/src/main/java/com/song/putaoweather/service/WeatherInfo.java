package com.song.putaoweather.service;

import android.os.Handler;
import android.os.Message;

import com.song.putaoweather.utils.HttpCallbackListener;
import com.song.putaoweather.utils.HttpUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by SongPc on 2016/3/9.
 */
public class WeatherInfo {
    private static final String WEATHER_ADDRESS = "http://wthrcdn.etouch.cn/WeatherApi?city=";
    private static final String ENCODING =  "utf-8";
    private List<String> cities;


    public WeatherInfo(List<String> cities){
        this.cities = cities;
    }

    /**
     * 获取城市列表的天气信息，可以通过重写Handler得到返回值；
     */
    public void getWeatherForCities(final Handler handler){
        for(String city:cities) {
            getWeatherForSingleCity(city, handler);
        }
    }
    /**
     * 获取单个城市的天气信息，可以通过重写Handler得到返回值；
     */
    public void getWeatherForSingleCity(String city, final Handler myHandler){
        try {
            if (city!=null){
                city = URLEncoder.encode(city,ENCODING);
                String address = WEATHER_ADDRESS + city;
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Message msg = myHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response;
                        myHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
