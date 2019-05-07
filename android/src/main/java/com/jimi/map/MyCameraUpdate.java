package com.jimi.map;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;

/**
 * Created by Administrator on 2015/8/10.
 *
 * 【描述地图状态将要发生的变化】
 */
public class MyCameraUpdate {
    MapStatusUpdate mCameraUpdate;

    /**
     * 设置地图新中心点
     * @param latLng
     * @return
     */
    public MyCameraUpdate newLatLng(MyLatLng latLng) {
        mCameraUpdate = MapStatusUpdateFactory.newLatLng(latLng.mLatLng);
        return this ;
    }

    //设置地图缩放级别
    public MyCameraUpdate zoomTo(float zoom) {
        mCameraUpdate = MapStatusUpdateFactory.zoomTo(zoom);
        return this;
    }

    /**
     * 设置地图中心点以及缩放级别zoom
     * @param latLng
     * @param zoom
     * @return
     */
    public MyCameraUpdate newLatLngZoom(MyLatLng latLng, float zoom) {
        mCameraUpdate =  MapStatusUpdateFactory.newLatLngZoom(latLng.mLatLng, zoom);
        return this;
    }

    //放大地图缩放级别
    public MyCameraUpdate zoomOut() {
        mCameraUpdate = MapStatusUpdateFactory.zoomOut();
        return this;
    }

    //缩小地图缩放级别
    public MyCameraUpdate zoomIn() {
        mCameraUpdate =  MapStatusUpdateFactory.zoomIn();
        return this;
    }

    /**
     * 设置显示在屏幕中的地图地理范围
     * @param pMyLatLngBounds
     * @return
     */
    public MyCameraUpdate newLatLngBounds(MyLatLngBounds pMyLatLngBounds) {
        mCameraUpdate =  MapStatusUpdateFactory.newLatLngBounds(pMyLatLngBounds.mLatLngBounds);
        return this;
    }
}
