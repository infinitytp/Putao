package com.song.putaoweather.weatherDAO;

import java.util.List;

/**
 * Created by SongPc on 2016/3/5.
 */
public interface WeatherDBInterface {
    List<String> getProvinces();
    List<String> getCity(String province);
    List<String> getCounty(String city);
    void init();
}
