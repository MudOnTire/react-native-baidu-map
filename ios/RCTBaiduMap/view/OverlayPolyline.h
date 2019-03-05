//
//  OverlayPolyline.h
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/3/1.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "OverlayView.h"
#import "Stroke.h"

NS_ASSUME_NONNULL_BEGIN

@interface OverlayPolyline : OverlayView

@property (nonatomic,strong) NSArray *points;
@property (nonatomic,copy) NSString *color;
@property (nonatomic,assign) float width;

@end

NS_ASSUME_NONNULL_END
