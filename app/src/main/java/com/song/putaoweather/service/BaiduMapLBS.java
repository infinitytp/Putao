package com.song.putaoweather.service;

import android.content.Context;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


/**
 * Created by SongPc on 2016-03-23.
 */
public class BaiduMapLBS {
    private Context context;
    private LocationClient mLocationClient;
    private MyLocationListener listener;

    public BaiduMapLBS(Context context){
        this.context = context;
        mLocationClient = new LocationClient(context.getApplicationContext());
        listener = new MyLocationListener();
        initLocation();
    }

    public void getCity(BaiduMapResponse response){
        listener.setListener(response);
        mLocationClient.registerLocationListener(listener);
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(2000);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setIsNeedLocationPoiList(true);
        mLocationClient.setLocOption(option);
    }

    private class MyLocationListener implements BDLocationListener{
        private BaiduMapResponse response;

        private void setListener(BaiduMapResponse response){
            this.response = response;
        }

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation.getLocType()==BDLocation.TypeNetWorkLocation){
                String city = bdLocation.getCity().replace("市","");
                response.onResponse(city);
            } else if (bdLocation.getLocType() == BDLocation.TypeServerError){
                Toast.makeText(context, "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因", Toast.LENGTH_SHORT).show();
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException){
                Toast.makeText(context, "网络不同导致定位失败，请检查网络是否通畅", Toast.LENGTH_SHORT).show();
            }
            mLocationClient.stop();

        }
    }
    public interface BaiduMapResponse{
        void onResponse(String city);
    }
}
