package com.jimi.map.listener;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.jimi.map.CameraPosition;
import com.jimi.map.MapChange;

/**
 * Created by Ma.Tianlun on 2016/8/22.
 * Email: mashouyinchen@jimi360.cn
 */
public interface OnMapStatusChangeCallBack {
    void onMapStatusChangeStart(MapChange var1);

    void onMapStatusChange(MapChange var1);

    void onMapStatusChangeFinish(MapChange var1);
}
