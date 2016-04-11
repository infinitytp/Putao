package com.song.putaoweather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.song.putaoweather.model.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by infinitytp on 2016/4/8.
 */
public class TemperatureView extends View {

    private Paint mPaint;
    private Rect mBounds;
    private List<Weather> weatherList;
    private int[] dayTemp = new int[6];
    private int[] nightTemp = new int[6];

    public TemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBounds = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.rgb(76, 175, 80));
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        mPaint.setColor(Color.YELLOW);
        int size = getMax(dayTemp) - getMin(nightTemp) + 3;
        int nightMin = getMin(nightTemp);

        draw_Temperature(dayTemp,canvas,mPaint,nightMin,size);
        draw_Temperature(nightTemp,canvas,mPaint,nightMin,size);
    }

    public void setWeatherList(List<Weather> list){
        this.weatherList = list;
        for (int i=0;i<list.size();i++){
            dayTemp[i] = Integer.parseInt(list.get(i).getHigh().replace("℃","").trim());
            nightTemp[i] = Integer.parseInt(list.get(i).getLow().replace("℃","").trim());
        }
    }



    public int getMin(int[] a){
        int temp = a[0];
        for (int i=1;i<a.length;i++){
            if (a[i]<=temp){
                temp = a[i];
            }
        }
        return temp;
    }

    public int getMax(int[] a){
        int temp = a[0];
        for (int i=1; i<a.length;i++){
            if (a[i] >=temp){
                temp = a[i];
            }
        }
        return temp;
    }

    public void draw_Temperature(int[] a,Canvas canvas,Paint paint,int min,int size){
        List<Map<String,Float>> list = new ArrayList<>();


        for (int i=0;i<a.length;i++){
            Map<String,Float> map = new HashMap<>();
            float x = (float)(getWidth()/6 *(i+1) - getWidth()/12);
            float y = (float)getHeight() - (float)getHeight()/size * (a[i] - min + 1f);
            canvas.drawCircle(x, y, 10, paint);
            map.put("x", x);
            map.put("y", y);
            list.add(map);
        }

        for (int i=0;i<list.size() -1;i++){
            float startX = list.get(i).get("x");
            float startY = list.get(i).get("y");
            float stopX = list.get(i+1).get("x");
            float stopY = list.get(i+1).get("y");
            paint.setStrokeWidth(4f);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            paint.setStrokeWidth(0);
        }

        for (int i=0;i<a.length;i++){
            paint.setTextSize(30);
            String str = String.valueOf(a[i]) + "℃";
            paint.getTextBounds(str,0,str.length(),mBounds);
            float x = list.get(i).get("x") - mBounds.left -mBounds.width()/2;
            float y = list.get(i).get("y") - 25f;
            canvas.drawText(str,x,y,paint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);

    }


}
