package com.song.putaoweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.song.putaoweather.utils.SharedPreferencesUtils;

import java.util.List;

public class ManageCountyActivity extends Activity {
    private ListView cityListView;
    private ArrayAdapter<String> adapter;
    private List<String> cities;
    private int position;
    private String county;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_county);
        cityListView = (ListView) findViewById(R.id.cityListView);

        intent = getIntent();
        String string = intent.getStringExtra("Cities");
        cities = SharedPreferencesUtils.String2List(string);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,cities);
        cityListView.setAdapter(adapter);
        cityListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                registerForContextMenu(cityListView);
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("菜单");
        menu.add(0, Menu.FIRST,0,"删除");
        menu.add(0, Menu.FIRST+1,0,"返回");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()){
            case Menu.FIRST:
                Toast.makeText(ManageCountyActivity.this,"删除 " + adapter.getItem(info.position),Toast.LENGTH_SHORT).show();
                cities.remove(info.position);
                adapter.notifyDataSetChanged();
                break;
            case Menu.FIRST +1:
                Toast.makeText(ManageCountyActivity.this,adapter.getItem(info.position) + info.position, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String resultString = SharedPreferencesUtils.List2String(cities);
        intent.putExtra("Counties",resultString);
        intent.putExtra("RemoveCounty",true);
        intent.setClass(ManageCountyActivity.this,MainActivity.class);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}
