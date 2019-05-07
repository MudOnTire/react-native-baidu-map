package com.jimi.map.listener;

/**
 * Created by Ma.Tianlun on 2016-3-24.
 * Email: mashouyinchen@jimi360.cn
 *
 *  【路径规划完成回调接口】
 *
 */
public interface OnRoutePlanCallback {
    public void onSuccess(int diatance);
    public void onFailure();
}
