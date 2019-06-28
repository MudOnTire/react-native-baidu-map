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
#import "JMPinAnnotationView.h"

@implementation BaiduMapViewManager

RCT_EXPORT_MODULE(BaiduMapView)

RCT_EXPORT_VIEW_PROPERTY(mapType, int)
RCT_EXPORT_VIEW_PROPERTY(zoom, float)
RCT_EXPORT_VIEW_PROPERTY(zoomControlsVisible, BOOL)
RCT_EXPORT_VIEW_PROPERTY(trafficEnabled, BOOL)          //路况图层
RCT_EXPORT_VIEW_PROPERTY(baiduHeatMapEnabled, BOOL)     //热力图
RCT_EXPORT_VIEW_PROPERTY(buildingsEnabled, BOOL)        //3D建筑物
RCT_EXPORT_VIEW_PROPERTY(overlookEnabled, BOOL)         //俯仰角
RCT_EXPORT_VIEW_PROPERTY(markers, NSArray*)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(visualRange, NSArray*)         //可视范围
RCT_CUSTOM_VIEW_PROPERTY(center, CLLocationCoordinate2D, BaiduMapView) {
    [view setCenterCoordinate:json ? [RCTConvert CLLocationCoordinate2D:json] : defaultView.centerCoordinate];
}
RCT_CUSTOM_VIEW_PROPERTY(correctPerspective, NSDictionary, BaiduMapView) {
    [view setCorrectPerspective:[RCTConvert NSDictionary:json]];
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

-(void)mapView:(BMKMapView *)mapView didSelectAnnotationView:(BMKAnnotationView *)view
{
    NSString *title = [[view annotation] title];
    if (!title) {
        title = @"";
    }
    NSDictionary* event = @{
                            @"type": @"onMarkerClick",
                            @"params": @{
                                    @"title": title,
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
        JMMarkerAnnotation *markerAnnotation = (JMMarkerAnnotation *)annotation;
        if (markerAnnotation.tag >= 0 && markerAnnotation.infoWindow != nil) {  //自定义Infowindow
            JMPinAnnotationView *annotationView = (JMPinAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:@"JMMarkerJMPinAnnotation"];
            if (!annotationView) {
                annotationView = [[JMPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"JMMarkerJMPinAnnotation"];
            }
            //        annotationView.pinColor = BMKPinAnnotationColorPurple;
            //        annotationView.animatesDrop = YES;
            annotationView.hidePaopaoWhenSingleTapOnMap = NO;
            annotationView.hidePaopaoWhenDoubleTapOnMap = NO;
            annotationView.hidePaopaoWhenTwoFingersTapOnMap = NO;
            annotationView.hidePaopaoWhenSelectOthers = NO;
            annotationView.hidePaopaoWhenDragOthers = NO;
            annotationView.hidePaopaoWhenDrag = NO;

            [(BaiduMapView *)mapView updateAnnotationView:annotationView annotation:markerAnnotation dataDic:nil];

            if (markerAnnotation.tag >= 0 && markerAnnotation.infoWindow != nil) {
                NSArray *nibContents = [[NSBundle mainBundle] loadNibNamed:@"TrackInfoView" owner:nil options:nil];
                TrackInfoView *popView = [nibContents lastObject];
                if (popView) {
                    [popView setModelDic:markerAnnotation.infoWindow];

                    BMKActionPaopaoView *pView = [[BMKActionPaopaoView alloc] initWithCustomView:popView];
                    pView.frame = popView.bounds;
                    ((JMPinAnnotationView *)annotationView).paopaoView = pView;
                }

                dispatch_async(dispatch_get_main_queue(), ^{
                    [mapView selectAnnotation:annotation animated:YES];
                });
            }
            return annotationView;
        } else if (markerAnnotation.infoWindow != nil) {
            BMKPinAnnotationView *annotationView = [[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"JMMarkerBMKPinAnnotation"];
            if (!annotationView) {
                annotationView = [[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"JMMarkerBMKPinAnnotation"];
            }
            annotationView.pinColor = BMKPinAnnotationColorPurple;
            annotationView.animatesDrop = YES;
            annotationView.hidePaopaoWhenSingleTapOnMap = YES;
            annotationView.hidePaopaoWhenDoubleTapOnMap = YES;
            annotationView.hidePaopaoWhenTwoFingersTapOnMap = YES;
            annotationView.hidePaopaoWhenSelectOthers = YES;
            annotationView.hidePaopaoWhenDragOthers = YES;

            UIView * popView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, [[markerAnnotation.infoWindow objectForKey:@"width"] doubleValue], [[markerAnnotation.infoWindow objectForKey:@"height"] doubleValue])];
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

            if (markerAnnotation.icon && ![markerAnnotation.icon isEqualToString:@""]) {
                UIImage *img = [UIImage imageWithContentsOfFile:markerAnnotation.icon];
                if (img) {
                    annotationView.image = img;
                } else {
                    annotationView.image = [UIImage imageNamed:markerAnnotation.icon];
                }
            }

            UILabel *titleLB = [[UILabel alloc] initWithFrame:popView.bounds];
            titleLB.text = [markerAnnotation.infoWindow objectForKey:@"title"];
            titleLB.textAlignment = NSTextAlignmentCenter;
            titleLB.numberOfLines = 0;
            titleLB.font = [UIFont systemFontOfSize:14.0];
            [popView addSubview:titleLB];

            BMKActionPaopaoView * pView = [[BMKActionPaopaoView alloc] initWithCustomView:popView];
            pView.frame = popView.bounds;
            annotationView.paopaoView = pView;
            return annotationView;
        }
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
    NSString *title = [[view annotation] title];
    if (!title) {
        title = @"";
    }
    NSDictionary* event = @{
                            @"type": @"onBubbleOfMarkerClick",
                            @"params": @{
                                    @"title": title,
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
