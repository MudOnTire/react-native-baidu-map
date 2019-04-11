//
//  TrackInfoView.h
//  JiMiPlatfromProject
//
//  Created by lzj<lizhijian_21@163.com> on 2019/4/10.
//  Copyright © 2019 jimi. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface TrackInfoView : UIView

@property (weak, nonatomic) IBOutlet UILabel *gpsTimeLabel;
@property (weak, nonatomic) IBOutlet UILabel *hbTimeLabel;
@property (weak, nonatomic) IBOutlet UILabel *addressHintLabel;
@property (weak, nonatomic) IBOutlet UILabel *gpsSpeedLabel;
@property (weak, nonatomic) IBOutlet UILabel *deviceNameLabel;
@property (weak, nonatomic) IBOutlet UIImageView *speedImageView;
@property (weak, nonatomic) IBOutlet UILabel *gpsCountLabel;
@property (weak, nonatomic) IBOutlet UIImageView *signalImageView;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;

- (void)setModelDic:(NSDictionary *)modelDic;

@end

NS_ASSUME_NONNULL_END
