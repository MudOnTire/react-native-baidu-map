//
//  OverlayView.m
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/2/28.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "OverlayView.h"
#import <React/RCTConvert.h>

NSString *const kBaiduMapViewRemoveOverlay = @"kBaiduMapViewRemoveOverlay";
NSString *const kBaiduMapOverlayVisible = @"kBaiduMapOverlayVisible";

@implementation OverlayView

- (void)addTopMap:(BMKMapView *)mapView
{
}

- (void)removeTopMap:(BMKMapView *)mapView
{
}

- (void)remove
{
    [self removeFromSuperview];
}


@end
