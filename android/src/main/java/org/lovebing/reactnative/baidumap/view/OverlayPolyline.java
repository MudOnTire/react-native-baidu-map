/**
 * Copyright (c) 2016-present, lovebing.org.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package org.lovebing.reactnative.baidumap.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lovebing Created on Dec 9, 2018
 */
public class OverlayPolyline extends View implements OverlayView {

    private List<LatLng> points;
    private Polyline polyline;
    private int color = 0xAAFF0000;
    private int width = 8;
    private boolean visible = true;

    public OverlayPolyline(Context context) {
        super(context);
    }

    public OverlayPolyline(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlayPolyline(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public OverlayPolyline(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        //Log.i("setPoints","更新了点：" + points.size() + " ,polyline:" + polyline);
        this.points = points;
        if (polyline != null && points.size() >= 2) {
            polyline.setPoints(points);
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        //Log.i("OverlayPolyline","visible:" + color);
        this.color = color;
        if (polyline != null) {
            polyline.setColor(color);
        }
    }

    public void setVisible(boolean visible){
        //Log.i("OverlayPolyline","visible:" + visible);
        this.visible = visible;
        if (polyline != null){
            polyline.setVisible(visible);
        }
    }

    public void setWidth(int width){
        //Log.i("OverlayPolyline","width:" + width);
        this.width = width;
        if (polyline != null){
            polyline.setWidth(width);
        }
    }

    @Override
    public void addTopMap(BaiduMap baiduMap) {
        //Log.i("OverlayPolyline","添加画线的覆盖物");
        PolylineOptions options = new PolylineOptions().width(width) //getWidth()
                .color(color).visible(visible);
        if(points.size() >=2){
            options.points(points);
            polyline = (Polyline) baiduMap.addOverlay(options);
        } else {
            List<LatLng> defaultPoints = new ArrayList<>();
            defaultPoints.add(new LatLng(39.916927,116.404269));
            defaultPoints.add(new LatLng(39.916927,116.404269));
            options.points(defaultPoints);
            polyline = (Polyline) baiduMap.addOverlay(options);
        }
    }

    @Override
    public void remove() {
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }
    }
}
