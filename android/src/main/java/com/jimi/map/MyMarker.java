package com.jimi.map;


import android.os.Bundle;

import com.baidu.mapapi.map.Marker;

/**
 * Created by Administrator on 2015/8/10.
 * 【地图标记类】
 */
public class MyMarker {
    public Marker mMarker;
    public MyLatLng mMyLatLng;

    public void remove() {
        mMarker.remove();
    }

    public void showInfoWindow(){
    }

    public void hideInfoWindow(){

    }

    public void setIcon(MyBitmapDescriptor pIcon){
        mMarker.setIcon(pIcon.mBitmapDescriptor);
    }

    public void setPosition(MyLatLng pPosition) {
        mMarker.setPosition(pPosition.mLatLng);
        mMyLatLng = pPosition;
    }

    public boolean isVisible() {
        return mMarker.isVisible();
    }

    /**
     * 便于设别 Marker 所属的设备
     * @param title
     */
    public void setTitle(String title) {
        mMarker.setTitle(title);
    }

    public String getTitle() {
        return  mMarker.getTitle();
    }


    //设置车头的方向
    public void setRotation(int pRotation){
        mMarker.setRotate(360 - pRotation);
        mMarker.setAnchor(0.5f,0.5f);
    }

    public void setExtraInfo(Bundle bundle){
        mMarker.setExtraInfo(bundle);
    }
    public Bundle getExtraInfo(){
        return mMarker.getExtraInfo();
    }

    /**
     * 设置marker的透明度
     * @param alpha
     */
    public void setAlpha(float alpha){
        mMarker.setAlpha(alpha);

    }

    /**
     * 设置marker在最顶层
     */
    public void setToTop() {
        mMarker.setZIndex(10000);
    }

    /**
     * 设置marker在最下层
     */
    public void removeToTop(){
        mMarker.setZIndex(100);
    }
}
