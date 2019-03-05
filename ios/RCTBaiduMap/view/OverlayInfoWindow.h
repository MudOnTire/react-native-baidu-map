//
//  OverlayInfoWindow.h
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/3/4.
//  Copyright © 2019 jimi. All rights reserved.
//
#import "OverlayView.h"
#import <React/RCTConvert+CoreLocation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OverlayInfoWindow : OverlayView

@property (nonatomic, strong) NSString *title;
@property (nonatomic, assign) BOOL visible;
@property (nonatomic, assign) CLLocationCoordinate2D location;
@property (nonatomic, assign) float width;
@property (nonatomic, assign) float height;

@end

NS_ASSUME_NONNULL_END
