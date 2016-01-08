package com.song.putaoweather;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongPc on 2016/1/8.
 */
public class LocationList {

    private static final String FILE_NAME = "areaid_v.csv";
    private static final String ENCODING = "GBK";
    private Context context;

    public LocationList(Context context){this.context = context;}

    public List<LocationId> getLocationList(){
        AssetManager am;
        InputStream is = null;
        BufferedReader br = null;
        List<LocationId> list = new ArrayList<>();
        String line;

        try {
            am = context.getAssets();
            is = am.open(FILE_NAME);
            br = new BufferedReader(new InputStreamReader(is,ENCODING));
            while ((line = br.readLine())!=null){
                String[] oneLine = line.split(",");
                LocationId id = new LocationId();
                id.setCountyId(oneLine[0]);
                id.setCounty(oneLine[1]);
                id.setCityId(oneLine[2]);
                id.setCity(oneLine[3]);
                id.setProvinceId(oneLine[4]);
                id.setProvince(oneLine[5]);
                list.add(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        list.remove(0);
        return list;
    }

    public void getcount(){
        List<LocationId> list = getLocationList();
        Log.d("Tag",String.valueOf(list.size()));
        Log.d("Tag",list.get(23).getCity());
    }
}
