package com.song.putaoweather.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SongPc on 2015/12/28.
 */
public class HttpUtil {
    private static final String ENCODING = "UTF-8";

    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){

            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(address);
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine())!=null){
                    stringBuilder.append(line);
                }
                if (listener!=null){
                    listener.onFinish(stringBuilder.toString());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(Exception e){
                if (listener!=null){
                    listener.onError(e);
                }
            } finally {
                if (urlConnection!=null){
                    urlConnection.disconnect();
                }
            }
    }
}
