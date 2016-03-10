package com.song.putaoweather.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import com.song.putaoweather.utils.HttpCallbackListener;
import com.song.putaoweather.utils.HttpUtil;
import com.song.putaoweather.utils.ParseXmlUtil;


import java.util.List;

/**
 * Created by SongPc on 2016/3/7.
 */
public class LocalCity implements LocationInterface{

    private static final String GeoUrl = "http://api.map.baidu.com/geocoder/v2/?" +
            "ak=migMLQuSEtcajrZLW8rcchPp&mcode=" +
            "8B:38:41:67:23:C7:A9:11:AB:E2:57:BC:6F:15:CF:6B:B3:AE:EF:C2;com.song.putaoweather" +
            "&output=xml&pois=1&callback=renderReverse&location=";
    private Context context;
    private String city;

    public LocalCity(Context context){
       this.context = context;
    }

    /**
     * 得到本地城市经纬度；
     */
    @Override
    public Location getLocation() {
        Location location = null;
        String provider;

        LocationManager manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = manager.getProviders(true);
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        } else if(providerList.contains(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(context,"没有可用的GPS信号",Toast.LENGTH_SHORT).show();
            provider = null;
        }
        try {
            if(provider!=null){
                location = manager.getLastKnownLocation(provider);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * 得到本地城市名称；
     */
    @Override
    public String getCity() {
        Location location = getLocation();
        String cityLocation = "";
        if(location!=null){
            cityLocation = location.getLatitude() + "," + location.getLongitude();
        }
        String address = GeoUrl + cityLocation;
        MyCallback myCallback = new MyCallback();
        HttpUtil.sendHttpRequest(address,myCallback);
        city = myCallback.getCityName();
        return city;
    }

    public boolean getCity(HttpCallbackListener myCallback){
        Location location = getLocation();
        String cityLocation = "";
        if(location!=null){
            cityLocation = location.getLatitude() + "," + location.getLongitude();
        }
        String address = GeoUrl + cityLocation;
        HttpUtil.sendHttpRequest(address,myCallback);
        return true;
    }

    private class MyCallback implements HttpCallbackListener{
        private String cityName;
        @Override
        public void onFinish(String response) {
            cityName = ParseXmlUtil.getLocationCity(response);
        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }

        public String getCityName(){ return cityName;}
    }
}
