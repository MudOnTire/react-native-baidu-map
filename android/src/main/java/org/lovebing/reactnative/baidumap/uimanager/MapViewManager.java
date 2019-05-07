/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.lovebing.reactnative.baidumap.uimanager;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import org.lovebing.reactnative.baidumap.R;
import org.lovebing.reactnative.baidumap.listener.MapListener;
import org.lovebing.reactnative.baidumap.util.LatLngUtil;
import org.lovebing.reactnative.baidumap.util.TrackUtils;
import org.lovebing.reactnative.baidumap.view.OverlayView;

import java.util.ArrayList;
import java.util.List;

public class MapViewManager extends ViewGroupManager<MapView> {

    private Context mContent;

    @Override
    public String getName() {
        return "BaiduMapView";
    }

    @Override
    public void addView(MapView parent, View child, int index) {
        ViewGroup p = (ViewGroup) child.getParent();
        if (p != null) {
            //p.removeView(child);
        }
        if (child instanceof OverlayView) {
            ((OverlayView) child).addTopMap(parent.getMap());
        }
    }

    @Override
    protected MapView createViewInstance(ThemedReactContext themedReactContext) {
        MapView mapView =  new MapView(themedReactContext);
        BaiduMap map = mapView.getMap();
        MapListener listener = new MapListener(mapView, themedReactContext);
        map.setOnMapStatusChangeListener(listener);
        map.setOnMapLoadedCallback(listener);
        map.setOnMapClickListener(listener);
        map.setOnMapDoubleClickListener(listener);
        map.setOnMarkerClickListener(listener);
        return mapView;
    }

    @ReactProp(name = "zoomControlsVisible")
    public void setZoomControlsVisible(MapView mapView, boolean zoomControlsVisible) {
        mapView.showZoomControls(zoomControlsVisible);
    }

    @ReactProp(name="trafficEnabled")
    public void setTrafficEnabled(MapView mapView, boolean trafficEnabled) {
        mapView.getMap().setTrafficEnabled(trafficEnabled);
    }

    @ReactProp(name="baiduHeatMapEnabled")
    public void setBaiduHeatMapEnabled(MapView mapView, boolean baiduHeatMapEnabled) {
        mapView.getMap().setBaiduHeatMapEnabled(baiduHeatMapEnabled);
    }

    @ReactProp(name = "mapType")
    public void setMapType(MapView mapView, int mapType) {
        mapView.getMap().setMapType(mapType);
    }

    @ReactProp(name="zoom")
    public void setZoom(MapView mapView, float zoom) {
        MapStatus mapStatus = new MapStatus.Builder().zoom(zoom).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mapView.getMap().setMapStatus(mapStatusUpdate);
    }

    @ReactProp(name="center")
    public void setCenter(MapView mapView, ReadableMap position) {
        if(position != null) {
            LatLng point = LatLngUtil.fromReadableMap(position);
            MapStatus mapStatus = new MapStatus.Builder()
                    .target(point)
                    .build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            mapView.getMap().setMapStatus(mapStatusUpdate);
        }
    }

    /****************************************   轨迹  *********************************************/
    private List<LatLng> mPoints = new ArrayList<>();
    private Polyline mLineOverlay; //全轨迹
    private Polyline mLineOverlay2; //边走边画轨迹
    private boolean isShowTrack = false;
    private Marker markerCar;
    private BitmapDescriptor  startIcon;
    private BitmapDescriptor  endIcon;
    private BitmapDescriptor  stopIcon;


    @ReactProp(name = "trackPoints")
    public void setTrackPoints(MapView mapView,ReadableArray data) {
        List<LatLng> tracks = LatLngUtil.fromReadableArray(data);
        mPoints = TrackUtils.gpsConversionBaidu(tracks);
        TrackUtils.setAllinVisibleRange(mapView.getMap(),mPoints);
        Log.i("MapView","更新了轨迹点：" + mPoints.size());
        showTrack( mapView, isShowTrack);
    }

    @ReactProp(name = "showTrack")
    public void showTrack(MapView mapView,boolean isShow){
        isShowTrack = isShow;
//        if(isShowTrack == isShow){
//            return;
//        }
        if(mPoints.size() < 2)
            return;
//        isShowTrack = isShow;
        if(isShow){
            if (mLineOverlay == null){
                PolylineOptions ooPolyline = new PolylineOptions().width(8)
                        .color(0xAA50AE6F).points(mPoints);
                mLineOverlay = (Polyline)mapView.getMap().addOverlay(ooPolyline);
            } else {
                mLineOverlay.setVisible(true);
            }
            Log.i("MapView","显示了轨迹");
        } else {
            if (mLineOverlay != null) {
                mLineOverlay.setVisible(false);
            }
            Log.i("MapView","隐藏了轨迹");
        }

        if(startIcon == null){
            startIcon = BitmapDescriptorFactory.fromResource(R.drawable.track_icon_start);
        }
        if(endIcon == null){
            endIcon = BitmapDescriptorFactory.fromResource(R.drawable.track_icon_end);
        }
//        if(stopIcon == null){
//            stopIcon = BitmapDescriptorFactory.fromResource(R.drawable.device_marker_stop);
//        }

        if(mPoints.size() > 2){
            MarkerOptions markStart = new MarkerOptions().position(mPoints.get(0)).icon(startIcon);
            MarkerOptions markEnd = new MarkerOptions().position(mPoints.get(mPoints.size() - 1)).icon(endIcon);
            mapView.getMap().addOverlay(markStart);
            mapView.getMap().addOverlay(markEnd);
        }

    }

    @ReactProp(name = "trackPlayInfo")
    public void setTrackPlayInfo(MapView mapView, ReadableMap playInfo){
        if(playInfo == null)
            return;
        int progress = playInfo.getInt("progress");
        int angle = playInfo.getInt("angle");//汽车图标的位置方向
        if(mPoints.size() <=  progress){
            return;
        }

        BaiduMap baiduMap = mapView.getMap();
        LatLng latLng = mPoints.get(progress);
        if(markerCar == null){
            BitmapDescriptor car = BitmapDescriptorFactory.fromResource(R.drawable.track_car_icon);
            MarkerOptions car_icon = new MarkerOptions().position(mPoints.get(0)).icon(car);
            markerCar = (Marker) baiduMap.addOverlay(car_icon);
        }
        markerCar.setPosition(latLng);
        //设置覆盖图标的随着坐标位置方向旋转
        markerCar.setRotate(360 - angle);
        markerCar.setAnchor(0.5f,0.5f);

        List<LatLng> vPointsLine = mPoints.subList(0, progress + 1);
        if (vPointsLine.size() > 1 && vPointsLine.size() < 10000){
            if (mLineOverlay2 == null){
                PolylineOptions vOoPolyline = new PolylineOptions().width(8)
                        .color(0xAA50AE6F ).points(vPointsLine);
                mLineOverlay2 = (Polyline) baiduMap.addOverlay(vOoPolyline);
            }else {
                mLineOverlay2.setPoints(vPointsLine);
            }
        }

        if (vPointsLine.size() == 1 && mLineOverlay2 != null) {
            mLineOverlay2.remove();
            mLineOverlay2 = null;
        }
        if(mLineOverlay != null){
            mLineOverlay.remove();
            mLineOverlay = null;
        }
        Projection projection = baiduMap.getProjection();
        if(projection != null){
            //将经纬度转换为屏幕的点坐标
            Point vPoint = projection.toScreenLocation(latLng);
            //如果在地图外面就更新当前点为地图中心
            if (isOutScreen(mapView, vPoint)){
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
            }
        }

    }

    /**
     * 车辆s是否显示在MapView中
     *
     * @param pPoint
     * @return
     */
    private boolean isOutScreen(MapView mapView, Point pPoint) {
        if (pPoint.x < 0 || pPoint.y < 0 || pPoint.x > mapView.getWidth() || pPoint.y > mapView
                .getHeight()) {
            return true;
        }
        return false;
    }

    private View infiView;
    TextView mAddress;
    TextView mStatusTv;
    TextView mLocationTime;
    TextView mHTime;
    TextView mSpeedTv;
    TextView mLocateTv;
    ImageButton mGpsSignal;
    GeoCoder mCoder;
    @ReactProp(name="infoWindows")
    public void showInfoWindows(MapView mapView,ReadableMap deviceInfo){
        String address = deviceInfo.getString("address");
        String status = deviceInfo.getString("status");
        String positionTime = deviceInfo.getString("positionTime");
        String communicationTime = deviceInfo.getString("communicationTime");
        String idelTiem = deviceInfo.getString("idelTiem");//离线或者静止时长
        String speed = deviceInfo.getString("speed");
        String positionType = deviceInfo.getString("positionType");
        int gpsSigna = deviceInfo.getInt("gpsSigna");
        double lat = deviceInfo.getDouble("latitude");
        double lng = deviceInfo.getDouble("longitude");
        LatLng latLng = new LatLng(lat,lng);
        boolean isShow = deviceInfo.getBoolean("isShow");
        if(infiView == null){
            infiView = LayoutInflater.from(mContent).inflate(R.layout.mapui_location_popu_layout, null, false);
            mAddress = (TextView) infiView.findViewById(R.id.tuqiang_tv_addres);
            mStatusTv = (TextView) infiView.findViewById(R.id.tuqiang_tv_status);//设备状态
            mLocationTime = (TextView) infiView.findViewById(R.id.tuqiang_tv_ltime); //定位时间
            mHTime = (TextView) infiView.findViewById(R.id.tuqiang_tv_htime); //通讯时间
            mSpeedTv = (TextView) infiView.findViewById(R.id.tuqiang_tv_speed); //速度
            mLocateTv = (TextView) infiView.findViewById(R.id.tuqiang_tv_loacte); //卫星定位
            mGpsSignal = (ImageButton) infiView.findViewById(R.id.elderly_fl_gpsSignal); //卫星信号
        }
        mCoder = GeoCoder.newInstance();
        if(mCoder != null) {
            mCoder.destroy();
        }
        mCoder = GeoCoder.newInstance();
        mCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR){
                    mAddress.setText("");
                } else {
                    mAddress.setText(result.getAddress());
                }
            }
        });

        mLocationTime.setText(positionTime);
        mHTime.setText(communicationTime);
        mSpeedTv.setText(speed + " km/h");
        mLocateTv.setText(positionType);
        String timeStr = formatSeconds(Long.parseLong(idelTiem)/1000);
        String statusText = "";
        if(status.equals("0")){
            statusText = "离线" + timeStr;
        } else if(status.equals("1")){
            statusText = "静止" + timeStr;
        } else if(status.equals("2")){
            statusText = "行驶中";
        } else if(status.equals("3")){
            statusText = "在线";
        }
        mStatusTv.setText(statusText);
        int resId = 0;
        switch (gpsSigna){
            case 0:
                resId = R.drawable.frame_gps_signal_0;
                break;
            case 1:
                resId = R.drawable.frame_gps_signal_1;
                break;
            case 2:
                resId = R.drawable.frame_gps_signal_2;
                break;
            case 3:
                resId = R.drawable.frame_gps_signal_3;
                break;
            case 4:
                resId = R.drawable.frame_gps_signal_5;
                break;
        }
        mGpsSignal.setImageResource(resId);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(infiView);
        InfoWindow mInfoWindow = new InfoWindow(bitmap, latLng, -100,null);
        mapView.getMap().showInfoWindow(mInfoWindow);
    }

    public String formatSeconds(long seconds) {
        String timeStr = 1 + "分钟";
        if (seconds > 60) {
            long second = seconds % 60;
            long min = seconds / 60;
            timeStr = min + "分";
            if (min > 60) {
                min = (seconds / 60) % 60;
                long hour = (seconds / 60) / 60;
                timeStr = hour + "小时" + min + "分";
                if (hour > 24) {
                    hour = ((seconds / 60) / 60) % 24;
                    long day = (((seconds / 60) / 60) / 24);
                    timeStr = day + "天" + hour + "小时" ;
                }
            }
        }
        return timeStr;
    }

    /****************************   追踪  *************************/
//    private List<LatLng> mPoints = new ArrayList<>();
//    private Polyline mLineOverlay; //全轨迹
    //设置追踪轨迹点集合
    @ReactProp(name = "tracePoints")
    public void setTracePoints(MapView mapView,ReadableArray data){
        List<LatLng> tracks = LatLngUtil.fromReadableArray(data);
        mPoints = TrackUtils.gpsConversionBaidu(tracks);
    }


}


