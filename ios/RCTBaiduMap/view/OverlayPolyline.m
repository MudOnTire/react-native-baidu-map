//
//  OverlayPolyline.m
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/3/1.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "OverlayPolyline.h"

@interface OverlayPolyline ()

@property (nonatomic,strong) JMPolyline *polyline;
@property (nonatomic,assign) BOOL bHasNotification;

@end

@implementation OverlayPolyline

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
    if (_points != nil) {
        [self sendPolylineVisible:NO];
    }
    _points = points;
    if (points.count == 0) return;

    CLLocationCoordinate2D *coords = (CLLocationCoordinate2D *)malloc(sizeof(CLLocationCoordinate2D) * points.count);
    for (int i = 0; i < points.count; i++) {
        CLLocationCoordinate2D coor = CLLocationCoordinate2DMake([[[points objectAtIndex:i] objectForKey:@"latitude"] doubleValue], [[[points objectAtIndex:i] objectForKey:@"longitude"] doubleValue]);
        coords[i] = coor;
    }

    [self.polyline setPolylineWithCoordinates:coords count:points.count];
    free(coords);

    [self sendPolylineVisible:YES];
}

- (void)setVisible:(BOOL)visible
{
    _visible = visible;

    [self sendPolylineVisible:visible];
}

- (void)addTopMap:(BMKMapView *)mapView
{
    if (!self.bHasNotification) {
        self.bHasNotification = YES;
        [[NSNotificationCenter defaultCenter] addObserver:mapView selector:@selector(visibleBaiduOverlay:) name:kBaiduMapOverlayVisible object:nil];
    }
    [mapView addOverlay:self.polyline];
}

- (void)removeTopMap:(BMKMapView *)mapView
{
    [[NSNotificationCenter defaultCenter] removeObserver:mapView name:kBaiduMapOverlayVisible object:nil];
    self.bHasNotification = NO;
    [mapView removeOverlay:self.polyline];
}

- (void)removeFromSuperview
{
    if (_polyline) {
        [[NSNotificationCenter defaultCenter] postNotificationName:kBaiduMapViewRemoveOverlay object:self.polyline];
        _polyline = nil;
    }
    [super removeFromSuperview];
}

- (void)sendPolylineVisible:(BOOL)visible
{
    if (_polyline && _polyline.visible != visible) {
        [[NSNotificationCenter defaultCenter] postNotificationName:kBaiduMapOverlayVisible object:@{@"Overlay": self.polyline, @"visible":[NSNumber numberWithBool:visible]}];
    }
}

@end
