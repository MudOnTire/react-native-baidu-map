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

@interface BaiduMapView : BMKMapView <BMKMapViewDelegate>

@property (nonatomic,copy) RCTBubblingEventBlock onChange;
@property (nonatomic,strong) NSArray *trackPoints;
@property (nonatomic,assign) BOOL showTrack;

- (void)removeBaiduOverlay:(NSNotification *)noti;

-(void)setZoom:(float)zoom;
-(void)setCenterLatLng:(NSDictionary *)LatLngObj;

-(void)setMarker:(NSDictionary *)Options;
-(void)setMarkers:(NSArray *)markers;

@end

#endif
