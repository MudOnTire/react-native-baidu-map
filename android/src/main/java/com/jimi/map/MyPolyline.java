package com.jimi.map;

import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/8/11.
 * 【矢量制图工具】
 *    用于创建折线覆盖物对象，如：绘制行车轨迹
 */
public class MyPolyline {
    Polyline mPolyline;
    public void remove() {
        mPolyline.remove();
    }

    /**
     * 设置绘制折线的坐标点
     * @param points
     */
    public void setPoints(List<MyLatLng> points) {
        List<LatLng> vLatLng = new ArrayList<LatLng>();
        for (MyLatLng vMyLatLng : points) {
            vLatLng.add(vMyLatLng.mLatLng);
        }
        mPolyline.setPoints(vLatLng);
    }

    public boolean isVisible() {
        return mPolyline.isVisible();
    }

    public void setVisible(boolean isVisible) {
        mPolyline.setVisible(isVisible);
    }
}
