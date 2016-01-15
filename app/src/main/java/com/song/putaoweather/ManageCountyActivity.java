package com.song.putaoweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class ManageCountyActivity extends Activity {
    private ListView cityListView;
    private int position;
    private String county;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_county);
        cityListView = (ListView) findViewById(R.id.cityListView);

        Intent intent = getIntent();
        String string = intent.getStringExtra("Cities");
        List<String> cities = SharedPreferencesUtils.String2List(string);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,cities);
        cityListView.setAdapter(adapter);
        cityListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                
                return false;
            }
        });
    }
}
