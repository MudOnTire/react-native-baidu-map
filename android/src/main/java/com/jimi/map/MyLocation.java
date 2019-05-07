package com.jimi.map;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.jimi.map.listener.OnLocationListener;

/**
 * Created by Administrator on 2015/8/11.
 * 【定位服务的客户端】
 */
public class MyLocation {
    public LocationClient mLocationClient;
    public Context mContext;
    public BDLocationListener myListener = new MyLocationListener();
    OnLocationListener mOnLocationListener;//定位成功回调接口

    private LocationManager locationManager;
    private PhoneLocationListener phoneLocationListener;


    public MyLocation(Context pContext, OnLocationListener pOnLocationListener) {
        mOnLocationListener = pOnLocationListener;
        this.mContext = pContext.getApplicationContext();
    }

    public MyLocation(Context pContext) {
        this.mContext = pContext.getApplicationContext();
    }

    // 百度定位
    public void onLocation(String type) {
        mLocationClient = new LocationClient(this.mContext); // 声明LocationClient类
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType(type);// 返回的定位结果是百度经纬度,默认值使用gcj02（国测局加密坐标）
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(myListener); // 注册监听函数
        mLocationClient.start();
        if (mLocationClient != null) {
            mLocationClient.requestLocation();
        }
    }

    /**
     * 无参数
     */
    public void onLocation2() {
        mLocationClient = new LocationClient(this.mContext); // 声明LocationClient类
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值使用gcj02（国测局加密坐标）
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(myListener); // 注册监听函数
        mLocationClient.start();
        if (mLocationClient != null) {
            mLocationClient.requestLocation();
        }
    }

    /**
     * 用于监听本机定位结果
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.e("Mylocation", "location.getLocType():" + location.getLocType());

            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            sb.append("\nCity : ");
            sb.append(location.getCity());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }
            if (161 == location.getLocType() || 61 == location.getLocType()) {
                MyLatLng vLatLng = new MyLatLng(location.getLatitude(),
                        location.getLongitude());
                /** 获取到定位结果后设置回调给实现类 */
                if (mOnLocationListener != null)
                    mOnLocationListener.onLocationResult(vLatLng, location.getRadius(), location.getSpeed());
                if (mOnLocationListener != null)
                    mOnLocationListener.onLocationResult2(vLatLng, location.getAddrStr());
                if (mOnLocationListener != null)
                    mOnLocationListener.onAddress(location.getAddress());
                Log.e("Mylocation", location.getAddrStr() + ",");
                mLocationClient.stop();
                mLocationClient.unRegisterLocationListener(myListener);
            }
        }
    }

    //手机原生定位
    public void onLocation3(){
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        phoneLocationListener = new PhoneLocationListener();
        locationManager.requestLocationUpdates("network", 0, 0, phoneLocationListener);
    }

    public class PhoneLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            if (mOnLocationListener != null)
                mOnLocationListener.onLocationResult(new MyLatLng(latitude,longitude), 0, 0);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
