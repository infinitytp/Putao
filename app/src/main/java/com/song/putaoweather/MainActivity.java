package com.song.putaoweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String WEATHERADDRESS = "http://wthrcdn.etouch.cn/WeatherApi?city=";
    private static final String GeoUrl = "http://api.map.baidu.com/geocoder/v2/?" +
            "ak=wwGMxI8Y2yG2nUxVfZ47MzO3&mcode=" +
            "8B:38:41:67:23:C7:A9:11:AB:E2:57:BC:6F:15:CF:6B:B3:AE:EF:C2;com.song.networktest" +
            "&output=xml&pois=1&callback=renderReverse&location=";
    private MyHandler myHandler;
    private String  city = null;
    private SharedPreferences pref;
    private WeatherDB weatherDB;
    private ViewPager viewPager;
    private ArrayList<WeatherFragment> fragments;
    private List<String> cities = new ArrayList<>();
    private boolean addCounty = false;
    private boolean removeCounty = false;
    private static final int SELECT_COUNTY_REQUEST_CODE = 1;
    private static final int MANAGE_COUNTY_REQUEST_CODE = 2;
    private MyFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        weatherDB.init();


        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean remember = pref.getBoolean("rememberLocation",false);
        if (remember){
            cities = SharedPreferencesUtils.String2List(pref.getString("City",""));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String city:cities){
                        connectWeatherSite(city);
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Location location = getLocation();
                    city = getCityFromBaidu(location);
                    if (city!=null){
                        connectWeatherSite(city);
                    }
                }
            }).start();
        }

        myHandler = new MyHandler();
        Message msg = myHandler.obtainMessage();
        myHandler.handleMessage(msg);

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
        if (id == R.id.action_settings) {
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
            Intent intent = new Intent(MainActivity.this,SelectCountyActivity.class);
            startActivityForResult(intent, SELECT_COUNTY_REQUEST_CODE);
        } else if (id == R.id.manageCity) {
            Intent intent = new Intent(MainActivity.this,ManageCountyActivity.class);
            intent.putExtra("Cities",SharedPreferencesUtils.List2String(cities));
            startActivityForResult(intent, MANAGE_COUNTY_REQUEST_CODE);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * 拿到本机所在经纬度;
     * */
    public Location getLocation(){
        Location location = null;
        String provider;

        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> providersList = manager.getProviders(true);
        if (providersList.contains(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
        } else if (providersList.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(MainActivity.this,"没有可用的GPS",Toast.LENGTH_SHORT).show();
            provider = null;
        }

        try {
            if (provider!=null){
                location = manager.getLastKnownLocation(provider);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * 连接百度GEOAPI，得到城市名称;
    * */
    public String getCityFromBaidu(Location location){
        String cityName;
        String cityLocation = "";
        if (location!=null){
            cityLocation = location.getLatitude() + "," + location.getLongitude();
        }
        String address = GeoUrl + cityLocation;
        MyHttpCallBack callBack = new MyHttpCallBack();
        HttpUtil.sendHttpRequest(address,callBack);
        cityName = callBack.getCityName();
        return cityName;
    }

    public void connectWeatherSite(String city){
        try {
            if (city!=null){
                city = URLEncoder.encode(city,"utf-8");
                String address = WEATHERADDRESS + city;
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Message msg = myHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = response;
                        myHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private class MyHandler extends Handler{
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

    private class MyHttpCallBack implements HttpCallbackListener{
        private String cityName;

        @Override
        public void onFinish(String response) {
            cityName = ParseXmlUtil.getLocationCity(response);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("rememberLocation",true);
            editor.putString("Location",cityName);
            cities.add(cityName);
            editor.commit();
        }

        @Override
        public void onError(Exception e) {

        }

        public String getCityName(){
            return cityName;
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
                        removeCounty = true;
                        break;
                    case RESULT_CANCELED:
                        removeCounty = true;
                        cities.remove(cities.size()-1);
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
            final String county = cities.get(cities.size()-1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connectWeatherSite(county);
                }
            }).start();
            addCounty = false;
        }
        if (removeCounty){
//            adapter.notifyDataSetChanged();
            removeCounty = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = pref.edit();
        String cityXml = SharedPreferencesUtils.List2String(cities);
        editor.putString("City",cityXml);
        editor.commit();
    }
}
