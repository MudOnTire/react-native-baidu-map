/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.lovebing.reactnative.baidumap.uimanager;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import org.json.JSONException;
import org.json.JSONObject;
import org.lovebing.reactnative.baidumap.R;
import org.lovebing.reactnative.baidumap.listener.MapListener;
import org.lovebing.reactnative.baidumap.util.LatLngUtil;
import org.lovebing.reactnative.baidumap.util.TrackUtils;
import org.lovebing.reactnative.baidumap.view.OverlayView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

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
        hiddenScaleAndlogoView(mapView);
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

    @ReactProp(name = "overlookEnabled")
    public void setOverlookEnabled(MapView mapView, boolean overlookEnabled){
        UiSettings settings=mapView.getMap().getUiSettings();
        settings.setOverlookingGesturesEnabled(false);
    }

    /****************************************   轨迹  *********************************************/


    @ReactProp(name = "visualRange")
    public void setVisualRange(MapView mapView,ReadableArray data) {
        List<LatLng> mPoints = LatLngUtil.fromReadableArray(data);
        //mPoints = TrackUtils.gpsConversionBaidu(tracks);
        TrackUtils.setAllinVisibleRange(mapView.getMap(),mPoints);
        Log.i("MapView","更新了轨迹点：" + mPoints.size());
    }


    @ReactProp(name = "trackPlayInfo")
    public void setTrackPlayInfo(MapView mapView, ReadableMap position){
        if(position != null){
            LatLng latLng = LatLngUtil.fromReadableMap(position);
            Projection projection = mapView.getMap().getProjection();
            if(projection != null){
                //将经纬度转换为屏幕的点坐标
                Point vPoint = projection.toScreenLocation(latLng);
                //如果在地图外面就更新当前点为地图中心
                if (isOutScreen(mapView, vPoint)){
                    mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
                }
            }
        }
    }

    @ReactProp(name = "correctPerspective")
    public void correctPerspective(MapView mapView,ReadableMap position){
        Log.i("MapView","correctPerspective：");
        if(position != null){
            LatLng latLng = LatLngUtil.fromReadableMap(position);
            Log.i("MapView","correctPerspective---latLng.latitude:" + latLng.latitude + " ,latLng.longitude:" + latLng.longitude);
            Projection projection = mapView.getMap().getProjection();
            if(projection != null){
                //将经纬度转换为屏幕的点坐标
                Point vPoint = projection.toScreenLocation(latLng);
                //如果在地图外面就更新当前点为地图中心
                if (isOutScreen(mapView, vPoint)){
                    mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng));
                }
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
        String gpsNum = deviceInfo.getString("gpsNum");
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
        String QUERYADDRESS = "http://poi.jimicloud.com/poi?_method_=geocoderForBaiDu&latlng=%s,%s&token=3500307a93c6fc335efa71f60438b465&language=%s";
        final String path = String.format(QUERYADDRESS, latLng.latitude, latLng.longitude, Locale.getDefault().getLanguage());
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {
                URL url = null;
                try {
                    url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        String address;
                        InputStream is = conn.getInputStream();
                        JSONObject json = new JSONObject(getTextFromStream(is));
                        int code = json.getInt("code");
                        if (code == 0) {
                            address = json.getString("msg");
                            if (address != null) {
                                return address;
                            }
                        }
                    }
                } catch (MalformedURLException pE) {
                    pE.printStackTrace();
                } catch (IOException pE) {
                    pE.printStackTrace();
                } catch (JSONException pE) {
                    pE.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s != null) {
                    Log.e("address", s);
                    mAddress.setText(s);
                    mLocationTime.setText(positionTime);
                    mHTime.setText(communicationTime);
                    mSpeedTv.setText(speed + " km/h");
                    String gpsinfo = "";
                    if(positionType.equals("GPS")){
                        gpsinfo = "GPS定位";
                        gpsinfo = TextUtils.isEmpty(gpsNum) ? gpsinfo : gpsinfo + ":" + gpsNum;
                    } else if(positionType.equals("LBS")){
                        gpsinfo = "基站定位";
                    } else if(positionType.equals("WIFI")){
                        gpsinfo = "wifi定位";
                    }
                    mLocateTv.setText(gpsinfo);
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

            }

        }.execute("");

    }

    private String getTextFromStream(InputStream is) {
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();
        int len = 0;
        try {
            while ((len = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            return sb.toString();
        } catch (IOException pE) {
            pE.printStackTrace();
        }
        return sb.toString();
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

    /**
     * 不显示地图比例尺和logo
     */
    public void hiddenScaleAndlogoView(MapView mapView) {
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }

        // 不显示地图上比例尺
        mapView.showScaleControl(false);
    }

}


