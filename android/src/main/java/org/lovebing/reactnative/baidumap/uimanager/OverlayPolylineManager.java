/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.lovebing.reactnative.baidumap.uimanager;

import android.content.Context;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import org.lovebing.reactnative.baidumap.util.ColorUtil;
import org.lovebing.reactnative.baidumap.view.OverlayPolyline;

import static org.lovebing.reactnative.baidumap.util.LatLngUtil.fromReadableArray;


/**
 * @author lovebing Created on Dec 09, 2018
 */
public class OverlayPolylineManager extends SimpleViewManager<OverlayPolyline> {

    @Override
    public String getName() {
        return "BaiduMapOverlayPolyline";
    }

    @Override
    protected OverlayPolyline createViewInstance(ThemedReactContext reactContext) {
        return new OverlayPolyline(reactContext);
    }

    @ReactProp(name = "points")
    public void setPoints(OverlayPolyline overlayPolyline, ReadableArray points) {
        overlayPolyline.setPoints(fromReadableArray(points));
    }

    @ReactProp(name = "color")
    public void setColor(OverlayPolyline overlayPolyline, String color) {
        overlayPolyline.setColor(ColorUtil.fromString(color));
    }

    @ReactProp(name="visible")
    public void visible(OverlayPolyline overlayPolyline, boolean visible){
        overlayPolyline.setVisible(visible);
    }

    @ReactProp(name="width")
    public void setWidth(OverlayPolyline overlayPolyline, int width ){
        overlayPolyline.setWidth(dip2px(overlayPolyline.getContext(),width));
    }

    /**
     * 根据手机分辨率从DP转成PX
     * @param context
     * @param dpValue
     * @return
     */
    private int dip2px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //不用LatLngUtil的那套，应为key值不一样
//    private LatLng fromReadableMap(ReadableMap readableMap) {
//        double lat, lng;
//        lat = Double.parseDouble(readableMap.getString("lat"));
//        lng = Double.parseDouble(readableMap.getString("lng"));
//        return new LatLng(lat, lng);
//    }
//
//    public List<LatLng> fromReadableArray(ReadableArray readableArray) {
//        List<LatLng> list = new ArrayList<>();
//        int size = readableArray.size();
//        for (int i = 0; i < size; i++) {
//            list.add(fromReadableMap(readableArray.getMap(i)));
//        }
//        return list;
//    }

}
