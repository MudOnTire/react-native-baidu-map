//
//  MarkerPointAnnotation.m
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/2/28.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "JMMarkerAnnotation.h"

@implementation JMMarkerAnnotation

- (UIImage *)getImage
{
    if (self.icon && ![self.icon isEqualToString:@""]) {
        UIImage *img = [UIImage imageWithContentsOfFile:self.icon];
        if (img) {
            return img;
        } else {
            return [UIImage imageNamed:self.icon];
        }
    }

    return nil;
}
@end
