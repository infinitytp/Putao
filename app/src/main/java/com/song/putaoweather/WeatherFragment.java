package com.song.putaoweather;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private TextView cityLive,liveType,liveTemperature,liveWindPower,liveWindDirection,liveHumidity;
    private ListView listView;
    private static String city;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance(String response){
        WeatherFragment wf = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Response", response);
        WeatherLive weatherLive = ParseXmlUtil.getLiveWeather(response);
        city = weatherLive.getCity();
        bundle.putString("City",city);
        wf.setArguments(bundle);
        return wf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_weather, container, false);
        findItem(view);
        String response = getArguments().getString("Response");
        WeatherLive weatherLive = ParseXmlUtil.getLiveWeather(response);
        List<Weather> weatherList = ParseXmlUtil.getSixDaysWeather(response);
        refreshLiveWeather(weatherLive);
        liveType.setText(weatherList.get(1).getDayType());
        refreshWeather(weatherList);
        return view;
    }

    public void findItem(View view){
        cityLive = (TextView) view.findViewById(R.id.cityLive);
        liveType = (TextView) view.findViewById(R.id.liveType);
        liveTemperature = (TextView) view.findViewById(R.id.liveTemperature);
        liveWindPower = (TextView) view.findViewById(R.id.liveWindPower);
        liveWindDirection = (TextView) view.findViewById(R.id.liveWindDirection);
        liveHumidity = (TextView) view.findViewById(R.id.liveHumidity);
        listView = (ListView) view.findViewById(R.id.weatherListView);
    }

    public void refreshLiveWeather(WeatherLive weatherLive){
        cityLive.setText(weatherLive.getCity());
        liveTemperature.setText(weatherLive.getWendu() + "℃");
        liveWindPower.setText("风力:" + weatherLive.getFengli());
        liveWindDirection.setText("风向:" + weatherLive.getFengxiang());
        liveHumidity.setText("湿度:" + weatherLive.getShidu());
    }

    public void refreshWeather(List<Weather> weatherList){
        MyAdapter adapter = new MyAdapter(getContext(),R.layout.weatheritem,weatherList);
        listView.setAdapter(adapter);
    }

    private class MyAdapter extends ArrayAdapter<Weather>{
        private int resourceId;

        public MyAdapter(Context context, int resourceId,List<Weather> list){
            super(context,resourceId,list);
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Weather weather = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            TextView dataId = (TextView) view.findViewById(R.id.dateId);
            TextView high = (TextView) view.findViewById(R.id.highTemperature);
            TextView low = (TextView) view.findViewById(R.id.lowTemperature);
            dataId.setText(weather.getDate());
            high.setText(weather.getHigh());
            low.setText(weather.getLow());
            return view;
        }
    }
}
