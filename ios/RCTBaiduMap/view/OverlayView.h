//
//  OverlayView.h
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/2/28.
//  Copyright © 2019 jimi. All rights reserved.
//

#import <BaiduMapAPI_Map/BMKCircleView.h>
#import <BaiduMapAPI_Map/BMKMapView.h>

NS_ASSUME_NONNULL_BEGIN

extern NSString *const kBaiduMapViewRemoveOverlay;

@interface OverlayView : UIView

/**
 往地图注入BMKOverlay，继承的视图需要重写并实现

 @param mapView 父视图-地图
 */
- (void)addTopMap:(BMKMapView *)mapView;

- (void)remove;

@end

NS_ASSUME_NONNULL_END
