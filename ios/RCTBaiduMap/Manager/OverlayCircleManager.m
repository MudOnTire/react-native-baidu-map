//
//  OverlayCircleManager.m
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/2/28.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "OverlayCircleManager.h"
#import "OverlayCircle.h"

@implementation OverlayCircleManager

RCT_EXPORT_MODULE(BaiduMapOverlayCircle)

RCT_EXPORT_VIEW_PROPERTY(radius, double)
RCT_EXPORT_VIEW_PROPERTY(fillColor, NSString)

RCT_CUSTOM_VIEW_PROPERTY(center, CLLocationCoordinate2D, OverlayCircle) {
    [view setCenterLatLng:[RCTConvert NSDictionary:json]];
}

RCT_CUSTOM_VIEW_PROPERTY(stroke, CLLocationCoordinate2D, OverlayCircle) {
    [view setStroke:[Stroke getStroke:[RCTConvert NSDictionary:json]]];
}

- (UIView *)view {
    OverlayCircle *view = [[OverlayCircle alloc] init];
    return view;
}


@end
