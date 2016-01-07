package com.song.putaoweather;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private TextView cityLive,liveType,liveTemperature,liveWindPower,liveWindDirection,liveHumidity;
    private ListView listView;
    private WeatherLive weatherLive;
    private List<Weather> weathers;

    public WeatherFragment() {
        // Required empty public constructor
    }

    public static WeatherFragment newInstance(String response){
        WeatherFragment wf = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Response",response);
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

}
