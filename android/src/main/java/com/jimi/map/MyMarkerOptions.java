package com.jimi.map;


import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/10.
 * 【覆盖物描述类】
 */
public class MyMarkerOptions {
    public MarkerOptions mMarkerOptions = new MarkerOptions();

    /**
     * 设置 marker 覆盖物的位置坐标
     * @param position
     * @return
     */
    public MyMarkerOptions position(MyLatLng position) {
        mMarkerOptions.position(position.mLatLng);
        return this;
    }

    /**
     * 设置 Marker 覆盖物的图标
     * （注！相同图案的 icon 的 marker 最好使用同一个 BitmapDescriptor 对象以节省内存空间）
     * @param icon
     * @return
     */
    public MyMarkerOptions icon(MyBitmapDescriptor icon) {
        if(icon != null && icon.mBitmapDescriptor != null) {

            mMarkerOptions.icon(icon.mBitmapDescriptor);
        }
        return this;
    }
    public MyMarkerOptions icons(ArrayList<BitmapDescriptor> iconList) {
        if (iconList != null && !iconList.isEmpty()) {
            mMarkerOptions.icons(iconList).zIndex(0).period(5);
        }
        return this;
    }
}
