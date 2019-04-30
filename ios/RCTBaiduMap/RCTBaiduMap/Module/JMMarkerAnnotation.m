//
//  MarkerPointAnnotation.m
//  JMSmallAppEngine
//
//  Created by lzj<lizhijian_21@163.com> on 2019/2/28.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "JMMarkerAnnotation.h"

@implementation JMMarkerAnnotation

- (instancetype)init
{
    self = [super init];
    if (self) {
        _tag = -1;
    }
    return self;
}

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

+ (UIImage *)imageRotated:(UIImage *)image radians:(CGFloat)radians
{
    UIView *rotatedViewBox = [[UIView alloc] initWithFrame:CGRectMake(0,0,image.size.width, image.size.height)];
    rotatedViewBox.transform = CGAffineTransformMakeRotation(radians);
    CGSize rotatedSize = rotatedViewBox.frame.size;

    UIGraphicsBeginImageContextWithOptions(rotatedSize, false, [UIScreen mainScreen].scale);
    CGContextRef bitmap = UIGraphicsGetCurrentContext();
    CGContextTranslateCTM(bitmap, rotatedSize.width/2.0, rotatedSize.height/2.0);
    CGContextRotateCTM(bitmap, radians);
    CGContextScaleCTM(bitmap, 1.0, -1.0);
    CGContextDrawImage(bitmap, CGRectMake(-image.size.width / 2.0, -image.size.height / 2.0, image.size.width, image.size.height), [image CGImage]);

    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();

    return newImage;
}

@end
