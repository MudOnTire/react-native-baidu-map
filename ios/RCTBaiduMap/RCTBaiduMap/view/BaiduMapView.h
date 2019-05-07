//
//  RCTBaiduMap.h
//  RCTBaiduMap
//
//  Created by lovebing on 4/17/2016.
//  Copyright © 2016 lovebing.org. All rights reserved.
//

#ifndef BaiduMapView_h
#define BaiduMapView_h


#import <React/RCTViewManager.h>
#import <React/RCTConvert+CoreLocation.h>
#import <BaiduMapAPI_Map/BMKMapView.h>
#import <BaiduMapAPI_Map/BMKPinAnnotationView.h>
#import <BaiduMapAPI_Map/BMKPointAnnotation.h>
#import <BaiduMapAPI_Map/BMKCircle.h>
#import <UIKit/UIKit.h>
#import "JMMarkerAnnotation.h"
#import "JMPinAnnotationView.h"

@interface BaiduMapView : BMKMapView <BMKMapViewDelegate>

@property (nonatomic,copy) RCTBubblingEventBlock onChange;
@property (nonatomic,assign) BOOL zoomControlsVisible;
@property (nonatomic,strong) NSArray *visualRange;

- (void)updateAnnotationView:(JMPinAnnotationView *)annotationView annotation:(JMMarkerAnnotation *)annotation dataDic:(NSDictionary *)dataDic;

- (void)removeBaiduOverlay:(NSNotification *)noti;

- (void)setZoom:(float)zoom;
- (void)setCenterLatLng:(NSDictionary *)LatLngObj;

- (void)setMarker:(NSDictionary *)Options;
- (void)setMarkers:(NSArray *)markers;

- (void)setCorrectPerspective:(NSDictionary *)infoDic;

- (void)setInfoWindows:(NSDictionary *)infoDic;

@end

#endif