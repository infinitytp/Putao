package com.song.putaoweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.song.putaoweather.weatherDAO.WeatherDB;

import java.util.List;

public class SelectCountyActivity extends Activity {
    private GridView gridView;
    private TextView selectCountyTip;
    private TextView tvTip;
    private Button btn;
    private List<String> dataList;
    private static final int PROVINCE_LEVEL = 0;
    private static final int CITY_LEVEL = 1;
    private static final int COUNTY_LEVEL = 2;
    private int currentLevel = 0;
    private String selected;
    private String province,city,county;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_county);
        gridView = (GridView) findViewById(R.id.gridView);
        selectCountyTip = (TextView) findViewById(R.id.selectCountyTip);
        tvTip = (TextView) findViewById(R.id.tvTip);
        btn = (Button) findViewById(R.id.btn);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        final Intent intent = getIntent();
        final WeatherDB weatherDB = WeatherDB.getInstance(this);
        dataList = weatherDB.getProvinces();
        final MyAdapter myAdapter = new MyAdapter(SelectCountyActivity.this,R.layout.gridviewitem,dataList);
        gridView.setAdapter(myAdapter);
        gridView.setMinimumHeight(relativeLayout.getHeight()/2);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = dataList.get(position);
                if (currentLevel == PROVINCE_LEVEL) {
                    province = selected;
                    tvTip.setText(province + ".");
                    dataList.clear();
                    dataList.addAll(weatherDB.getCity(selected));
                    myAdapter.notifyDataSetChanged();
                    currentLevel = CITY_LEVEL;
                } else if (currentLevel == CITY_LEVEL) {
                    city = selected;
                    tvTip.setText(province + "." + city + ".");
                    dataList.clear();
                    dataList.addAll(weatherDB.getCounty(selected));
                    myAdapter.notifyDataSetChanged();
                    currentLevel = COUNTY_LEVEL;
                } else if (currentLevel == COUNTY_LEVEL){
                    county = selected;
                    tvTip.setText(province + "." + city + "." + county);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == PROVINCE_LEVEL){
                    Toast.makeText(SelectCountyActivity.this, "请选择城市", Toast.LENGTH_SHORT).show();
                } else if (currentLevel == CITY_LEVEL){
                    Toast.makeText(SelectCountyActivity.this, "请选择县区", Toast.LENGTH_SHORT).show();
                } else if (currentLevel == COUNTY_LEVEL){
                    Bundle bundle = new Bundle();
                    bundle.putString("County",selected);
                    bundle.putBoolean("AddCounty",true);
                    intent.putExtras(bundle);
                    intent.setClass(SelectCountyActivity.this, MainActivity.class);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private class MyAdapter extends ArrayAdapter<String>{
        private int resourceId;

        public MyAdapter(Context context, int resourceId, List<String> objects){
            super(context,resourceId,objects);
            this.resourceId = resourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            String city = dataList.get(position);
            TextView tv = (TextView) view.findViewById(R.id.gridItemTv);
            tv.setText(city);
            return view;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
