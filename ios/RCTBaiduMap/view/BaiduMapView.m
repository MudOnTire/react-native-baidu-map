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

@interface BaiduMapView()

@property (nonatomic, strong) NSMutableArray *annotationArray;
@property (nonatomic, strong) JMMarkerAnnotation *markerAnnotation;

@property (nonatomic, strong) OverlayPolyline *trackPolyline;
@property (nonatomic, strong) OverlayPolyline *trackLocusPolyline;
@property (nonatomic, assign) BOOL isTraceEnable;
@property (nonatomic, strong) NSMutableArray *trackLocusArray;

@end

@implementation BaiduMapView

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
    NSUInteger markersCount = [markers count];
    if (_annotationArray == nil) {
        _annotationArray = [[NSMutableArray alloc] init];
    }

    if (markers != nil) {
        for (int i = 0; i < markersCount; i++)  {
            NSDictionary *option = [markers objectAtIndex:i];
            
            JMMarkerAnnotation *annotation = nil;
            if (i < [_annotationArray count]) {
                annotation = [_annotationArray objectAtIndex:i];
            }

            if (annotation == nil) {
                annotation = [[JMMarkerAnnotation alloc] init];
                [self addMarker:annotation option:option];
                [_annotationArray addObject:annotation];
            } else {
                [self updateMarker:annotation option:option];
            }
        }
        
        NSUInteger _annotationsCount = [_annotationArray count];
        if (markersCount < _annotationsCount) {
            NSUInteger start = _annotationsCount - 1;
            for (NSUInteger i = start; i >= markersCount; i--) {
                JMMarkerAnnotation *annotation = [_annotationArray objectAtIndex:i];
                [self removeAnnotation:annotation];
                [_annotationArray removeObject:annotation];
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
    BMKPinAnnotationView *annotationView = (BMKPinAnnotationView *)[self viewForAnnotation:annotation];
    [self updateAnnotationView:annotationView annotation:annotation dataDic:option];
}

- (void)updateAnnotationView:(BMKPinAnnotationView *)annotationView annotation:(JMMarkerAnnotation *)annotation dataDic:(NSDictionary *)dataDic
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
        annotation.coordinate = coor;
        if (![annotation.icon isEqualToString:iconStr] || !dataDic) {
            annotation.icon = iconStr;
            annotationView.image = [annotation getImage];
        }

        if (annotation.rotate != rotate || !dataDic) {
            annotation.rotate = rotate;
//            annotationView.layer.transform = CATransform3DMakeRotation(rotate/360.0, 0, 0, 1);
            annotationView.transform = CGAffineTransformMakeRotation(annotation.rotate);
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

- (void)addMarker:(JMMarkerAnnotation *)annotation option:(NSDictionary *)option {
    [self updateMarker:annotation option:option];
    [self addAnnotation:annotation];
}

#pragma mark - 轨迹

- (void)setTrackPoints:(NSArray *)trackPoints
{
    if (trackPoints.count > 0) {
        _trackPoints = [TrackUtils gpsConversionBaidu:trackPoints];
        [TrackUtils setAllinVisibleRange:self pointArray:_trackPoints];
    } else {
        _trackPoints = nil;
    }

    [self setShowTrack:self.showTrack];
}

- (void)setShowTrack:(BOOL)showTrack
{
    _showTrack = showTrack;
    if (_trackLocusPolyline) {  //移除动态轨迹
        [self.trackLocusPolyline removeFromSuperview];
        _trackLocusPolyline = nil;
    }

    if (self.trackPoints.count < 2 || !showTrack) {
        if (_trackPolyline) {
            [self.trackPolyline removeFromSuperview];
            _trackPolyline = nil;
        }
        return;
    } else {
        if (!_trackPolyline) {
            self.trackPolyline = [[OverlayPolyline alloc] init];
            self.trackPolyline.color = @"0xAA50AE6F";
            self.trackPolyline.width = 1.0;
            self.trackPolyline.points = self.trackPoints;

            [self.trackPolyline addTopMap:self];
        } else {
            self.trackPolyline.points = self.trackPoints;
        }

        if (self.isTraceEnable) {
            NSDictionary *startDic = [NSMutableDictionary dictionaryWithDictionary:self.trackPoints.firstObject];
            [startDic setValue:@"track_icon_start" forKey:@"icon"];
            NSDictionary *carDic = [NSMutableDictionary dictionaryWithDictionary:self.trackPoints.lastObject];
            [carDic setValue:@"icon_car" forKey:@"icon"];
            [self setMarkers:[NSArray arrayWithObjects:startDic, carDic, nil]];
        } else {
            if (!_trackLocusArray) {
                _trackLocusArray = [NSMutableArray array];
            } else {
                [self.trackLocusArray removeAllObjects];
            }
            NSDictionary *startDic = [NSMutableDictionary dictionaryWithDictionary:self.trackPoints.firstObject];
            [startDic setValue:@"track_icon_start" forKey:@"icon"];
            NSDictionary *carDic = [NSMutableDictionary dictionaryWithDictionary:self.trackPoints.firstObject];
            [carDic setValue:@"icon_car" forKey:@"icon"];
            NSDictionary *endDic = [NSMutableDictionary dictionaryWithDictionary:self.trackPoints.lastObject];
            [endDic setValue:@"track_icon_end" forKey:@"icon"];

            [self.trackLocusArray addObjectsFromArray:@[startDic, carDic, endDic]];
            [self setMarkers:self.trackLocusArray];
        }
    }
}

- (void)setTrackPlayInfo:(NSDictionary *)info
{
    if (!info) return;

    int progress = [[info objectForKey:@"progress"] intValue];
    int angle = [[info objectForKey:@"angle"] intValue];
    if (progress >= self.trackPoints.count) {
        return;
    }

    /*这个地方有问题，旋转会被还原，所以屏蔽*/
//    if (progress == 2 && _trackPolyline) {    //擦除轨迹
//        [self.trackPolyline removeFromSuperview];
//        _trackPolyline = nil;
//    } else {
//        [self.trackLocusPolyline removeFromSuperview];
//        _trackLocusPolyline = nil;
//    }
//
//    if (!_trackLocusPolyline) {
//        self.trackLocusPolyline = [[OverlayPolyline alloc] init];
//        self.trackLocusPolyline.color = @"0xAA50AE6F";
//        self.trackLocusPolyline.width = 1.0;
//        [self.trackLocusPolyline addTopMap:self];
//    }
//    self.trackLocusPolyline.points = [self.trackPoints subarrayWithRange:NSMakeRange(0, progress)]; //更新轨迹

    if ([self.trackLocusArray count] > 1) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithDictionary:[self.trackPoints objectAtIndex:progress]];
        [dic setObject:[NSNumber numberWithDouble:angle] forKey:@"rotate"];
        [dic setValue:@"icon_car" forKey:@"icon"];
        [self.trackLocusArray replaceObjectAtIndex:1 withObject:dic];
        [self setMarkers:self.trackLocusArray];
    } else {
        NSMutableDictionary *dic = [NSMutableDictionary dictionaryWithDictionary:[self.trackPoints objectAtIndex:progress]];
        [dic setObject:[NSNumber numberWithDouble:angle] forKey:@"rotate"];
        [dic setValue:@"icon_car" forKey:@"icon"];
        [self setMarkers:[NSArray arrayWithObject:dic]];
    }

    CLLocationCoordinate2D coor = [TrackUtils getCoordinate:[self.trackPoints objectAtIndex:progress]];
    CGPoint point = [self convertCoordinate:coor toPointToView:self];
    if (![self.layer containsPoint:point]) {    //范围放大之后，更新当前坐标，即视角根据车移动
        BMKMapStatus *mapStatus = [self getMapStatus];
        mapStatus.targetGeoPt = coor;
        [self setMapStatus:mapStatus withAnimation:YES];
    }
}

- (void)setTracePoints:(NSArray *)tracePoints
{
    self.isTraceEnable = YES;
    [self setTrackPoints:tracePoints];
}

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
