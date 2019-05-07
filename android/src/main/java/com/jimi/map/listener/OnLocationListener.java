package com.jimi.map.listener;


import com.baidu.location.Address;
import com.jimi.map.MyLatLng;

/**
 * Created by Administrator on 2015/8/11.
 * 【自定义手机定位结果回调接口】
 */
public interface OnLocationListener {
    public void onLocationResult(MyLatLng pMyLatLng, float radius, float speed);

    public void onLocationResult2(MyLatLng pMyLatLng, String pAddress);

    public void onAddress(Address address);
}
