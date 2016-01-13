package com.song.putaoweather;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by SongPc on 2016/1/13.
 */
public class SharedPreferencesUtils {

    public static String List2String(List<String> list){
        String xmlString = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(list);
            xmlString = new String(Base64.encode(baos.toByteArray(),Base64.DEFAULT));
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlString;
    }

    public static List<String> String2List(String string){
        byte[] bytes = Base64.decode(string.getBytes(),Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        List<String> list = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            list = (List<String>) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }
}
