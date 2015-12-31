package com.song.putaoweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
    /*public WeatherLive weatherLive;
    public  List<Weather> weatherList;*/
    private TextView cityLive,liveType,liveTemperature,liveWindPower,liveWindDirection,liveHumidity;
    private SharedPreferences pref;
    private MyAdapter adapter;
    private ListView listView;

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

        findItem();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean remember = pref.getBoolean("rememberLocation",false);
        if (remember){
            city = pref.getString("Location","");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    connectWeatherSite(city);
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Location location = getLocation();
                    city = getCityFromBaidu(location);
                    Log.d("Tag",city + city + city);
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
            Log.d("Tag","增加城市");
        } else if (id == R.id.manageCity) {
            Log.d("Tag","管理城市");
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

    public void connectWeatherSite(String cityName){
        try {
            if (cityName!=null){
                cityName = URLEncoder.encode(cityName,"utf-8");
                String address = WEATHERADDRESS + cityName;
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
                    String str = (String)msg.obj;
                    WeatherLive weatherLive = ParseXmlUtil.getLiveWeather(str);
                    List<Weather> weatherList = ParseXmlUtil.getSixDaysWeather(str);
                    refreshLiveWeather(weatherLive);
                    liveType.setText(weatherList.get(1).getDayType());
                    refreshWeather(weatherList);

                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    public void refreshLiveWeather(WeatherLive weatherLive){
        cityLive.setText(weatherLive.getCity());
        liveTemperature.setText(weatherLive.getWendu() + "℃");
        liveWindPower.setText("风力:" + weatherLive.getFengli());
        liveWindDirection.setText("风向:" + weatherLive.getFengxiang());
        liveHumidity.setText("湿度:" + weatherLive.getShidu());
    }

    public void refreshWeather(List<Weather> weathers){
        adapter = new MyAdapter(MainActivity.this,R.layout.weatheritem,weathers);
        listView.setAdapter(adapter);
    }

    public void findItem(){
        cityLive = (TextView) findViewById(R.id.cityLive);
        liveType = (TextView) findViewById(R.id.liveType);
        liveTemperature = (TextView) findViewById(R.id.liveTemperature);
        liveWindPower = (TextView) findViewById(R.id.liveWindPower);
        liveWindDirection = (TextView) findViewById(R.id.liveWindDirection);
        liveHumidity = (TextView) findViewById(R.id.liveHumidity);
        listView = (ListView) findViewById(R.id.weatherListView);
    }

    private class MyHttpCallBack implements HttpCallbackListener{
        private String cityName;

        @Override
        public void onFinish(String response) {
            cityName = ParseXmlUtil.getLocationCity(response);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("rememberLocation",true);
            editor.putString("Location",cityName);
            editor.commit();
        }

        @Override
        public void onError(Exception e) {

        }

        public String getCityName(){
            return cityName;
        }
    }

    private class MyAdapter extends ArrayAdapter<Weather> {
        private int resouredId;

        public MyAdapter(Context context,int resouredId,List<Weather> objects){
            super(context,resouredId,objects);
            this.resouredId = resouredId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Weather weather = getItem(position);
            View view = LayoutInflater.from(getContext()).inflate(resouredId, null);
            TextView dataId = (TextView) view.findViewById(R.id.dateId);
            TextView highTemperature = (TextView) view.findViewById(R.id.highTemperature);
            TextView lowTemperature = (TextView) view.findViewById(R.id.lowTemperature);
            dataId.setText(weather.getDate());
            highTemperature.setText(weather.getHigh());
            lowTemperature.setText(weather.getLow());
            return view;
        }
    }

}
