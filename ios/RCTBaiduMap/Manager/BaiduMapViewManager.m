//
//  RCTBaiduMapViewManager.m
//  RCTBaiduMap
//
//  Created by lovebing on Aug 6, 2016.
//  Copyright © 2016 lovebing.org. All rights reserved.
//

#import "BaiduMapViewManager.h"
#import <BaiduMapAPI_Map/BMKCircleView.h>
#import <BaiduMapAPI_Map/BMKPolygonView.h>
#import <BaiduMapAPI_Map/BMKPolylineView.h>
#import <BaiduMapAPI_Map/BMKArclineView.h>
#import "OverlayView.h"
#import "JMMarkerAnnotation.h"
#import "JMCircle.h"
#import "JMPolygon.h"
#import "JMPolyline.h"
#import "JMArc.h"

@implementation BaiduMapViewManager

RCT_EXPORT_MODULE(BaiduMapView)

RCT_EXPORT_VIEW_PROPERTY(mapType, int)
RCT_EXPORT_VIEW_PROPERTY(zoom, float)
RCT_EXPORT_VIEW_PROPERTY(trafficEnabled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(baiduHeatMapEnabled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(markers, NSArray*)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)

RCT_EXPORT_VIEW_PROPERTY(trackPoints, NSArray*) //轨迹点
RCT_EXPORT_VIEW_PROPERTY(showTrack, BOOL)

RCT_CUSTOM_VIEW_PROPERTY(center, CLLocationCoordinate2D, BaiduMapView) {
    [view setCenterCoordinate:json ? [RCTConvert CLLocationCoordinate2D:json] : defaultView.centerCoordinate];
}


+(void)initSDK:(NSString*)key {
    
    BMKMapManager *_mapManager = [[BMKMapManager alloc] init];
    BOOL ret = [_mapManager start:key  generalDelegate:nil];
    if (!ret) {
        NSLog(@"manager start failed!");
    }
}

- (UIView *)view {
    BaiduMapView* mapView = [[BaiduMapView alloc] init];
    mapView.delegate = self;
    
    [[NSNotificationCenter defaultCenter] addObserver:mapView selector:@selector(removeBaiduOverlay:) name:kBaiduMapViewRemoveOverlay object:nil];
    return mapView;
}


#pragma mark - BMKMapViewDelegate

- (void)mapview:(BMKMapView *)mapView onDoubleClick:(CLLocationCoordinate2D)coordinate {
    NSLog(@"onDoubleClick");
    NSDictionary* event = @{
                            @"type": @"onMapDoubleClick",
                            @"params": @{
                                    @"latitude": @(coordinate.latitude),
                                    @"longitude": @(coordinate.longitude)
                                    }
                            };
    [self sendEvent:(BaiduMapView *)mapView params:event];
}

- (void)mapView:(BMKMapView *)mapView onClickedMapBlank:(CLLocationCoordinate2D)coordinate {
    NSLog(@"onClickedMapBlank");
    NSDictionary* event = @{
                            @"type": @"onMapClick",
                            @"params": @{
                                    @"latitude": @(coordinate.latitude),
                                    @"longitude": @(coordinate.longitude)
                                    }
                            };
    [self sendEvent:(BaiduMapView *)mapView params:event];
}

-(void)mapViewDidFinishLoading:(BMKMapView *)mapView {
    NSDictionary* event = @{
                            @"type": @"onMapLoaded",
                            @"params": @{}
                            };
    [self sendEvent:(BaiduMapView *)mapView params:event];
}

-(void)mapView:(BMKMapView *)mapView didSelectAnnotationView:(BMKAnnotationView *)view {
    NSDictionary* event = @{
                            @"type": @"onMarkerClick",
                            @"params": @{
                                    @"title": [[view annotation] title],
                                    @"position": @{
                                            @"latitude": @([[view annotation] coordinate].latitude),
                                            @"longitude": @([[view annotation] coordinate].longitude)
                                            }
                                    }
                            };
    [self sendEvent:(BaiduMapView *)mapView params:event];
}

- (void)mapView:(BMKMapView *)mapView onClickedMapPoi:(BMKMapPoi *)mapPoi {
    NSDictionary* event = @{
                            @"type": @"onMapPoiClick",
                            @"params": @{
                                    @"name": mapPoi.text,
                                    @"uid": mapPoi.uid,
                                    @"latitude": @(mapPoi.pt.latitude),
                                    @"longitude": @(mapPoi.pt.longitude)
                                    }
                            };
    [self sendEvent:(BaiduMapView *)mapView params:event];
}

- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id <BMKAnnotation>)annotation {
    if ([annotation isKindOfClass:[JMMarkerAnnotation class]]) {
        BMKPinAnnotationView *newAnnotationView = [[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"JMMarkerAnnotation"];
        newAnnotationView.pinColor = BMKPinAnnotationColorPurple;
        newAnnotationView.animatesDrop = YES;
        newAnnotationView.hidePaopaoWhenSingleTapOnMap = YES;
        newAnnotationView.hidePaopaoWhenDoubleTapOnMap = YES;
        newAnnotationView.hidePaopaoWhenTwoFingersTapOnMap = YES;
        newAnnotationView.hidePaopaoWhenSelectOthers = YES;
        newAnnotationView.hidePaopaoWhenDragOthers = YES;

        JMMarkerAnnotation *markerAnnotation = (JMMarkerAnnotation *)annotation;
        if (markerAnnotation.icon && ![markerAnnotation.icon isEqualToString:@""]) {
            UIImage *img = [UIImage imageWithContentsOfFile:markerAnnotation.icon];
            if (img) {
                newAnnotationView.image = img;
            } else {
                newAnnotationView.image = [UIImage imageNamed:markerAnnotation.icon];
            }
        }

        if (markerAnnotation.infoWindow != nil) {
            UIView * popView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, [[markerAnnotation.infoWindow objectForKey:@"width"] doubleValue], [[markerAnnotation.infoWindow objectForKey:@"height"] doubleValue])];
            popView.backgroundColor = [UIColor whiteColor];
            [popView.layer setMasksToBounds:YES];
            [popView.layer setCornerRadius:3.0];

            NSNumber *alphaValue = [markerAnnotation.infoWindow objectForKey:@"alpha"];
            if (alphaValue) {
                popView.alpha = [alphaValue doubleValue];
            } else {
                popView.alpha = 1.0;
            }

            NSNumber *visibleValue = [markerAnnotation.infoWindow objectForKey:@"visible"];
            if (visibleValue) {
                popView.hidden = ![visibleValue boolValue];
            }

            UILabel *titleLB = [[UILabel alloc] initWithFrame:popView.bounds];
            titleLB.text = [markerAnnotation.infoWindow objectForKey:@"title"];
            titleLB.textAlignment = NSTextAlignmentCenter;
            titleLB.numberOfLines = 0;
            titleLB.font = [UIFont systemFontOfSize:14.0];
            [popView addSubview:titleLB];

            BMKActionPaopaoView * pView = [[BMKActionPaopaoView alloc] initWithCustomView:popView];
            pView.frame = popView.bounds;
            ((BMKPinAnnotationView *)newAnnotationView).paopaoView = pView;
        }

        return newAnnotationView;
    } else if ([annotation isKindOfClass:[BMKPointAnnotation class]]) {
        BMKPinAnnotationView *newAnnotationView = [[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"myAnnotation"];
        newAnnotationView.pinColor = BMKPinAnnotationColorPurple;
        newAnnotationView.animatesDrop = YES;
        return newAnnotationView;
    }
    return nil;
}

-(void)mapStatusDidChanged: (BMKMapView *)mapView {
    CLLocationCoordinate2D targetGeoPt = [mapView getMapStatus].targetGeoPt;
    NSDictionary* event = @{
                            @"type": @"onMapStatusChange",
                            @"params": @{
                                    @"target": @{
                                            @"latitude": @(targetGeoPt.latitude),
                                            @"longitude": @(targetGeoPt.longitude)
                                            },
                                    @"zoom": @(mapView.zoomLevel),
                                    @"overlook": @(mapView.overlooking)
                                    }
                            };
    [self sendEvent:(BaiduMapView *)mapView params:event];
}

- (void)mapView:(BMKMapView *)mapView annotationViewForBubble:(BMKAnnotationView *)view
{
    NSDictionary* event = @{
                            @"type": @"onBubbleOfMarkerClick",
                            @"params": @{
                                    @"title": [[view annotation] title],
                                    @"position": @{
                                            @"latitude": @([[view annotation] coordinate].latitude),
                                            @"longitude": @([[view annotation] coordinate].longitude)
                                            }
                                    }
                            };
    [self sendEvent:(BaiduMapView *)mapView params:event];
}

- (BMKOverlayView *)mapView:(BMKMapView *)mapView viewForOverlay:(id <BMKOverlay>)overlay
{
    if ([overlay isKindOfClass:[JMCircle class]]) {
        BMKCircleView *view = (BMKCircleView *)[mapView viewForOverlay:overlay];
        if (view == nil) {
            view = [[BMKCircleView alloc] initWithOverlay:overlay];
        }
        view.fillColor = ((JMCircle *)overlay).fillColor;
        view.strokeColor = ((JMCircle *)overlay).stroke.strokeColor;
        view.lineWidth = ((JMCircle *)overlay).stroke.lineWidth;
        return view;
    } else if ([overlay isKindOfClass:[JMPolygon class]]) {
        BMKPolygonView *view = (BMKPolygonView *)[mapView viewForOverlay:overlay];
        if (view == nil) {
            view = [[BMKPolygonView alloc] initWithPolygon:overlay];
        }
        view.fillColor = ((JMPolygon *)overlay).fillColor;
        view.strokeColor = ((JMPolygon *)overlay).stroke.strokeColor;
        view.lineWidth = ((JMPolygon *)overlay).stroke.lineWidth;
        return view;
    } else if ([overlay isKindOfClass:[JMPolyline class]]) {
        BMKPolylineView *view = (BMKPolylineView *)[mapView viewForOverlay:overlay];
        if (view == nil) {
            view = [[BMKPolylineView alloc] initWithPolyline:overlay];
        }
        view.fillColor = ((JMPolyline *)overlay).color;
        view.strokeColor = ((JMPolyline *)overlay).color;
        view.lineWidth = ((JMPolyline *)overlay).width;
        return view;
    } else if ([overlay isKindOfClass:[JMArc class]]) {
        BMKArclineView *view = (BMKArclineView *)[mapView viewForOverlay:overlay];
        if (view == nil) {
            view = [[BMKArclineView alloc] initWithArcline:overlay];
        }
        view.fillColor = ((JMArc *)overlay).color;
        view.strokeColor = ((JMArc *)overlay).color;
        view.lineWidth = ((JMArc *)overlay).width;
        return view;
    }

    return nil;
}

-(void)sendEvent:(BaiduMapView *) mapView params:(NSDictionary *) params {
    if (!mapView.onChange) {
        return;
    }
    mapView.onChange(params);
}

@end
