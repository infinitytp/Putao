package com.song.putaoweather;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.song.putaoweather.model.Weather;
import com.song.putaoweather.model.WeatherLive;
import com.song.putaoweather.utils.ParseXmlUtil;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private TextView cityLive,liveType,liveTemperature,liveWindPower,liveWindDirection,liveHumidity;
    private ListView listView;
    private static String city;
    private GridView gridViewfordays;
    private TemperatureView myTempView;

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
        myTempView.setWeatherList(weatherList);
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
        gridViewfordays = (GridView) view.findViewById(R.id.girdViewfordays);
        myTempView = (TemperatureView) view.findViewById(R.id.myTempView);
    }

    public void refreshLiveWeather(WeatherLive weatherLive){
        cityLive.setText(weatherLive.getCity());
        liveTemperature.setText(weatherLive.getWendu() + "℃");
        liveWindPower.setText("风力:    " + weatherLive.getFengli());
        liveWindDirection.setText("风向:    " + weatherLive.getFengxiang());
        liveHumidity.setText("湿度:    " + weatherLive.getShidu());
    }

    public void refreshWeather(List<Weather> weatherList){
        MyAdapter adapter = new MyAdapter(getContext(),R.layout.weatheritem,weatherList);
        listView.setAdapter(adapter);
        MyGridAdpater gridAdpater = new MyGridAdpater(getContext(),R.layout.weatheritemforgird,weatherList);
        gridViewfordays.setAdapter(gridAdpater);
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

    private class MyGridAdpater extends ArrayAdapter<Weather>{
        private int resourcedId;

        public MyGridAdpater(Context context, int resource, List<Weather> objects) {
            super(context, resource, objects);
            this.resourcedId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Weather weather = getItem(position);
            ViewHolder holder;
            View view;
            if (convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourcedId,null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.typeIdGrid.setText(weather.getDayType());
            holder.dateIdGrid.setText(weather.getDate());
            return view;
        }
    }

    private class ViewHolder{
        public TextView dateIdGrid;
        public TextView typeIdGrid;

        public ViewHolder(View view){
            dateIdGrid = (TextView) view.findViewById(R.id.dateIdGrid);
            typeIdGrid = (TextView) view.findViewById(R.id.typeIdGrid);
        }
    }
}
