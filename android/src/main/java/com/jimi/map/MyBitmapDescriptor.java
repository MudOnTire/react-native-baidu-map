package com.jimi.map;


import android.view.View;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;

/**
 * Created by Administrator on 2015/8/10.
 *
 * 获取 bitmap 描述信息
 */
public class MyBitmapDescriptor {
    BitmapDescriptor mBitmapDescriptor;

    public MyBitmapDescriptor(int pResId) {
        mBitmapDescriptor =  BitmapDescriptorFactory
                .fromResource(pResId);
    }

    public MyBitmapDescriptor(View pView) {
        mBitmapDescriptor =  BitmapDescriptorFactory
                .fromView(pView);
    }

    /**
     * 回收 bitmap 资源
     * （注！请确保在不再使用该 bitmap descriptor 时再调用该函数。）
     */
    public void recycle() {
        mBitmapDescriptor.recycle();
    }
}
