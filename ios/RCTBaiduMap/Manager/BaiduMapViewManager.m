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
#import "JMCircle.h"
#import "JMPolygon.h"
#import "JMPolyline.h"
#import "JMArc.h"
#import "TrackInfoView.h"

@implementation BaiduMapViewManager

RCT_EXPORT_MODULE(BaiduMapView)

RCT_EXPORT_VIEW_PROPERTY(mapType, int)
RCT_EXPORT_VIEW_PROPERTY(zoom, float)
RCT_EXPORT_VIEW_PROPERTY(zoomControlsVisible, BOOL)
RCT_EXPORT_VIEW_PROPERTY(trafficEnabled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(baiduHeatMapEnabled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(markers, NSArray*)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
RCT_CUSTOM_VIEW_PROPERTY(center, CLLocationCoordinate2D, BaiduMapView) {
    [view setCenterCoordinate:json ? [RCTConvert CLLocationCoordinate2D:json] : defaultView.centerCoordinate];
}

RCT_EXPORT_VIEW_PROPERTY(trackPoints, NSArray*)
RCT_EXPORT_VIEW_PROPERTY(showTrack, BOOL)
RCT_EXPORT_VIEW_PROPERTY(tracePoints, NSArray*)
RCT_CUSTOM_VIEW_PROPERTY(trackPlayInfo, NSDictionary, BaiduMapView) {
    [view setTrackPlayInfo:[RCTConvert NSDictionary:json]];
}
RCT_CUSTOM_VIEW_PROPERTY(infoWindows, NSDictionary, BaiduMapView) {
    [view setInfoWindows:[RCTConvert NSDictionary:json]];
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
//    mapView.showMapScaleBar = YES;  //比例尺
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
        BMKPinAnnotationView *annotationView = (BMKPinAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:@"JMMarkerAnnotation"];
        if (!annotationView) {
            annotationView = [[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"JMMarkerAnnotation"];
        }
        annotationView.pinColor = BMKPinAnnotationColorPurple;
        annotationView.animatesDrop = YES;
        annotationView.hidePaopaoWhenSingleTapOnMap = NO;
        annotationView.hidePaopaoWhenDoubleTapOnMap = NO;
        annotationView.hidePaopaoWhenTwoFingersTapOnMap = NO;
        annotationView.hidePaopaoWhenSelectOthers = NO;
        annotationView.hidePaopaoWhenDragOthers = NO;
        annotationView.hidePaopaoWhenDrag = NO;

        JMMarkerAnnotation *markerAnnotation = (JMMarkerAnnotation *)annotation;
        [(BaiduMapView *)mapView updateAnnotationView:annotationView annotation:markerAnnotation dataDic:nil];

        if (markerAnnotation.infoWindow != nil) {
            NSArray *nibContents = [[NSBundle mainBundle] loadNibNamed:@"TrackInfoView" owner:nil options:nil];
            TrackInfoView *popView = [nibContents lastObject];
            [popView setModelDic:markerAnnotation.infoWindow];
            popView.tag = 1024;
            
            BMKActionPaopaoView *pView = [[BMKActionPaopaoView alloc] initWithCustomView:popView];
            pView.frame = popView.bounds;
            ((BMKPinAnnotationView *)annotationView).paopaoView = pView;

            dispatch_async(dispatch_get_main_queue(), ^{
                [mapView selectAnnotation:annotation animated:YES];
            });
        }

        return annotationView;
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

- (void)sendEvent:(BaiduMapView *) mapView params:(NSDictionary *) params {
    if (!mapView.onChange) {
        return;
    }
    mapView.onChange(params);
}

@end
