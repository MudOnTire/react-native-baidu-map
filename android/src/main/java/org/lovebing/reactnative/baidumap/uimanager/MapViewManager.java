/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.lovebing.reactnative.baidumap.uimanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.text.TextUtils;
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
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
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
        mContent = themedReactContext;
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
    private Marker markerStart;
    private Marker markerEnd;
    private BitmapDescriptor  startIcon;
    private BitmapDescriptor  endIcon;
    private BitmapDescriptor  carIcon;


    @ReactProp(name = "trackPoints")
    public void setTrackPoints(MapView mapView,ReadableArray data) {
        List<LatLng> tracks = LatLngUtil.fromReadableArray(data);
        mPoints = TrackUtils.gpsConversionBaidu(tracks);
        TrackUtils.setAllinVisibleRange(mapView.getMap(),mPoints);
        Log.i("MapView","更新了轨迹点：" + mPoints.size());

        if(mPoints.size() == 0){
            if(markerCar != null){
                markerCar.remove();
                markerCar = null;
            }
            if(markerStart != null){
                markerStart.remove();
                markerStart = null;
            }
            if(markerEnd != null){
                markerEnd.remove();
                markerEnd = null;
            }
        }

        showTrack( mapView, isShowTrack);
    }

    @ReactProp(name = "showTrack")
    public void showTrack(MapView mapView,boolean isShow){
        isShowTrack = isShow;
        if(mPoints.size() < 2){
            if(mLineOverlay != null){
                mLineOverlay.remove();
                mLineOverlay = null;
            }
            return;
        }
        if(isShow){
            if (mLineOverlay == null){
                PolylineOptions ooPolyline = new PolylineOptions().width(8)
                        .color(0xAA50AE6F).points(mPoints);
                mLineOverlay = (Polyline)mapView.getMap().addOverlay(ooPolyline);
            } else {
                mLineOverlay.setPoints(mPoints);
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

        if(mPoints.size() > 2){
            if(markerStart == null){
                MarkerOptions markStart = new MarkerOptions().position(mPoints.get(0)).icon(startIcon);
                markerStart = (Marker) mapView.getMap().addOverlay(markStart);
            } else {
                markerStart.setPosition(mPoints.get(0));
            }
            if (markerEnd == null){
                MarkerOptions markEnd = new MarkerOptions().position(mPoints.get(mPoints.size() - 1)).icon(endIcon);
                markerEnd = (Marker) mapView.getMap().addOverlay(markEnd);
            } else {
                markerEnd.setPosition(mPoints.get(mPoints.size() - 1));
            }
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
            if(carIcon == null){
                Bitmap car = TrackUtils.zoomImg(BitmapFactory.decodeResource(mContent.getResources(), R.drawable.track_car_icon), 60, 60);
                carIcon = BitmapDescriptorFactory.fromBitmap(car);
            }
            MarkerOptions car_icon = new MarkerOptions().position(mPoints.get(0)).icon(carIcon);
            markerCar = (Marker) baiduMap.addOverlay(car_icon);
        }
        markerCar.setPosition(latLng);
        //设置覆盖图标的随着坐标位置方向旋转
        markerCar.setRotate(360 - angle);
        markerCar.setAnchor(0.5f,0.5f);

        List<LatLng> vPointsLine = mPoints.subList(0, progress + 1);
        if (progress == 1 && mLineOverlay2 != null) {
            mLineOverlay2.remove();
            mLineOverlay2 = null;
        }

        if(mLineOverlay != null){
            mLineOverlay.remove();
            mLineOverlay = null;
        }

        if (vPointsLine.size() > 1 && vPointsLine.size() < 10000){
            if (mLineOverlay2 == null){
                PolylineOptions vOoPolyline = new PolylineOptions().width(8)
                        .color(0xAA50AE6F ).points(vPointsLine);
                mLineOverlay2 = (Polyline) baiduMap.addOverlay(vOoPolyline);
            }else {
                mLineOverlay2.setPoints(vPointsLine);
            }
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
        Log.i("showInfoWindows","infoWindows");
        boolean isShow = deviceInfo.getBoolean("isShow");
        if(!isShow){
            mapView.getMap().hideInfoWindow();
            return;
        }
        String status = deviceInfo.getString("status");
        String positionTime = deviceInfo.getString("positionTime");
        String communicationTime = deviceInfo.getString("communicationTime");
        String idelTiem = deviceInfo.getString("idelTiem");//离线或者静止时长
        String speed = deviceInfo.getString("speed");
        String positionType = deviceInfo.getString("positionType");
        String gpsSigna = deviceInfo.getString("gpsSigna");
        String lat = deviceInfo.getString("latitude");
        String lng = deviceInfo.getString("longitude");
        LatLng latLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));

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
        mCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(getBaiduCoorFromGPSCoor(new LatLng(latLng.latitude, latLng.longitude))));
        mCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR){
                    mAddress.setText("");
                } else {
                    //ReverseGeoCodeResult.AddressComponent addressComponent = result.getAddressDetail();
                    mAddress.setText(result.getAddress());
                }
                mLocationTime.setText(positionTime);
                mHTime.setText(communicationTime);
                mSpeedTv.setText(speed + " km/h");
                mLocateTv.setText(positionType);
                String idelTiem = deviceInfo.getString("idelTiem");//离线或者静止时长
                if(TextUtils.isEmpty(idelTiem)){
                    idelTiem = "0";
                }
                String timeStr = formatSeconds(Long.parseLong(idelTiem)/1000);
                String statusText = "";
                if(status.equals("0")){
                    statusText = "离线" + timeStr;
                    mStatusTv.setTextColor(mContent.getResources().getColor(R.color.gray1));
                } else if(status.equals("1")){
                    statusText = "静止" + timeStr;
                    mStatusTv.setTextColor(mContent.getResources().getColor(R.color.red1));
                } else if(status.equals("2")){
                    statusText = "行驶中";
                    mStatusTv.setTextColor(mContent.getResources().getColor(R.color.green1));
                } else if(status.equals("3")){
                    statusText = "在线";
                    mStatusTv.setTextColor(mContent.getResources().getColor(R.color.green1));
                }
                mStatusTv.setText(statusText);
                if(gpsSigna != null){
                    int resId = 0;
                    switch (Integer.parseInt(gpsSigna)){
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
                }

                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(infiView);
                InfoWindow mInfoWindow = new InfoWindow(bitmap, latLng, -100,null);
                mapView.getMap().showInfoWindow(mInfoWindow);
            }
        });


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

    protected LatLng getBaiduCoorFromGPSCoor(LatLng sourceLatLng) {
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;

    }

    /****************************   追踪  *************************/
//    private List<LatLng> mPoints = new ArrayList<>();
//    private Polyline mLineOverlay; //全轨迹
    private Polyline mTraceLine; //追踪轨迹
    private Marker traceMarkerStart;
    private Marker traceMarkerEnd;
    //设置追踪轨迹点集合
    @ReactProp(name = "tracePoints")
    public void setTracePoints(MapView mapView,ReadableArray data){
        List<LatLng> tracks = LatLngUtil.fromReadableArray(data);
        mPoints = TrackUtils.gpsConversionBaidu(tracks);
        if(mPoints.size() == 0){
            traceMarkerStart = null;
            traceMarkerEnd = null;
        }
        Log.i("setTracePoints","追踪--mPoints.size:" + mPoints.size());
        if(startIcon == null){
            Bitmap bitmap = TrackUtils.zoomImg(BitmapFactory.decodeResource(mContent.getResources(), R.drawable.track_icon_start), 60, 60);
            //startIcon = BitmapDescriptorFactory.fromResource(R.drawable.track_icon_start);
            startIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        }
        if(carIcon == null){
            Bitmap bitmap = TrackUtils.zoomImg(BitmapFactory.decodeResource(mContent.getResources(), R.drawable.track_car_icon), 60, 60);
            //endIcon = BitmapDescriptorFactory.fromResource(R.drawable.track_icon_end);
            carIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        }
        if(mPoints.size() >= 1 && traceMarkerStart == null){
            MarkerOptions markStart = new MarkerOptions().position(mPoints.get(0)).icon(startIcon);
            traceMarkerStart = (Marker) mapView.getMap().addOverlay(markStart);
        }
        if(mPoints.size() == 1){
            mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(mPoints.get(0)));
        }
        if(mPoints.size() < 2)
            return;

        BaiduMap baiduMap = mapView.getMap();
        if (mTraceLine == null){
            PolylineOptions ooPolyline = new PolylineOptions().width(8)
                    .color(0xAA50AE6F).points(mPoints);
            mTraceLine = (Polyline)mapView.getMap().addOverlay(ooPolyline);
        } else {
            mTraceLine.setPoints(tracks);
        }

        if(mPoints.size() > 2 && traceMarkerEnd == null){
            MarkerOptions markEnd = new MarkerOptions().position(mPoints.get(mPoints.size() - 1)).icon(endIcon);
            traceMarkerEnd = (Marker) mapView.getMap().addOverlay(markEnd);
        } else {
            traceMarkerStart.setPosition(mPoints.get(0));
            traceMarkerEnd.setPosition(mPoints.get(mPoints.size() - 1));
        }
        LatLng latLng = mPoints.get(mPoints.size() - 1);
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


}


