//
//  MarkerPointAnnotation.h
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/2/28.
//  Copyright © 2019 jimi. All rights reserved.
//

#import <BaiduMapAPI_Map/BMKPointAnnotation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface JMMarkerAnnotation : BMKPointAnnotation

@property (nonatomic, strong) NSString *icon;
@property (nonatomic, assign) float alpha;
@property (nonatomic, assign) float rotate;
@property (nonatomic, assign) BOOL flat;
@property (nonatomic, strong) NSDictionary *infoWindow;

- (UIImage *)getImage;

@end

NS_ASSUME_NONNULL_END
