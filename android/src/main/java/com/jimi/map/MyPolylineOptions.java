package com.jimi.map;


import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/10.
 * 【创建折线覆盖物选项类】
 */
public class MyPolylineOptions {
    PolylineOptions mPolylineOptions = new PolylineOptions();

    /**
     * 设置折线线宽， 默认为 5， 单位：像素
     * @param width
     * @return
     */
    public MyPolylineOptions width(float width) {
        mPolylineOptions.width((int) width);
        return this;
    }

    /**
     * 太简单不说了
     * @param color
     * @return
     */
    public MyPolylineOptions color(int color) {
        mPolylineOptions.color(color);
        return this;
    }

    /**
     * 设置折线坐标点列表
     * @param points
     * @return
     */
    public MyPolylineOptions addAll(List<MyLatLng> points) {
        List<LatLng> points2 = new ArrayList<LatLng>();
        for (MyLatLng vMyLatLng : points ) {
            points2.add(vMyLatLng.mLatLng);
        }
        mPolylineOptions.points(points2);

        return this;
    }

    /**
     * 添加纹理
     * @param bitmapDescriptor
     * @return
     */
    public MyPolylineOptions customTexture (BitmapDescriptor bitmapDescriptor){
        mPolylineOptions.dottedLine(true).width(16).customTexture(bitmapDescriptor);
        return this;
    }
}
