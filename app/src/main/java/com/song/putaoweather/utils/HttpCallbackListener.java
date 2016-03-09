package com.song.putaoweather.utils;

/**
 * Created by SongPc on 2015/12/28.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
