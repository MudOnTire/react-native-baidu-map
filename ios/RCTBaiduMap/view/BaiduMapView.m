//
//  RCTBaiduMap.m
//  RCTBaiduMap
//
//  Created by lovebing on 4/17/2016.
//  Copyright © 2016 lovebing.org. All rights reserved.
//

#import "BaiduMapView.h"
#import "OverlayCircle.h"
#import "JMMarkerAnnotation.h"
#import "OverlayInfoWindow.h"

@implementation BaiduMapView {
    JMMarkerAnnotation *_annotation;
    NSMutableArray *_annotations;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kBaiduMapViewRemoveOverlay object:nil];
}

- (void)addSubview:(UIView *)view
{
    if ([view isKindOfClass:[OverlayView class]]) {
        [((OverlayView *)view) addTopMap:self];
    } else {
        [super addSubview:view];
    }
}

- (void)removeBaiduOverlay:(NSNotification *)noti
{
    id <BMKOverlay>overlay = noti.object;
    if (overlay) {
        [self removeOverlay:overlay];
    }
}

-(void)setZoom:(float)zoom {
    self.zoomLevel = zoom;
}

-(void)setCenterLatLng:(NSDictionary *)LatLngObj {
    double lat = [RCTConvert double:LatLngObj[@"lat"]];
    double lng = [RCTConvert double:LatLngObj[@"lng"]];
    CLLocationCoordinate2D point = CLLocationCoordinate2DMake(lat, lng);
    self.centerCoordinate = point;
}

-(void)setMarker:(NSDictionary *)option {
    if (option != nil) {
        if(_annotation == nil) {
            _annotation = [[JMMarkerAnnotation alloc]init];
            [self addMarker:_annotation option:option];
        }
        else {
            [self updateMarker:_annotation option:option];
        }
    }
}

-(void)setMarkers:(NSArray *)markers {
    NSUInteger markersCount = [markers count];
    if(_annotations == nil) {
        _annotations = [[NSMutableArray alloc] init];
    }

    if(markers != nil) {
        for (int i = 0; i < markersCount; i++)  {
            NSDictionary *option = [markers objectAtIndex:i];
            
            JMMarkerAnnotation *annotation = nil;
            if(i < [_annotations count]) {
                annotation = [_annotations objectAtIndex:i];
            }
            if(annotation == nil) {
                annotation = [[JMMarkerAnnotation alloc]init];
                [self addMarker:annotation option:option];
                [_annotations addObject:annotation];
            }
            else {
                [self updateMarker:annotation option:option];
            }
        }
        
        NSUInteger _annotationsCount = [_annotations count];
        if(markersCount < _annotationsCount) {
            NSUInteger start = _annotationsCount - 1;
            for(NSUInteger i = start; i >= markersCount; i--) {
                JMMarkerAnnotation *annotation = [_annotations objectAtIndex:i];
                [self removeAnnotation:annotation];
                [_annotations removeObject:annotation];
            }
        }
    }
}

- (CLLocationCoordinate2D)getCoorFromMarkerOption:(NSDictionary *)option {
    double lat = 0;
    double lng= 0;
    if (option[@"location"]) {  //lzj fixed
        NSDictionary *Dic = [RCTConvert NSDictionary:option[@"location"]];
        lat = [[Dic objectForKey:@"latitude"] doubleValue];
        lng = [[Dic objectForKey:@"longitude"] doubleValue];
    } else {
        lat = [RCTConvert double:option[@"latitude"]];
        lng = [RCTConvert double:option[@"longitude"]];
    }
    CLLocationCoordinate2D coor;
    coor.latitude = lat;
    coor.longitude = lng;
    return coor;
}

-(void)updateMarker:(JMMarkerAnnotation *)annotation option:(NSDictionary *)option {
    CLLocationCoordinate2D coor = [self getCoorFromMarkerOption:option];
    NSString *title = [RCTConvert NSString:option[@"title"]];
    if(title.length == 0) {
        title = nil;
    }

    annotation.coordinate = coor;
    annotation.title = title;
    annotation.icon = [RCTConvert NSString:option[@"icon"]];
    annotation.alpha = [RCTConvert float:option[@"alpha"]];
    annotation.rotate = [RCTConvert float:option[@"rotate"]];
    annotation.flat = [RCTConvert BOOL:option[@"flat"]];
    annotation.infoWindow = [RCTConvert NSDictionary:option[@"infoWindow"]];
}

- (void)addMarker:(JMMarkerAnnotation *)annotation option:(NSDictionary *)option {
    [self updateMarker:annotation option:option];
    [self addAnnotation:annotation];
}

@end
