package com.jimi.map;

import com.baidu.navisdk.adapter.BNRoutePlanNode;

import java.io.Serializable;

/**
 * Created by Ma.Tianlun on 2016/11/21.
 */

public class MyBNRoutePlanNode extends BNRoutePlanNode implements Serializable {

    public MyBNRoutePlanNode(double longitude, double latitude, String name, String description) {
        super(longitude, latitude, name, description);
    }

//    public MyBNRoutePlanNode(double longitude, double latitude, String name, String description, CoordinateType coType) {
//        super(longitude, latitude, name, description, coType);
//    }
}
