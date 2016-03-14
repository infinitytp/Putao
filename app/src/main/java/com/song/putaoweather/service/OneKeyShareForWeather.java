package com.song.putaoweather.service;

import android.content.Context;
import android.service.chooser.ChooserTargetService;

import com.song.putaoweather.R;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by SongPc on 2016/3/14.
 */
public class OneKeyShareForWeather {
    private Context context;

    public OneKeyShareForWeather(Context context){
        this.context = context;
    }

    public void share(){
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setTitle(context.getString(R.string.ssdk_oks_share));
        oks.setTitleUrl("https://github.com/infinitytp");
        oks.setText("I Am GrapeWeather!");
        oks.setUrl("https://github.com/infinitytp");
        oks.setSiteUrl("https://github.com/infinitytp");
        oks.show(context);
    }
}
