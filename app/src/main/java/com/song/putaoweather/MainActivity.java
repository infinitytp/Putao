package com.song.putaoweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.song.putaoweather.service.BaiduMapLBS;
import com.song.putaoweather.service.LocalCity;
import com.song.putaoweather.service.OneKeyShareForWeather;
import com.song.putaoweather.service.WeatherInfo;
import com.song.putaoweather.utils.HttpCallbackListener;
import com.song.putaoweather.utils.ParseXmlUtil;
import com.song.putaoweather.utils.SharedPreferencesUtils;
import com.song.putaoweather.weatherDAO.WeatherDB;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MyHandler myHandler;
    private SharedPreferences pref;
    public  WeatherDB weatherDB;
    private ViewPager viewPager;
    private ArrayList<WeatherFragment> fragments;
    private List<String> cities = new ArrayList<>();
    private boolean addCounty = false;
    private boolean removeCounty = false;
    private static final int SELECT_COUNTY_REQUEST_CODE = 1;
    private static final int MANAGE_COUNTY_REQUEST_CODE = 2;
    private MyFragmentAdapter adapter;
    private LocalCity localCity;
    private WeatherInfo weatherInfo;
    private ProgressDialog dialog;
    private OneKeyShareForWeather shareForWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        fragments = new ArrayList<>();
        weatherDB = WeatherDB.getInstance(this);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean remember = pref.getBoolean("rememberLocation",false);
        if (remember){
            cities = SharedPreferencesUtils.String2List(pref.getString("City", ""));
            weatherInfo = new WeatherInfo(cities);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    weatherInfo.getWeatherForCities(myHandler);
                }
            }).start();
        } else {
            localCity = new LocalCity(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String city = localCity.getCity();
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("rememberLocation",true);
                    editor.putString("Location", city);
                    cities.add(city);
                    editor.commit();
                    weatherInfo = new WeatherInfo(cities);
                    weatherInfo.getWeatherForCities(myHandler);
                    saveCities();
                }
            }).start();
        }

        myHandler = new MyHandler();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_locate) {
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("正在定位");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();

            BaiduMapLBS mapLBS = new BaiduMapLBS(MainActivity.this);
            mapLBS.getCity(new BaiduMapLBS.BaiduMapResponse() {
                @Override
                public void onResponse(String city) {
                    final String location = city;
                    if (cities.contains(city)) {
                        Toast.makeText(MainActivity.this, "当前城市: " + city, Toast.LENGTH_SHORT).show();
                        dialog.hide();
                    } else {
                        Toast.makeText(MainActivity.this, "当前位置: " + city, Toast.LENGTH_SHORT).show();
                        dialog.hide();
                        if ((!city.equals("")) && city != null) {
                            cities.add(city);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                weatherInfo.getWeatherForSingleCity(location, myHandler);
                            }
                        }).start();
                    }
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addCity) {
            // Handle the camera action
            item.setCheckable(false);
            Intent intent = new Intent(MainActivity.this,SelectCountyActivity.class);
            startActivityForResult(intent, SELECT_COUNTY_REQUEST_CODE);
        } else if (id == R.id.manageCity) {
            item.setCheckable(false);
            Intent intent = new Intent(MainActivity.this,ManageCountyActivity.class);
            intent.putExtra("Cities",SharedPreferencesUtils.List2String(cities));
            startActivityForResult(intent, MANAGE_COUNTY_REQUEST_CODE);
        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_share) {
            shareForWeather = new OneKeyShareForWeather(MainActivity.this);
            shareForWeather.share();
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 获取天气信息，并且传给Fragments；
     */
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String response = (String)msg.obj;
                    WeatherFragment fragment = WeatherFragment.newInstance(response);
                    fragments.add(fragment);
                    if (adapter == null){
                        adapter = new MyFragmentAdapter(getSupportFragmentManager(),fragments);
                        viewPager.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECT_COUNTY_REQUEST_CODE:
                switch (resultCode){
                    case RESULT_OK:
                        Bundle bundle = data.getExtras();
                        String county = bundle.getString("County");
                        addCounty = bundle.getBoolean("AddCounty");
                        cities.add(county);
                        break;
                    default:
                        break;
                }
                break;
            case MANAGE_COUNTY_REQUEST_CODE:
                switch (resultCode){
                    case RESULT_OK:
                        removeCounty = data.getBooleanExtra("RemoveCounty",false);
                        String resultString = data.getStringExtra("Counties");
                        cities.clear();
                        cities.addAll(SharedPreferencesUtils.String2List(resultString));
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (addCounty){
            final String city = cities.get(cities.size()-1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    weatherInfo.getWeatherForSingleCity(city, myHandler);
                }
            }).start();
            addCounty = false;
        }
        if (removeCounty){
            Iterator<WeatherFragment> iterator = fragments.iterator();
            while (iterator.hasNext()){
                WeatherFragment fragment = iterator.next();
                if (!cities.contains(fragment.getArguments().getString("City"))){
                    iterator.remove();
                }
            }
            adapter.notifyDataSetChanged();
            removeCounty = false;
        }
    }

    @Override
    protected void onPause() {
        saveCities();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        saveCities();
        super.onDestroy();
    }

    /**
     * 保存城市列表到SharedPreferences；
     */
    public void saveCities(){
        SharedPreferences.Editor editor = pref.edit();
        String cityXml = SharedPreferencesUtils.List2String(cities);
        editor.putString("City",cityXml);
        editor.commit();
    }
}
