package org.lovebing.reactnative.baidumap.module;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;

/*
 * COPYRIGHT. ShenZhen JiMi Technology Co., Ltd. 2019.
 * ALL RIGHTS RESERVED.
 *
 * No part of this publication may be reproduced, stored in a retrieval system, or transmitted,
 * on any form or by any means, electronic, mechanical, photocopying, recording,
 * or otherwise, without the prior written permission of ShenZhen JiMi Network Technology Co., Ltd.
 *
 * @ProjectName newsmarthome2.0
 * @Description: 百度地图线路规划功能
 * @Date 2019/3/16 16:41
 * @author HuangJiaLin
 * @version 2.0
 */
public class RoutePlanModule extends BaseModule{
    public RoutePlanModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "BaiduRoutePlanModule";
    }

    @ReactMethod
    public void startRoutePlan(){


    }

}
