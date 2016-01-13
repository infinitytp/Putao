package com.song.putaoweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class SelectCountyActivity extends Activity {
    private GridView gridView;
    private TextView selectCountyTip;
    private TextView tvTip;
    private Button btn;
    private List<String> dataList;
    private static final int PROVINCELEVEL = 0;
    private static final int CITYLEVEL = 1;
    private static final int COUNTYLEVEL = 2;
    private int currentLevel = 0;
    private String selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_county);
        gridView = (GridView) findViewById(R.id.gridView);
        selectCountyTip = (TextView) findViewById(R.id.selectCountyTip);
        tvTip = (TextView) findViewById(R.id.tvTip);
        btn = (Button) findViewById(R.id.btn);

        final Intent intent = getIntent();
        final WeatherDB weatherDB = WeatherDB.getInstance(this);
        dataList = weatherDB.getProvinces();
        final MyAdapter myAdapter = new MyAdapter(SelectCountyActivity.this,R.layout.gridviewitem,dataList);
        gridView.setAdapter(myAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = dataList.get(position);
                if (currentLevel == PROVINCELEVEL) {
                    dataList.clear();
                    Log.d("Tagaaaa", selected);
                    dataList.addAll(weatherDB.getCity(selected));
                    myAdapter.notifyDataSetChanged();
                    currentLevel = CITYLEVEL;
                    Log.d("Tag",String.valueOf(currentLevel));
                } else if (currentLevel == CITYLEVEL) {
                    dataList.clear();
                    dataList.addAll(weatherDB.getCounty(selected));
                    myAdapter.notifyDataSetChanged();
                    currentLevel = COUNTYLEVEL;
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == PROVINCELEVEL){
                    Toast.makeText(SelectCountyActivity.this, "请选择城市", Toast.LENGTH_SHORT).show();
                } else if (currentLevel == CITYLEVEL){
                    Toast.makeText(SelectCountyActivity.this, "请选择县区", Toast.LENGTH_SHORT).show();
                } else if (currentLevel == COUNTYLEVEL){
                    Bundle bundle = new Bundle();
                    bundle.putString("County",selected);
                    bundle.putBoolean("AddCounty",true);
                    intent.putExtras(bundle);
                    intent.setClass(SelectCountyActivity.this, MainActivity.class);
                    setResult(10, intent);
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
            String str = dataList.get(position);
            Log.d("Tag",str);
            TextView tv = (TextView) view.findViewById(R.id.gridItemTv);
            tv.setText(str);
            return view;
        }
    }
}
