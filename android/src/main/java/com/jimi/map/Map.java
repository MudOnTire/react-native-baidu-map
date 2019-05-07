package com.jimi.map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.lbsapi.model.BaiduPanoData;
import com.baidu.lbsapi.panoramaview.PanoramaRequest;
import com.baidu.lbsapi.panoramaview.PanoramaView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNRouteGuideManager;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviBaseCallbackModel;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviCommonModule;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.NaviModuleFactory;
import com.baidu.navisdk.adapter.NaviModuleImpl;
import com.jimi.map.listener.OnCameraChangeListener;
import com.jimi.map.listener.OnGetAddressCallback;
import com.jimi.map.listener.OnInfoWindowClickListener;
import com.jimi.map.listener.OnJumpToNavigatorListener;
import com.jimi.map.listener.OnLocationListener;
import com.jimi.map.listener.OnMapLoadedCallback;
import com.jimi.map.listener.OnMapReadyCallback;
import com.jimi.map.listener.OnMapStatusChangeCallBack;
import com.jimi.map.listener.OnMarkerClickListener;
import com.jimi.map.listener.OnMyNaviInitListener;
import com.jimi.map.listener.OnPanoramaReadyCallback;
import com.jimi.map.listener.OnRoutePlanCallback;
import com.jimi.map.listener.OnSearchResultListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.microedition.khronos.opengles.GL10;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Administrator on 2015/8/10.
 */
public class Map {

    public static final String MAP_MARKER_ID = "marker";
    public static final int CAR_MAP_MARKER_ID = 0;
    public static final int PHONE_MAP_MARKER_ID = 1;
    public static final int TRACKCAR_MAP_MARKER_ID = 2;
    public String locationString = "";
    /**
     * MapView 是地图主控件
     */
    public MapView mMapView;
    private BaiduMap mBaiduMap;
    private UiSettings mUiSettings;
    /**
     * 地图街景控件
     */
    private PanoramaView mPanoramaView;
    private PanoramaRequest mPanoramaRequest;

    //普通地图
    public static final int MAP_TYPE_NORMAL = 1;
    //卫星地图
    public static final int MAP_TYPE_SATELLITE = 2;

    Activity mActivity;

    OnMapReadyCallback mOnMapReadyCallback;
    InfoWindow mWindow;
    OnMapLoadedCallback mOnMapLoadedCallback;

    private static GeoCoder mSearch;
    private MyCircleOverlay mCircle;
    private OnMapStatusChangeCallBack mOnMapStatusChangeCallBack;
    private OnLocationListener mOnLocationListener;

    /**
     * 初始化得到map
     *
     * @param pContext
     * @param pMapView
     * @return
     */
    public View getMap(Activity pContext, View pMapView, Bundle savedInstanceState) {
        mActivity = pContext;
        mMapView = (MapView) pMapView;
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setRotateGesturesEnabled(false);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mBaiduMap.hideInfoWindow();

            }
        });
        return mMapView;
    }

    /**
     * 初始化得到PanoramaView
     *
     * @param pContext
     * @param mParentView
     * @return
     */
    public void initPanorama(Activity pContext, View mParentView,Bundle savedInstanceState) {
        mActivity = pContext;
        this.mPanoramaRequest = PanoramaRequest.getInstance(pContext);
        this.mPanoramaView = (PanoramaView) mParentView;
    }

    /**
     * 街景初始化完成回调
     *
     * @param pOnPanoramaReadyCallback
     */
    public void setOnPanoramaReadyCallback(OnPanoramaReadyCallback pOnPanoramaReadyCallback) {
        mPanoramaView.setPanoramaViewListener(pOnPanoramaReadyCallback);
    }

    /**
     * 展示全景
     */
    public void showPanoramaView() {
        if (mPanoramaView != null) {
            mPanoramaView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏全景
     */
    public void hintPanoramaView() {
        if (mPanoramaView.getParent() != null) {
            mPanoramaView.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是否有全景
     *
     * @param pMyLatlng
     * @return
     */
    public boolean isHavePanorama(MyLatLng pMyLatlng) {
        BaiduPanoData vBaiduPanoData = mPanoramaRequest.getPanoramaInfoByLatLon(pMyLatlng.longitude, pMyLatlng.latitude);
        if (vBaiduPanoData.getErrorCode() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 显示街景
     *
     * @param lon
     * @param lat
     */
    public void showPanorama(double lon, double lat) {
        mPanoramaView.setPanorama(lon, lat);
    }

    /**
     * 隐藏windowinfo
     */
    public void hideInfoWindow() {
        mBaiduMap.hideInfoWindow();
    }

    /**
     * 隐藏地图缩放控件
     */
    public void hideZoomControls() {
        mMapView.showZoomControls(false);
    }

    /**
     * 获取地图显示样式 普通/卫星
     *
     * @return
     */
    public final int getMapType() {
        return mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_SATELLITE ? MAP_TYPE_SATELLITE :
                MAP_TYPE_NORMAL;
    }

    /**
     * 设置地图显示样式 普通/卫星
     *
     * @param pType
     */
    public final void setMapType(int pType) {
        if (pType == MAP_TYPE_SATELLITE) {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        } else if (pType == MAP_TYPE_NORMAL) {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }

    }

    /**
     * 打开实时路况
     */
    public void openTmc() {
        mBaiduMap.setTrafficEnabled(true);
    }

    /**
     * 关闭实时路况
     */
    public void closeTmc() {
        mBaiduMap.setTrafficEnabled(false);
    }

    /**
     * 初始化
     *
     * @param pContext
     */
    public static void init(Context pContext) {
        SDKInitializer.initialize(pContext);
        initEngineManager(pContext);
    }

    public static void initEngineManager(Context context) {

        if (!new BMapManager(context).init(new MyGeneralListener())) {
            Log.d("ljx", "BMapManager  初始化错误!");
        }

    }

    public void put(String encodingType, String s) {
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetPermissionState(int iError) {
            // 非零值表示key验证未通过
            if (iError != 0) {
                // 授权Key错误：
                Log.d("ljx", "请在AndoridManifest.xml中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError);
            } else {
                Log.d("ljx", "key认证成功");
            }
        }
    }

    /**
     * 放大
     */
    public void zoomIn() {
        if (mBaiduMap != null)
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomIn());
    }

    /**
     * 缩小
     */
    public void zoomOut() {
        if (mBaiduMap != null)
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.zoomOut());
    }

    /**
     * 获得缩放级别
     *
     * @return
     */
    public float getZoom() {
        return mBaiduMap.getMapStatus().zoom;
    }

    /**
     * 地图初始准备好了回掉
     *
     * @param pOnMapReadyCallback
     */
    public void setOnMapReadyCallback(OnMapReadyCallback pOnMapReadyCallback) {
        this.mOnMapReadyCallback = pOnMapReadyCallback;
        if (mOnMapReadyCallback != null) mOnMapReadyCallback.onMapReady();
    }

    /**
     * Map是否为空
     *
     * @return
     */
    public boolean isNull() {
        return mBaiduMap == null;
    }

    /**
     * 清理地图
     */
    public void clear() {
        mBaiduMap.clear();
        mMarkerMobile = null;
        if (mBDLocation != null && mIsShowPhone) {
            phoneOverlay();
        }
    }

    /**
     * 得到最大缩放级别
     *
     * @return
     */
    public float getMaxZoomLevel() {

        return mBaiduMap.getMaxZoomLevel();
    }

    /**
     * 得到最小缩放级别
     *
     * @return
     */
    public final float getMinZoomLevel() {
        return mBaiduMap.getMinZoomLevel();
    }

    /**
     * 更新地图不带动画效果
     *
     * @param pMyCameraUpdate
     */
    public void moveCamera(MyCameraUpdate pMyCameraUpdate) {
        mBaiduMap.setMapStatus(pMyCameraUpdate.mCameraUpdate);
    }

    /**
     * 添加
     *
     * @param pMyMarkerOptions
     * @return
     */
    public MyMarker addMarker(MyMarkerOptions pMyMarkerOptions) {
        MyMarker vMyMarker = new MyMarker();
        if (pMyMarkerOptions.mMarkerOptions.getIcon() != null) {

            vMyMarker.mMarker = (Marker) mBaiduMap.addOverlay(pMyMarkerOptions.mMarkerOptions);
        } else {
            return null;
        }

        return vMyMarker;
    }
    /**
     * 添加
     * gif mark 动画
     *
     * @param pMyMarkerOptions
     * @return
     */

    public MyMarker animateMarker(MyMarkerOptions pMyMarkerOptions) {
        MyMarker vMyMarker = new MyMarker();
        if (!pMyMarkerOptions.mMarkerOptions.getIcons().isEmpty()) {

            vMyMarker.mMarker = (Marker) mBaiduMap.addOverlay(pMyMarkerOptions.mMarkerOptions);
        } else {
            return null;
        }

        return vMyMarker;
    }

    /**
     * 设置最大缩放级别和最小缩放级别
     *
     * @param max 最大缩放级别
     * @param min 最小缩放级别
     */
    public final void setMaxAndMinZoomLevel(float max, float min) {
        mBaiduMap.setMaxAndMinZoomLevel(max, min);
    }


    /**
     * 更新地图带动画效果
     *
     * @param update
     */
    public void animateCamera(MyCameraUpdate update) {
//        mBaiduMap.animateMapStatus(update.mCameraUpdate, 20);
        if (update.mCameraUpdate == null)return;
        mBaiduMap.animateMapStatus(update.mCameraUpdate);
    }

    /**
     * 在给定位置绘制半径多大的圆
     *
     * @param pLatlng ：坐标位置
     */
    public MyCircleOverlay drawCircle(MyLatLng pLatlng) {
        //1.创建自己
        CircleOptions circleOptions = new CircleOptions();
        //2.给自己设置数据
        circleOptions.center(pLatlng.mLatLng) //圆心
                .radius(500)//半径 单位米
                .fillColor(0x22606060)//填充色
                .stroke(new Stroke(2, 0x33606060));//边框宽度和颜色

        //3.把覆盖物添加到地图中
        mCircle = new MyCircleOverlay();
        mCircle.setmCircle(mBaiduMap.addOverlay(circleOptions));
        return mCircle;
    }

    /**
     * 添加线条覆盖物
     *
     * @param options
     * @return
     */
    public MyPolyline addPolyline(MyPolylineOptions options) {
        MyPolyline vMyPolyline = new MyPolyline();
        vMyPolyline.mPolyline = (Polyline) mBaiduMap.addOverlay(options.mPolylineOptions);
        return vMyPolyline;
    }

    /**
     * 显示windowinfo
     *
     * @param pLatLng
     */
    public void showWindowInfo(MyLatLng pLatLng, View pWindowView, int excursion) {
        if (pLatLng == null || pLatLng.mLatLng == null|| pWindowView == null) return;
        mWindow = new InfoWindow(pWindowView, pLatLng.mLatLng, excursion);
        mBaiduMap.showInfoWindow(mWindow);
    }

    public void onPause() {
        if (mMapView != null) mMapView.onPause();
        if (mPanoramaView != null) mPanoramaView.onPause();
        if (myOrientationListener != null) myOrientationListener.stop();
        if (mBaiduNaviCommonModule != null) {
            mBaiduNaviCommonModule.onPause();
        }
    }

    public void onDestroy() {
        if (mMapView != null) mMapView.onDestroy();
        if (mSearch != null) mSearch.destroy();
        if (mLocClient != null) mLocClient.stop();
//        if (mPanoramaView != null) mPanoramaView.destroy();
        if (mBaiduNaviCommonModule != null) {
            mBaiduNaviCommonModule.onDestroy();
        }
    }

    public void onStop() {
        if (mBaiduNaviCommonModule != null) {
            mBaiduNaviCommonModule.onStop();
        }
    }

    public void onStart() {
        if (mBaiduNaviCommonModule != null) {
            mBaiduNaviCommonModule.onStart();
        }
    }

    public void onBackPressed() {
        if (mBaiduNaviCommonModule != null) {
            mBaiduNaviCommonModule.onBackPressed(false);
        }
    }

    public void onResume() {
        if (mMapView != null) mMapView.onResume();
        if (mPanoramaView != null) mPanoramaView.onResume();
        if (myOrientationListener != null) {
            myOrientationListener.start();
        }
        if (mBaiduNaviCommonModule != null) {
            mBaiduNaviCommonModule.onResume();
        }
    }

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        if (mBaiduNaviCommonModule != null) {
            mBaiduNaviCommonModule.onConfigurationChanged(newConfig);
        }
    }

    public boolean onKeyDown(int keyCode, android.view.KeyEvent event, boolean psuper) {
        if (mBaiduNaviCommonModule != null) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(RouteGuideModuleConstants.KEY_TYPE_KEYCODE, keyCode);
            mBundle.putParcelable(RouteGuideModuleConstants.KEY_TYPE_EVENT, event);
            mBaiduNaviCommonModule.setModuleParams(RouteGuideModuleConstants
                    .METHOD_TYPE_ON_KEY_DOWN, mBundle);
            try {
                Boolean ret = (Boolean) mBundle.get(RET_COMMON_MODULE);
                if (ret) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return psuper;
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onLowMemory() {

    }

    /**
     * 地图定位到
     *
     * @param pMyLatLng
     */
    public void location(MyLatLng pMyLatLng) {
        if (pMyLatLng == null) {
            MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(4);
            mBaiduMap.animateMapStatus(u);
        } else {
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(pMyLatLng.mLatLng, getMaxZoomLevel()-3);
            mBaiduMap.animateMapStatus(u);
        }
    }
    /**
     * 地图定位到
     *
     * @param pMyLatLng
     * @param zoom      放大级别
     */
    public void location(MyLatLng pMyLatLng, int zoom) {
        if (pMyLatLng == null) {
            MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoom);
            mBaiduMap.animateMapStatus(u);
        } else {
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(pMyLatLng.mLatLng, zoom);
            mBaiduMap.animateMapStatus(u);
        }
    }

//    /**
//     * 获取经纬度获取地址
//     *
//     * @param pMyLatLng
//     * @param pOnGetAddressCallback
//     */
//    public static void getAddress(Context pContext, final MyLatLng pMyLatLng, final
//    OnGetAddressCallback pOnGetAddressCallback) {
//
//        mSearch = GeoCoder.newInstance();
//        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//            @Override
//            public void onGetGeoCodeResult(GeoCodeResult pGeoCodeResult) {
//
//            }
//
//            @Override
//            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult pReverseGeoCodeResult) {
//                if (pReverseGeoCodeResult == null || pReverseGeoCodeResult.error != SearchResult
//                        .ERRORNO.NO_ERROR) {
//                    return;
//                }
//                pMyLatLng.address = pReverseGeoCodeResult.getAddress();
//                List<PoiInfo> vPois = pReverseGeoCodeResult.getPoiList();
//                if (vPois != null && vPois.size() > 0) {
//                    pMyLatLng.address += "," + vPois.get(0).name;
//                }
//                pOnGetAddressCallback.onGetAddress(pMyLatLng.address);
//            }
//        });
//        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(pMyLatLng.mLatLng));
//    }


    /**
     * 通过传入的位置信息，获取google地图的地理位置文字描述
     *
     * @param pContext
     * @param pMyLatLng
     * @param pOnGetAddressCallback
     */
    public static void getAddress(final Context pContext, final MyLatLng pMyLatLng, final OnGetAddressCallback pOnGetAddressCallback) {
//      http://poi.jimicloud.com/poi?_method_=geocoderForBaiDu&latlng=22.553205,113.951165&token=3500307a93c6fc335efa71f60438b465&language=zh
        String QUERYADDRESS = "http://poi.jimicloud.com/poi?_method_=geocoderForBaiDu&latlng=%s,%s&token=3500307a93c6fc335efa71f60438b465&language=%s";

        final String url = String.format(QUERYADDRESS, pMyLatLng.latitude, pMyLatLng.longitude, Locale.getDefault().getLanguage());
        //Log.e("lzx", "url = " + url);

        new AsyncTask<String, Integer, String>(){

            @Override
            protected String doInBackground(String... params) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {

                        String address;
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            int code = json.getInt("code");
                            if (code == 0){
                                address = json.getString("msg");

                                if (address != null) {
                                    return address;
                                }
                            }

                        } catch (Exception e) {
                            Log.e("ljt", "baidumap getaddress Exception!!!!");
                        }

                    } else {
                        Log.e("ljt", "baidumap getaddress response false !!!!");
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("ljt", "baidumap getaddress IOException!!!!3");
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s != null){
                    Log.e("address", s);
                    pOnGetAddressCallback.onGetAddress(s);
                }

            }

        }.execute("");

    }

    public final MyProjection getProjection() {
        MyProjection vMyProjection = new MyProjection();
        vMyProjection.mProjection = mBaiduMap.getProjection();
        return vMyProjection;
    }

    /**
     * 获取地图状态
     *
     * @return
     */
    public MyCameraPosition getCameraPosition() {
        MyCameraPosition vMyCameraPosition = new MyCameraPosition();
        vMyCameraPosition.mCameraPosition = mBaiduMap.getMapStatus();
        return vMyCameraPosition;
    }

    /**
     * 获取中心点
     *
     * @return
     */
    public MyLatLng getTarget() {
        return new MyLatLng(mBaiduMap.getMapStatus().target.latitude, mBaiduMap.getMapStatus()
                .target.longitude);
    }

    /**
     * 地图发生改变监听
     *
     * @param pOnCameraChangeListener
     */
    public void setOnCameraChangeListener(final OnCameraChangeListener pOnCameraChangeListener) {
        mBaiduMap.setOnMapDrawFrameCallback(new BaiduMap.OnMapDrawFrameCallback() {
            @Override
            public void onMapDrawFrame(GL10 pGL10, MapStatus pMapStatus) {
                MyCameraPosition vMyCameraPosition = new MyCameraPosition();
                vMyCameraPosition.mCameraPosition = pMapStatus;
                pOnCameraChangeListener.onCameraChange(vMyCameraPosition);
            }
        });
    }


    /**
     * 设置获取当前手机位置监听：
     * 防止出现定位图层显示手机，但是定位方法中没有返回手机位置
     *
     * @param
     */
    public void setOnLocationListener(final OnLocationListener pOnLocationListener) {
        mOnLocationListener = pOnLocationListener;
    }


    /**
     * 地图加载完监听
     *
     * @param pOnMapLoadedCallback
     */
    public void setOnMapLoadedCallback(OnMapLoadedCallback pOnMapLoadedCallback) {
        mOnMapLoadedCallback = pOnMapLoadedCallback;
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mOnMapLoadedCallback.onMapLoaded();
            }
        });
    }

    /**
     * 地图移动完监听
     *
     * @param pOnMapStatusChangeCallBack
     */
    public void setOnMapStatusChangeCallBack(OnMapStatusChangeCallBack pOnMapStatusChangeCallBack) {
        mOnMapStatusChangeCallBack = pOnMapStatusChangeCallBack;
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                MapChange vM = new MapChange();
                if (mapStatus != null) {
                    vM.target = mapStatus.target;
                    vM.zoom = mapStatus.zoom;
                }
                mOnMapStatusChangeCallBack.onMapStatusChangeStart(vM);
            }


            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                MapChange vM = new MapChange();
                if (mapStatus != null) {
                    vM.target = mapStatus.target;
                    vM.zoom = mapStatus.zoom;
                }
                mOnMapStatusChangeCallBack.onMapStatusChange(vM);
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                MapChange vM = new MapChange();
                if (mapStatus != null) {
                    vM.target = mapStatus.target;
                    vM.zoom = mapStatus.zoom;
                }
                mOnMapStatusChangeCallBack.onMapStatusChangeFinish(vM);
            }
        });
    }

    /**
     * 监听Marker点击事件
     *
     * @param pOnMarkerClickListener
     */
    public void setOnMarkerClickListener(final OnMarkerClickListener pOnMarkerClickListener) {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                MyMarker vMyMarker = new MyMarker();
                vMyMarker.mMarker = marker;
                vMyMarker.mMyLatLng = new MyLatLng(marker.getPosition().latitude, marker
                        .getPosition().longitude);
                pOnMarkerClickListener.onMarkerClick(vMyMarker);
                return false;
            }
        });
    }

    public final void setOnInfoWindowClickListener(OnInfoWindowClickListener
                                                           pOnInfoWindowClickListener) {

    }


    SuggestionSearch mSuggestionSearch;

    /**
     * 关键字搜索
     *
     * @param keywords
     * @param pOnSearchResultListener
     */
    public void searchSugget(final String keywords, final OnSearchResultListener
            pOnSearchResultListener) {
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }
                List<SuggestionResult.SuggestionInfo> infos = suggestionResult.getAllSuggestions();
                final ArrayList<LocationResult> v = new ArrayList<LocationResult>();
                if (infos != null && infos.size() > 0) {
                    for (SuggestionResult.SuggestionInfo info : infos) {
                        if (info.pt == null) continue;
                        LocationResult vLocation = new LocationResult();
                        vLocation.keywords = info.key;
                        vLocation.address = info.city + info.district;
                        vLocation.latLng = new MyLatLng(info.pt.latitude, info.pt.longitude);
                        v.add(vLocation);
                    }
                }
                pOnSearchResultListener.onSearchResult(v);
            }
        });

        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .keyword(keywords).city(keywords));
    }


    /**
     * poi搜索
     *
     * @param pMyLatLng
     * @param pOnSearchResultListener
     */
    public void searchPoi(final MyLatLng pMyLatLng, final OnSearchResultListener
            pOnSearchResultListener) {

        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent pMotionEvent) {
                if (pMotionEvent.getAction() == MotionEvent.ACTION_UP) {
                    reverseGeoCode(mBaiduMap.getMapStatus().target, pOnSearchResultListener);
                }
            }
        });

        reverseGeoCode(pMyLatLng.mLatLng, pOnSearchResultListener);

    }

    /**
     * 获取详细地址
     *
     * @param pMyLatLng
     * @param pOnSearchResultListener
     */
    private void reverseGeoCode(LatLng pMyLatLng, final OnSearchResultListener
            pOnSearchResultListener) {
        GeoCoder coder = GeoCoder.newInstance();//创建一个地址解析器的实例

        ReverseGeoCodeOption reverseCode = new ReverseGeoCodeOption();
        ReverseGeoCodeOption result = reverseCode.location(new LatLng(
                pMyLatLng.latitude, pMyLatLng.longitude));
        coder.reverseGeoCode(result);
        coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            @Override
            public void onGetReverseGeoCodeResult(
                    ReverseGeoCodeResult result) {
                Log.e("BaiduMap",
                        "onGetReverseGeoCodeResult:" + result.getAddress());
                List<PoiInfo> vPoiInfos = result.getPoiList();
                final List<LocationResult> vLocations = new ArrayList<LocationResult>();
                if (vPoiInfos != null && vPoiInfos.size() > 0) {
                    for (PoiInfo vPI : vPoiInfos) {
                        Log.e("BaiduMap", vPI.name + ": " + vPI.address);
                        LocationResult vLocation = new LocationResult();
                        vLocation.keywords = vPI.name;
                        vLocation.address = vPI.address;
                        vLocation.latLng = new MyLatLng(vPI.location.latitude, vPI.location
                                .longitude);
                        vLocations.add(vLocation);
                    }
                }
                pOnSearchResultListener.onSearchResult(vLocations);
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                Log.e("BaiduMap", "onGetGeoCodeResult:" + result.getAddress());
            }
        });
    }


    /**
     * 计算四个点的中心点
     *
     * @param points
     */
    public void setCenter(List<MyLatLng> points) {
        MyLatLng latMax = null, latMin = null, lngMax = null, lngMin = null;
        for (int i = 0; i < points.size(); i++) {
            MyLatLng vMyLatLng = points.get(i);
            if (latMax == null) {
                latMax = vMyLatLng;
                latMin = vMyLatLng;
                lngMax = vMyLatLng;
                lngMin = vMyLatLng;
            } else {
                if (vMyLatLng.latitude > latMax.latitude) latMax = vMyLatLng;
                if (vMyLatLng.latitude < latMin.latitude) latMin = vMyLatLng;
                if (vMyLatLng.longitude > lngMax.longitude) lngMax = vMyLatLng;
                if (vMyLatLng.longitude < lngMin.longitude) lngMin = vMyLatLng;
            }
        }

        MyLatLng northeast = new MyLatLng(latMin.latitude, lngMax.longitude);
        MyLatLng southwest = new MyLatLng(latMax.latitude, lngMin.longitude);
        moveCamera(new MyCameraUpdate().newLatLngBounds(new MyLatLngBounds(northeast, southwest)));
        moveCamera(new MyCameraUpdate().zoomOut());
    }

    /**
     * 更新地图带动画效果
     *
     * @param update
     * @param durationMs 动画延时毫秒
     */
    public final void animateCamera(MyCameraUpdate update, int durationMs) {
        mBaiduMap.animateMapStatus(update.mCameraUpdate, durationMs);
    }

    public MyMarker mMarkerMobile;
    public MyBitmapDescriptor mobile;

    /**
     * 初始化方向传感器
     */
    private void initOritationListener() {
        myOrientationListener = new MyOrientationListener(
                mActivity);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener
                .OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mXDirection = (int) x;
                if (mBDLocation == null) return;
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .accuracy(mCurrentAccracy)
                        .direction(mXDirection)
                        .latitude(mBDLocation.getLatitude())
                        .longitude(mBDLocation.getLongitude())
                        .build();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
            }
        });
        myOrientationListener.start();
    }

    /**
     * 是否显示手机图标
     */
    public void setIsShowPhone(boolean flag) {
        this.mIsShowPhone = flag;
    }

    boolean mIsShowPhone = true;
    LocationClient mLocClient;
    MyOrientationListener myOrientationListener;
    int mXDirection;
    public BDLocation mBDLocation;
    float mCurrentAccracy;

    public void myLocation() {
        if (mLocClient == null) {
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            // 定位初始化
            mLocClient = new LocationClient(mActivity);
            mLocClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation pBDLocation) {
                    if (pBDLocation == null || mMapView == null)
                        return;
                    mBDLocation = pBDLocation;
                    mCurrentAccracy = pBDLocation.getRadius();
                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(pBDLocation.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(mXDirection).latitude(pBDLocation.getLatitude())
                            .longitude(pBDLocation.getLongitude()).build();
                    mBaiduMap.setMyLocationData(locData);
                    if (mIsShowPhone) {
                        /** 添加手机图标 */
                        phoneOverlay();
                    } else {
                        /** 移除手机图标 */
                        if (mMarkerMobile != null) mMarkerMobile.remove();
                    }
                }

            });
            MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration
                    .LocationMode.NORMAL;
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true,
                    null));
            initOritationListener();
        }

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(8000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 添加手机图标
     */
    public void phoneOverlay() {
        if (mBDLocation == null) return;

        //获取到当前手机位置回调传值
        if (mOnLocationListener != null) {
            mOnLocationListener.onLocationResult(new MyLatLng(mBDLocation.getLatitude(),
                    mBDLocation.getLongitude()), mBDLocation.getRadius(),mBDLocation.getSpeed());
        }
        if (mOnLocationListener != null) {
            mOnLocationListener.onLocationResult2(new MyLatLng(mBDLocation.getLatitude(),
                    mBDLocation.getLongitude()), "");
        }
        if (mMarkerMobile != null) {
            mMarkerMobile.setPosition(new MyLatLng(mBDLocation.getLatitude(), mBDLocation
                    .getLongitude()));
        } else {
            TextView textView = (TextView) getView();
            textView.setText(locationString);
            Log.e("lzx", "locationString = " + locationString);
            mobile = new MyBitmapDescriptor(textView);

            MyMarkerOptions ooA = new MyMarkerOptions().position(new MyLatLng(mBDLocation
                    .getLatitude(), mBDLocation.getLongitude())).icon(mobile);
            mMarkerMobile = addMarker(ooA);
            Bundle vBundle = new Bundle();
            vBundle.putInt(MAP_MARKER_ID, PHONE_MAP_MARKER_ID);
            mMarkerMobile.setExtraInfo(vBundle);
        }
    }

    public View getView() {

        LayoutInflater inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView vTextView = (TextView) inflater.inflate(R.layout.view_home_location_marker, null);
        return vTextView;
    }


    /**
     * 隐藏手机图标及定位图层
     */
    public void hinitLocation() {
        mBaiduMap.setMyLocationEnabled(false);
        mMarkerMobile.remove();
        mLocClient = null;
        myOrientationListener.stop();
    }

    /**
     * 路径规划
     *
     * @param start
     * @param end
     * @param startIconRes
     * @param endIconRes
     * @param mode
     * @param span         是否将轨迹路线缩放到全部可见的地图层级
     */
    public void routePlan(final MyLatLng start, final MyLatLng end, final int startIconRes, final
    int endIconRes, int mode, final boolean span, final OnRoutePlanCallback pCallback) {
        // 初始化搜索模块，注册事件监听
        RoutePlanSearch mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult pDrivingRouteResult) {
                if (pDrivingRouteResult != null && pDrivingRouteResult.getRouteLines() != null) {
                    //返回路线的总长度
                    pCallback.onSuccess(pDrivingRouteResult.getRouteLines().get(0).getDistance());
                }
                if (pDrivingRouteResult == null || pDrivingRouteResult.error != SearchResult
                        .ERRORNO.NO_ERROR) {
                    pCallback.onFailure();
                }
                if (pDrivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                    //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                    //result.getSuggestAddrInfo()
                    return;
                }

                if (pDrivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap,
                            startIconRes, endIconRes);
                    overlay.setData(pDrivingRouteResult.getRouteLines().get(0));
                    overlay.addToMap();
                    if (span) overlay.zoomToSpan();

                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
        //设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withLocation(start.mLatLng);
        PlanNode enNode = PlanNode.withLocation(end.mLatLng);

        switch (mode) {
            case 1:  //驾车
                mSearch.drivingSearch((new DrivingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));
                break;
            case 2:  //步行
                mSearch.walkingSearch((new WalkingRoutePlanOption())
                        .from(stNode)
                        .to(enNode));
                break;
            case 3:  //踦自行车
                break;
            case 4:  //公交
                break;
        }

    }

    //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
        int startIconRes, endIconRes;


        public MyDrivingRouteOverlay(BaiduMap baiduMap, int startIconRes, int endIconRes) {
            super(baiduMap);
            this.startIconRes = startIconRes;
            this.endIconRes = endIconRes;
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(startIconRes);
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(endIconRes);
        }

    }


    public void Navigtion(final MyLatLng end) {

    }


    //用于判断百度地图是否显示
    public BaiduMap getMainMap() {
        return mBaiduMap;
    }

    /**
     * 获取当前屏幕显示范围内的地理范围，当旋转或俯视时，是当前屏幕可见显示范围的最大外接矩形
     *
     * @return
     */
    public MyLatLngBounds getLatLngBounds() {
        MyLatLngBounds bounds = new MyLatLngBounds(mBaiduMap.getMapStatus());
        return bounds;
    }

    /**
     * 计算两点之间距离
     *
     * @param start
     * @param end
     * @return 米
     */
    static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI = 6.28318530712; // 2*PI
    static double DEF_PI180 = 0.01745329252; // PI/180.0
    static double DEF_R = 6370693.5; // radius of earth

    public double getDistance(LatLng start, LatLng end) {

        double ew1, ns1, ew2, ns2;

        double distance = 0;
        // 角度转换为弧度
        ew1 = start.longitude * DEF_PI180;
        ns1 = start.latitude * DEF_PI180;
        ew2 = end.longitude * DEF_PI180;
        ns2 = end.latitude * DEF_PI180;
        // 求大圆劣弧与球心所夹的角(弧度)
        distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1)
                * Math.cos(ns2) * Math.cos(ew1 - ew2);
        // 调整到[-1..1]范围内，避免溢出
        if (distance > 1.0)
            distance = 1.0;
        else if (distance < -1.0)
            distance = -1.0;
        // 求大圆劣弧长度
        distance = DEF_R * Math.acos(distance);
        return distance / 1000;

    }

    /*******************************************************************************************************************
     * 导航
     ********************************************************************************************************************/

    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "tuqiang";
    public static List<Activity> activityList = new LinkedList<Activity>();
    public OnJumpToNavigatorListener onJumpToNavigatorListener;

    public void initNavigation(Activity activity, OnMyNaviInitListener onMyNaviInitListener,
                               OnJumpToNavigatorListener listener) {
        // 打开log开关
        BNOuterLogUtil.setLogSwitcher(true);
        this.onJumpToNavigatorListener = listener;
        if (initDirs()) {
            initNavi(activity, onMyNaviInitListener);
        }
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void initNavi(final Activity activity, OnMyNaviInitListener listener) {
        BaiduNaviManager.getInstance().init(activity, mSDCardPath, APP_FOLDER_NAME, listener, null);
    }

    public void initSetting() {
        // 设置是否双屏显示
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition
                .ROAD_CONDITION_BAR_SHOW_ON);
        // 设置导航播报模式(当前是新手模式)
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Novice);
        // 是否开启路况
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    public void routeplanToNavi(Activity activity, MyLatLng mStartLatlng, MyLatLng mEndLatlng) {
        BNRoutePlanNode sNode = new BNRoutePlanNode(mStartLatlng.longitude, mStartLatlng
                .latitude, null, null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(mEndLatlng.longitude, mEndLatlng.latitude,
                null, null, BNRoutePlanNode.CoordinateType.WGS84);
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(activity, list, 1, true, new
                    DemoRoutePlanListener(activity, sNode));
        }
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {
        private BNRoutePlanNode mBNRoutePlanNode = null;
        private Context mContext;

        public DemoRoutePlanListener(Context context, BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
            this.mContext = context;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
			 */
            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BaiduNavigationActivity")) {
                    return;
                }
            }
            //调用诱导界面回调
            onJumpToNavigatorListener.onJumpToNavigator();
        }

        @Override
        public void onRoutePlanFailed() {
            onJumpToNavigatorListener.onRoutePlanFailed();
        }
    }

    /**
     * 是否初始化完成
     *
     * @return
     */
    public boolean isNaviInited() {
        return BaiduNaviManager.isNaviInited();
    }

    private BaiduNaviCommonModule mBaiduNaviCommonModule = null;
    private final static String RET_COMMON_MODULE = "module.ret";

    private interface RouteGuideModuleConstants {
        final static int METHOD_TYPE_ON_KEY_DOWN = 0x01;
        final static String KEY_TYPE_KEYCODE = "keyCode";
        final static String KEY_TYPE_EVENT = "event";
    }

    public View getNavigationView(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        }
        View view = null;
        //使用通用接口
        mBaiduNaviCommonModule = NaviModuleFactory.getNaviModuleManager().getNaviCommonModule(
                NaviModuleImpl.BNaviCommonModuleConstants.ROUTE_GUIDE_MODULE, activity,
                BNaviBaseCallbackModel.BNaviBaseCallbackConstants.CALLBACK_ROUTEGUIDE_TYPE, new
                        MyOnNavigationListener(activity).getOnNavigationListener());
        if (mBaiduNaviCommonModule != null) {
            mBaiduNaviCommonModule.onCreate();
            view = mBaiduNaviCommonModule.getView();
        }
        return view;
    }

    private class MyOnNavigationListener {

        private Activity mContext;
        private BNRouteGuideManager.OnNavigationListener mOnNavigationListener = null;

        public MyOnNavigationListener(Activity context) {
            this.mContext = context;
        }

        public BNRouteGuideManager.OnNavigationListener getOnNavigationListener() {

            return mOnNavigationListener = new BNRouteGuideManager.OnNavigationListener() {

                @Override
                public void onNaviGuideEnd() {
                    //退出导航
                    mContext.finish();
                }

                @Override
                public void notifyOtherAction(int actionType, int arg1, int arg2, Object obj) {
                    if (actionType == 0) {
                        //导航到达目的地 自动退出
                        Log.i("lun", "notifyOtherAction actionType = " + actionType + ",导航到达目的地！");
                    }
//                  Log.i(TAG, "actionType:" + actionType + "arg1:" + arg1 + "arg2:" + arg2 +
// "obj:" + obj.toString());
                    Log.i("lun", "actionType:" + actionType + "arg1:" + arg1 + "arg2:" + arg2);
                }
            };
        }
    }

    public void resetNavigation(MyLatLng mEndLatlng) {
        BNRouteGuideManager.getInstance().resetEndNodeInNavi(new BNRoutePlanNode(mEndLatlng
                .longitude, mEndLatlng.latitude, null, null, BNRoutePlanNode.CoordinateType.WGS84));
    }

    public void exitNavigation() {
        BNRouteGuideManager.getInstance().forceQuitNaviWithoutDialog();
    }

    public void hidenGoogleMapLocationView() {
    }

}