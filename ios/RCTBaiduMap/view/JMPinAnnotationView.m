//
//  JMPinAnnotationView.m
//  JiMiPlatfromProject
//
//  Created by lzj<lizhijian_21@163.com> on 2019/4/26.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "JMPinAnnotationView.h"
#import "JMMarkerAnnotation.h"


@interface JMPinAnnotationView()

@property (nonatomic,strong) UIImageView *imageView;

@end

@implementation JMPinAnnotationView

- (id)initWithAnnotation:(id<BMKAnnotation>)annotation reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithAnnotation:annotation reuseIdentifier:reuseIdentifier];
    if ([annotation isKindOfClass:[JMMarkerAnnotation class]]) {
        JMMarkerAnnotation *annotation1 = (JMMarkerAnnotation *)annotation;
        if (annotation1.icon != nil) {
            self.image = [annotation1 getImage];
        }
    }

    return self;
}

- (UIImageView *)imageView
{
    if (_imageView == nil) {
        _imageView = [[UIImageView alloc] initWithFrame:CGRectMake(-10, -10, 30, 30)];
        _imageView.contentMode = UIViewContentModeScaleAspectFit;
        [self addSubview:_imageView];
    }

    return _imageView;
}

- (void)setImage:(UIImage *)image
{
    NSInteger width = image.size.width;
    NSInteger height = image.size.height;
    CGRect frame = CGRectMake(-width/2.0, -height/2.0, width, height);
    self.imageView.frame = frame;
    self.imageView.image = image;
}

- (UIImage *)image
{
    return self.imageView.image;
}

@end
