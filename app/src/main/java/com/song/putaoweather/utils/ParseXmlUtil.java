package com.song.putaoweather.utils;

import android.util.Log;

import com.mob.tools.utils.LocalDB;
import com.song.putaoweather.model.Weather;
import com.song.putaoweather.model.WeatherLive;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SongPc on 2015/12/28.
 */
public class ParseXmlUtil {

    public static WeatherLive getLiveWeather(String response){
        WeatherLive weatherLive = new WeatherLive();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser pullParser = factory.newPullParser();
            InputStream inputStream = new ByteArrayInputStream(response.getBytes());
            pullParser.setInput(inputStream,"utf-8");
            int eventType = pullParser.getEventType();
            boolean flag = true;
            while (flag && eventType!=XmlPullParser.END_DOCUMENT ){
                String tagName = pullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (tagName!=null && tagName.equals("city")){
                            weatherLive.setCity(pullParser.nextText());
                        }
                        if (tagName!=null && tagName.equals("updatetime")){
                            weatherLive.setUpdatetime(pullParser.nextText());
                        }
                        if (tagName!=null && tagName.equals("wendu")){
                            weatherLive.setWendu(pullParser.nextText());
                        }
                        if (tagName!=null && tagName.equals("shidu")){
                            weatherLive.setShidu(pullParser.nextText());
                        }
                        if (tagName!=null && tagName.equals("fengxiang")){
                            weatherLive.setFengxiang(pullParser.nextText());
                        }
                        if (tagName!=null && tagName.equals("fengli")){
                            weatherLive.setFengli(pullParser.nextText());
                        }
                        if (tagName!=null && tagName.equals("sunrise_1")){
                            weatherLive.setSunrise(pullParser.nextText());
                        }
                        if (tagName!=null && tagName.equals("sunset_1")){
                            weatherLive.setSunset(pullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName!=null && tagName.equals("yesterday")){
                            flag = false;
                        }
                        break;
                    default:
                        break;
                }

                try {
                    eventType = pullParser.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return weatherLive;
    }

    public static List<Weather> getSixDaysWeather(String response){
        List<Weather> weathersList = new ArrayList<>();
        Weather weather = null;
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            InputStream inputStream = new ByteArrayInputStream(response.getBytes());
            xmlPullParser.setInput(inputStream,"utf-8");
            int eventType = xmlPullParser.getEventType();
            String type = null;
            String fengxiang = null;
            String fengli = null;
            while (eventType!=XmlPullParser.END_DOCUMENT){
                String tagName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (tagName!=null && tagName.equals("yesterday")){
                            weather = new Weather();
                        }
                        if (tagName!=null && tagName.equals("date_1") && weather!=null){
                            StringBuilder sb = new StringBuilder();
                            String date = xmlPullParser.nextText();
                            Pattern p = Pattern.compile("[\\u4E00-\\u9FA5][\\u4E00-\\u9FA5][\\u4E00-\\u9FA5]$");
                            Matcher matcher = p.matcher(date);
                            if (matcher.find()){
                                sb.append(matcher.group());
                            }
                            weather.setDate(sb.toString());
                        }
                        if (tagName!=null && tagName.equals("high_1") && weather!=null){
                            weather.setHigh(xmlPullParser.nextText().split("温")[1]);
                        }
                        if (tagName!=null && tagName.equals("low_1") && weather!=null){
                            weather.setLow(xmlPullParser.nextText().split("温")[1]);
                        }
                        if (tagName!=null && tagName.equals("type_1")){
                            type = xmlPullParser.nextText();
                        }
                        if (tagName!=null && tagName.equals("fengxiang_1")){
                            fengxiang = xmlPullParser.nextText();
                        }
                        if (tagName!=null && tagName.equals("fengli_1")){
                            fengli = xmlPullParser.nextText();
                        }
                        if (tagName!=null && tagName.equals("weather")){
                            weather = new Weather();
                        }
                        if (tagName!=null && tagName.equals("date") && weather!=null){
                            StringBuilder sb = new StringBuilder();
                            String date = xmlPullParser.nextText();
                            Pattern p = Pattern.compile("[\\u4E00-\\u9FA5][\\u4E00-\\u9FA5][\\u4E00-\\u9FA5]$");
                            Matcher matcher = p.matcher(date);
                            if (matcher.find()){
                                sb.append(matcher.group());
                            }
                            weather.setDate(sb.toString());
                        }
                        if (tagName!=null && tagName.equals("high") && weather!=null){
                            weather.setHigh(xmlPullParser.nextText().split("温")[1]);
                        }
                        if (tagName!=null && tagName.equals("low") && weather!=null){
                            weather.setLow(xmlPullParser.nextText().split("温")[1]);
                        }
                        if (tagName!=null && tagName.equals("type")){
                            type = xmlPullParser.nextText();
                        }
                        if (tagName!=null && tagName.equals("fengxiang")){
                            fengxiang = xmlPullParser.nextText();
                        }
                        if (tagName!=null && tagName.equals("fengli")){
                            fengli = xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName!=null && tagName.equals("day_1") && weather!=null){
                            weather.setDayType(type);
                            weather.setDayFengxiang(fengxiang);
                            weather.setDayFengli(fengli);
                            type = null;
                            fengxiang = null;
                            fengli = null;
                        }
                        if (tagName!=null && tagName.equals("night_1") && weather !=null){
                            weather.setNightType(type);
                            weather.setNightFengxiang(fengxiang);
                            weather.setNightFengli(fengli);
                            type = null;
                            fengxiang = null;
                            fengli = null;
                        }
                        if (tagName!=null && tagName.equals("yesterday") && weather!=null){
                            weathersList.add(weather);
                            weather = null;
                        }
                        if (tagName!=null && tagName.equals("day") && weather!=null){
                            weather.setDayType(type);
                            weather.setDayFengxiang(fengxiang);
                            weather.setDayFengli(fengli);
                            type = null;
                            fengxiang = null;
                            fengli = null;
                        }
                        if (tagName!=null && tagName.equals("night") && weather !=null){
                            weather.setNightType(type);
                            weather.setNightFengxiang(fengxiang);
                            weather.setNightFengli(fengli);
                            type = null;
                            fengxiang = null;
                            fengli = null;
                        }
                        if (tagName!=null && tagName.equals("weather") && weather!=null){
                            weathersList.add(weather);
                            weather = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return weathersList;
    }

    public static String getLocationCity(String response){
        String city = "";
        try {
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlFactory.newPullParser();
            InputStream in = new ByteArrayInputStream(response.getBytes());
            xmlPullParser.setInput(in,"utf-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType!= XmlPullParser.END_DOCUMENT){
                String tagName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (tagName.equals("city")){
                            city = xmlPullParser.nextText();
                        }
                        break;
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        city = city.replace("市","");
        return city;
    }
}
