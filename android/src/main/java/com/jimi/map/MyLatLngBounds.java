package com.jimi.map;

import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLngBounds;

/**
 * Created by Administrator on 2015/10/15.
 * 【地理范围数据结构，由西南以及东北坐标点确认】
 */
public class MyLatLngBounds {
    public LatLngBounds mLatLngBounds;

    public MyLatLngBounds(MapStatus mapStatus){
        this.mLatLngBounds = mapStatus.bound;
    }

    public MyLatLngBounds(MyLatLng northeast, MyLatLng southwest){
        mLatLngBounds = new LatLngBounds.Builder().include(northeast.mLatLng).include(southwest.mLatLng).build();
    }

}
