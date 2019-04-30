//
//  RCTBaiduMap.m
//  RCTBaiduMap
//
//  Created by lovebing on 4/17/2016.
//  Copyright © 2016 lovebing.org. All rights reserved.
//

#import "BaiduMapView.h"
#import "OverlayCircle.h"
#import "OverlayInfoWindow.h"
#import "OverlayPolyline.h"
#import "TrackUtils.h"
#import "TrackInfoView.h"
#import <BaiduMapAPI_Map/BMKPolylineView.h>
#import "JMPinAnnotationView.h"

@interface BaiduMapView()

@property (nonatomic, strong) NSMutableArray *annotationArray;
@property (nonatomic, strong) JMMarkerAnnotation *markerAnnotation;

@end

@implementation BaiduMapView

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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

- (void)visibleBaiduOverlay:(NSNotification *)noti
{
    NSDictionary *data = noti.object;
    if (data) {
        JMPolyline *overlay = [data objectForKey:@"Overlay"];
        BOOL visible = [[data objectForKey:@"visible"] boolValue];
        if (overlay && overlay.visible != visible) {
            overlay.visible = visible;
            if (visible) {
                [self addOverlay:overlay];
            } else {
                [self removeOverlay:overlay];
            }
        }
    }
}

-(void)setZoom:(float)zoom {
    self.zoomLevel = zoom;
}

- (void)setZoomControlsVisible:(BOOL)zoomControlsVisible
{
//    self.showMapScaleBar = zoomControlsVisible;
}

- (void)setCenterLatLng:(NSDictionary *)LatLngObj {
    double lat = [RCTConvert double:LatLngObj[@"lat"]];
    double lng = [RCTConvert double:LatLngObj[@"lng"]];
    CLLocationCoordinate2D point = CLLocationCoordinate2DMake(lat, lng);
    self.centerCoordinate = point;
}

#pragma mark - Marker

- (void)setMarker:(NSDictionary *)option {
    if (option != nil) {
        if(_markerAnnotation == nil) {
            _markerAnnotation = [[JMMarkerAnnotation alloc] init];
            [self addMarker:_markerAnnotation option:option];
        }
        else {
            [self updateMarker:_markerAnnotation option:option];
        }
    }
}

- (void)setMarkers:(NSArray *)markers
{
    NSInteger markersCount = [markers count];
    if (_annotationArray == nil) {
        _annotationArray = [[NSMutableArray alloc] init];
    }

    if (markers != nil) {
        for (NSInteger i = 0, markersMax = markersCount; i < markersMax; i++)  {
            NSDictionary *option = [markers objectAtIndex:i];
            
            JMMarkerAnnotation *annotation = nil;
            if (i < [_annotationArray count]) {
                annotation = [self.annotationArray objectAtIndex:i];
            }

            if (![RCTConvert BOOL:option[@"visible"]]) {  //隐藏则移除此Marker;
                markersCount --;
            } else {
                if (annotation == nil) {
                    annotation = [[JMMarkerAnnotation alloc] init];
                    [self addMarker:annotation option:option];
                    [self.annotationArray addObject:annotation];
                } else {
                    [self updateMarker:annotation option:option];
                }
            }
        }
        
        NSInteger annotationsCount = [_annotationArray count];
        if (markersCount < annotationsCount) {
            NSInteger start = annotationsCount - 1;
            for (NSInteger i = start; i >= markersCount ; i--) {
                JMMarkerAnnotation *annotation = [self.annotationArray objectAtIndex:i];
                [self removeAnnotation:annotation];
                [self.annotationArray removeObject:annotation];
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

- (void)addMarker:(JMMarkerAnnotation *)annotation option:(NSDictionary *)option {
    [self updateMarker:annotation option:option];
    [self addAnnotation:annotation];
}

-(void)updateMarker:(JMMarkerAnnotation *)annotation option:(NSDictionary *)option {
    JMPinAnnotationView *annotationView = (JMPinAnnotationView *)[self viewForAnnotation:annotation];
    [self updateAnnotationView:annotationView annotation:annotation dataDic:option];
}

- (void)updateAnnotationView:(JMPinAnnotationView *)annotationView annotation:(JMMarkerAnnotation *)annotation dataDic:(NSDictionary *)dataDic
{
    if (!annotation) return;

    CLLocationCoordinate2D coor = annotation.coordinate;
    NSString *title = annotation.title;
    float alpha = annotation.alpha;
    BOOL flat =  annotation.flat;
    NSDictionary *infoWindow = annotation.infoWindow;
    NSString *iconStr = annotation.icon;
    float rotate = annotation.rotate;
    int tag = annotation.tag;

    if (dataDic) {
        tag = [RCTConvert int:dataDic[@"tag"]];
        title = [RCTConvert NSString:dataDic[@"title"]];
        alpha = [RCTConvert float:dataDic[@"alpha"]];
        flat = [RCTConvert BOOL:dataDic[@"flat"]];
        coor = [self getCoorFromMarkerOption:dataDic];
        iconStr = [RCTConvert NSString:dataDic[@"icon"]];
        rotate = [RCTConvert float:dataDic[@"rotate"]];
        infoWindow = [RCTConvert NSDictionary:dataDic[@"infoWindow"]];
    }

    if (annotationView) {
        if (![annotation.icon isEqualToString:iconStr] || !dataDic) {
            annotation.icon = iconStr;
            annotation.iconImage = [annotation getImage];
            annotationView.image = annotation.iconImage;
        }

        if (annotation.rotate != rotate || !dataDic) {
            annotation.rotate = rotate;
            annotationView.image = [JMMarkerAnnotation imageRotated:annotation.iconImage radians:annotation.rotate];
        }
        annotation.coordinate = coor;

        if (tag < 0 && annotationView.paopaoView) {
            [annotationView.paopaoView removeFromSuperview];
        }
    } else {
        annotation.coordinate = coor;
    }

    annotation.tag = tag;
    annotation.icon = iconStr;
    annotation.title = title;
    annotation.alpha = alpha;
    annotation.flat = flat;
    annotation.rotate = rotate;
    annotation.infoWindow = infoWindow;
}

#pragma mark - 轨迹

- (void)setCorrectPerspective:(NSDictionary *)info
{
    if (!info) return;

    double latitude = [[info objectForKey:@"latitude"] doubleValue];
    double longitude = [[info objectForKey:@"longitude"] doubleValue];

    if (!latitude && !longitude) {
        return;
    }

    CLLocationCoordinate2D coor = CLLocationCoordinate2DMake(latitude, longitude);
    CGPoint point = [self convertCoordinate:coor toPointToView:self];
    if (![self.layer containsPoint:point]) {    //范围放大之后，更新当前坐标，即视角根据车移动
        BMKMapStatus *mapStatus = [self getMapStatus];
        mapStatus.targetGeoPt = coor;
        [self setMapStatus:mapStatus withAnimation:YES];
    }
}

- (void)setVisualRange:(NSArray *)tracePoints
{
    if (tracePoints.count > 0) {
        [TrackUtils setVisualRange:self pointArray:tracePoints];
    }
}

#pragma mark - InfoWindow

- (void)setInfoWindows:(NSDictionary *)infoDic
{
    if (!infoDic) return;
    NSNumber *tagValue = [infoDic objectForKey:@"tag"];
    if (!tagValue) return;
    int tag = [tagValue intValue];

    NSArray *array = [NSArray arrayWithArray:self.annotations];
    for (JMMarkerAnnotation *annotation in array) {
        if (annotation.tag >= 0 && annotation.tag == tag) {
            BMKPinAnnotationView *annotationView = (BMKPinAnnotationView *)[self viewForAnnotation:annotation];
            if (annotationView && annotationView.paopaoView) {
                TrackInfoView *infoView = [annotationView.paopaoView viewWithTag:(tag + 1)];
                if (infoView) {
                    [infoView setModelDic:infoDic];
                    NSLog(@"InfoWindows:%@", infoDic);
                }
            } else {
                annotation.infoWindow = infoDic;
                [self removeAnnotation:annotation];
                [self addAnnotation:annotation];
            }
            break;
        }
    }
}

@end
