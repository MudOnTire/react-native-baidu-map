//
//  PoiSearchModule.h
//  JiMiPlatfromProject
//
//  Created by lzj<lizhijian_21@163.com> on 2019/4/15.
//  Copyright © 2019 jimi. All rights reserved.
//

#import <BaiduMapAPI_Search/BMKPoiSearch.h>
#import "BaseModule.h"
#import "BaiduMapViewManager.h"

NS_ASSUME_NONNULL_BEGIN

@interface PoiSearchModule : BaseModule <BMKPoiSearchDelegate>

- (void)sendEvent:(NSString *)name body:(NSMutableDictionary *)body;

@end

NS_ASSUME_NONNULL_END
