//
//  OverlayCircle.m
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/2/28.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "OverlayCircle.h"
#import "JMCircle.h"

@interface OverlayCircle ()

@property (nonatomic, strong) JMCircle *circle;

@end

@implementation OverlayCircle

- (JMCircle *)circle
{
    if (!_circle) {
        _circle = [[JMCircle alloc] init];
    }

    return _circle;
}

-(void)setCenterLatLng:(NSDictionary *)LatLngObj {
    if (LatLngObj) {
        double lat = 0;
        double lng = 0;
        if (LatLngObj[@"center"]) {  //lzj fixed
            NSDictionary *Dic = [LatLngObj objectForKey:@"center"];
            lat = [[Dic objectForKey:@"latitude"] doubleValue];
            lng = [[Dic objectForKey:@"longitude"] doubleValue];
        } else {
            lat = [RCTConvert double:LatLngObj[@"latitude"]];
            lng = [RCTConvert double:LatLngObj[@"longitude"]];
        }
        CLLocationCoordinate2D point = CLLocationCoordinate2DMake(lat, lng);

        _coordinate = point;
        self.circle.coordinate = point;
    }
}

- (void)setRadius:(double)radius
{
    _radius = radius;
    self.circle.radius = radius;
}

- (void)setFillColor:(NSString *)fillColor
{
    _fillColor = fillColor;
    self.circle.fillColor = [Stroke colorWithHexString:fillColor];
}

- (void)setStroke:(Stroke *)stroke
{
    self.circle.stroke = stroke;
}

- (void)addTopMap:(BMKMapView *)mapView
{
    [mapView addOverlay:self.circle];
}

- (void)removeFromSuperview
{
    if (_circle) {
        [[NSNotificationCenter defaultCenter] postNotificationName:kBaiduMapViewRemoveOverlay object:self.circle];
        _circle = nil;
    }
    [super removeFromSuperview];
}

@end
