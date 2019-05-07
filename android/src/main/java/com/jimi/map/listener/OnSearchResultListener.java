package com.jimi.map.listener;

import com.jimi.map.LocationResult;

import java.util.List;

/**
 * Created by Administrator on 2015/8/11.
 *
 * 【查询坐标详细地址回调接口】
 */
public interface OnSearchResultListener {
    public void onSearchResult(List<LocationResult> pLocations);
}
