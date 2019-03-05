//
//  OverlayPolyline.m
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/3/1.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "OverlayPolyline.h"
#import "JMPolyline.h"
#import <React/RCTConvert+CoreLocation.h>

@interface OverlayPolyline ()

@property (nonatomic,strong) JMPolyline *polyline;
@property (nonatomic,assign) CLLocationCoordinate2D *pointArray;    //需要释放！！！
@property (nonatomic,assign) NSUInteger pointCount;

@end

@implementation OverlayPolyline

- (void)dealloc
{
    if (_pointArray) {
        free(_pointArray);
        _pointArray = NULL;
    }
}

- (JMPolyline *)polyline
{
    if (!_polyline) {
        _polyline = [[JMPolyline alloc] init];
    }

    return _polyline;
}

- (void)setColor:(NSString *)color
{
    _color = color;
    self.polyline.color = [Stroke colorWithHexString:color];
}

- (void)setWidth:(float)width
{
    _width = width;
    self.polyline.width = width;
}

- (void)setPoints:(NSArray *)points
{
    _points = points;
    if (_pointCount < points.count) {
        if (_pointArray) {
            free(_pointArray);
            _pointArray = NULL;
        }
    }

    if (_pointArray == NULL) {
        _pointArray  = (CLLocationCoordinate2D *)malloc(points.count * sizeof(CLLocationCoordinate2D));
    }
    _pointCount = points.count;

    for (int i=0; i<points.count; i++) {
        _pointArray[i].latitude = [[[points objectAtIndex:i] objectForKey:@"latitude"] doubleValue];
        _pointArray[i].longitude = [[[points objectAtIndex:i] objectForKey:@"longitude"] doubleValue];
    }

    [self.polyline setPolylineWithCoordinates:_pointArray count:_pointCount];
}

- (void)addTopMap:(BMKMapView *)mapView
{
    [mapView addOverlay:self.polyline];
}

- (void)removeFromSuperview
{
    if (_polyline) {
        [[NSNotificationCenter defaultCenter] postNotificationName:kBaiduMapViewRemoveOverlay object:self.polyline];
        _polyline = nil;
    }
    [super removeFromSuperview];
}

@end
