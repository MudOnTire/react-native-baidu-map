//
//  PoiSearchModule.m
//  JiMiPlatfromProject
//
//  Created by lzj<lizhijian_21@163.com> on 2019/4/15.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "PoiSearchModule.h"
#import "GeolocationModule.h"
#import "ZJRequestDataServer.h"

@implementation PoiSearchModule

@synthesize bridge = _bridge;

static BMKPoiSearch *poiSearch;

- (BMKPoiSearch *)getPoiSearch {
    if (poiSearch == nil) {
        poiSearch = [[BMKPoiSearch alloc]init];
    }
    return poiSearch;
}

RCT_EXPORT_MODULE(BaiduSearchModule);

RCT_EXPORT_METHOD(poiSearchNearby:(double)lat lng:(double)lng radius:(int)radius keyword:(NSString *)keyword)
{
    BMKPoiSearch *poiSearch = [self getPoiSearch];
    poiSearch.delegate = self;

    BMKPOINearbySearchOption *nearbySearchOption = [[BMKPOINearbySearchOption alloc] init];
    nearbySearchOption.location = CLLocationCoordinate2DMake(lat, lng);
    nearbySearchOption.radius = radius;
    nearbySearchOption.keywords = [NSArray arrayWithObject:keyword];
    nearbySearchOption.pageSize = 3;

    [poiSearch poiSearchNearBy:nearbySearchOption];
}

RCT_EXPORT_METHOD(requestSuggestion:(NSString *)city keyword:(NSString *)keyword)
{
    BMKPoiSearch *poiSearch = [self getPoiSearch];
    poiSearch.delegate = self;

    BMKPOICitySearchOption *citySearchOption = [[BMKPOICitySearchOption alloc] init];
    citySearchOption.city = city ? city: @"";
    citySearchOption.keyword = keyword;
    citySearchOption.pageSize = 3;

    [poiSearch poiSearchInCity:citySearchOption];

//    NSMutableDictionary *para = [NSMutableDictionary dictionary];
//    [para setObject:keyword forKey:@"query"];
//    [para setObject:@"131" forKey:@"region"];
//    [para setObject:@"json" forKey:@"output"];
//    [para setObject:@"XliivossrZ4sE123456pFe7tzWyENF" forKey:@"ak"];
//
//    [ZJRequestDataServer requestWithURL:@"http://api.map.baidu.com/place/v2/suggestion" isFormData:NO params:para httpMethod:@"GET" completedBlock:^(id result) {
//
//    } failureBlock:^(NSError *error) {
//
//    }];
}

- (void)onGetPoiResult:(BMKPoiSearch*)searcher result:(BMKPOISearchResult*)poiResult errorCode:(BMKSearchErrorCode)errorCode
{
    NSMutableDictionary *body = [self getEmptyBody];
    if (errorCode == BMK_SEARCH_NO_ERROR) {
        NSMutableArray *dataArray = [NSMutableArray array];
        for (BMKPoiInfo *poiInfo in poiResult.poiInfoList) {
            NSMutableDictionary *dataDic = [NSMutableDictionary dictionary];
            if (poiInfo.city != nil) [dataDic setObject:poiInfo.city forKey:@"city"];
            else continue;
            if (poiInfo.area != nil) [dataDic setObject:poiInfo.area forKey:@"district"];
            if (poiInfo.name != nil) [dataDic setObject:poiInfo.name forKey:@"key"];
            [dataDic setObject:[NSNumber numberWithDouble:poiInfo.pt.latitude] forKey:@"latitude"];
            [dataDic setObject:[NSNumber numberWithDouble:poiInfo.pt.longitude] forKey:@"longitude"];
            [dataArray addObject:dataDic];
        }
        body[@"sugList"] = dataArray;
    } else {
        body[@"errcode"] = [NSString stringWithFormat:@"%d", errorCode];
        body[@"errmsg"] = [GeolocationModule getSearchErrorInfo:errorCode];
    }
    [self sendEvent:@"onGetSuggestionResult" body:body];
}

-(void)sendEvent:(NSString *)name body:(NSMutableDictionary *)body {
    [self.bridge.eventDispatcher sendDeviceEventWithName:name body:body];
}

@end
