package com.jimi.map;

import android.graphics.Point;

import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2015/8/11.
 *
 *   该接口用来在屏幕像素x/y坐标系和地球经纬度坐标系之间进行转换，
 *     通过 MapView.getProjection()来取得映射类。
 */
public class MyProjection {
    public Projection mProjection;

    /**
     * 将经纬度转换为屏幕的点坐标
     * @param pMyLatLng
     * @return
     */
    public Point toScreenLocation(MyLatLng pMyLatLng) {
        return mProjection.toScreenLocation(pMyLatLng.mLatLng);
    }

    /**
     * 将转屏幕的点坐标换为经纬度
     * @param pPoint
     * @return
     */
    public MyLatLng fromScreenLocation(Point pPoint){
        LatLng vLatLng =  mProjection.fromScreenLocation(pPoint);
        return new MyLatLng(vLatLng.latitude, vLatLng.longitude);
    }

}
