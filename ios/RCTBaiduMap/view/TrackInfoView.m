//
//  TrackInfoView.m
//  JiMiPlatfromProject
//
//  Created by lzj<lizhijian_21@163.com> on 2019/4/10.
//  Copyright © 2019 jimi. All rights reserved.
//

#import "TrackInfoView.h"

#define ZJColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface TrackInfoView()

@property (nonatomic, strong) NSURLSessionDataTask *sessionTask;

@end

@implementation TrackInfoView

- (CGSize)sizeWithString:(NSString *)str font:(UIFont *)font maxSize:(CGSize)maxSize
{
    CGSize textSize;
    if (CGSizeEqualToSize(maxSize, CGSizeZero)) {
        NSDictionary *attributes = [NSDictionary dictionaryWithObject:font forKey:NSFontAttributeName];
        textSize = [str sizeWithAttributes:attributes];
    } else {
        NSStringDrawingOptions option = NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin | NSStringDrawingUsesFontLeading;
        NSDictionary *attributes = [NSDictionary dictionaryWithObject:font forKey:NSFontAttributeName];
        CGRect rect = [str boundingRectWithSize:maxSize
                                         options:option
                                      attributes:attributes
                                         context:nil];

        textSize = rect.size;
    }
    return textSize;
}

- (void)setWidthWithView:(UIView *)view width:(CGFloat)width
{
    CGRect frame = view.frame;
    frame.size.width = width;
    view.frame = frame;
}

- (void)setHeightWithView:(UIView *)view height:(CGFloat)height
{
    CGRect frame = view.frame;
    frame.size.height = height;
    view.frame = frame;
}

#pragma mark -

- (void)awakeFromNib
{
    [super awakeFromNib];

    self.backgroundColor = [UIColor clearColor];
    [self setWidthWithView:self width:[[UIScreen mainScreen] bounds].size.width/3.0*2];

    UIImageView *imageView = [[UIImageView alloc] initWithFrame:self.bounds];
    imageView.image = [UIImage imageNamed:@"frame_map_mark_info_bg"];
    [self insertSubview:imageView atIndex:0];

    self.speedImageView.image = [UIImage imageNamed:@"frame_speed"];
    
    UIColor *color = ZJColorFromRGB(0x747474);
    self.gpsTimeLabel.textColor = color;
    self.hbTimeLabel.textColor = color;
    self.addressHintLabel.textColor = color;
    self.gpsSpeedLabel.textColor = color;
    self.deviceNameLabel.textColor = color;
    self.gpsCountLabel.textColor = color;
    self.addressLabel.textColor = color;

    NSString *str = NSLocalizedString(@"Address",nil);
    self.addressHintLabel.text = [NSString stringWithFormat:@"%@: ", str];
}

- (void)setModelDic:(NSDictionary *)modelDic
{
    if (!modelDic) return;

    NSString *status = [modelDic objectForKey:@"status"];
    NSString *positionTime = [modelDic objectForKey:@"positionTime"];
    NSString *communicationTime = [modelDic objectForKey:@"communicationTime"];
    NSString *idelTiem = [modelDic objectForKey:@"idelTiem"];//离线或者静止时长
    NSString *speed = [modelDic objectForKey:@"speed"];
//    NSString *positionType = [modelDic objectForKey:@"positionType"];   //定位类型
    NSString *gpsSigna = [modelDic objectForKey:@"gpsSigna"];
    NSString *gpsNum = [modelDic objectForKey:@"gpsNum"];
    NSString *lat = [modelDic objectForKey:@"latitude"];
    NSString *lng = [modelDic objectForKey:@"longitude"];

    [self updateCarState:status time:idelTiem];
    [self updateSignalStatus:gpsSigna];
    self.gpsTimeLabel.text = [NSString stringWithFormat:@"%@: %@", NSLocalizedString(@"PositionTime",nil), positionTime];
    self.hbTimeLabel.text = [NSString stringWithFormat:@"%@: %@", NSLocalizedString(@"HBTime",nil), communicationTime];
    self.gpsSpeedLabel.text = [NSString stringWithFormat:@"%@km/h", speed];
    self.gpsCountLabel.text = [NSString stringWithFormat:@"%@: %@", NSLocalizedString(@"GPS Count",nil), gpsNum];

    NSString *urlString = [NSString stringWithFormat:@"http://poi.jimicloud.com/poi?_method_=geocoderForBaiDu&latlng=%@,%@&token=3500307a93c6fc335efa71f60438b465&language=%@", lat, lng,@"zh"];
    NSURL *requestURL = [NSURL URLWithString:urlString];
    NSURLRequest *request = [NSURLRequest requestWithURL:requestURL cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:10.0];
    if (_sessionTask && self.sessionTask.state == NSURLSessionTaskStateRunning) {
        [self.sessionTask cancel];
    }

    __weak typeof(self) weakSelf = self;
    _sessionTask = [[NSURLSession sharedSession] dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (!error) {
            NSError *jsonError = nil;
            NSDictionary *dataDic = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:&jsonError];
            if (dataDic && [[dataDic objectForKey:@"code"] intValue] == 0) {
                NSString *address = [dataDic objectForKey:@"msg"];
                dispatch_async(dispatch_get_main_queue(), ^{
                    weakSelf.addressLabel.text = [NSString stringWithFormat:@"%@", address];
                    CGSize size = [self sizeWithString:weakSelf.addressLabel.text font:weakSelf.addressLabel.font maxSize:CGSizeMake(weakSelf.addressLabel.bounds.size.width, MAXFLOAT)];
                    CGFloat pHeight = weakSelf.frame.size.height - weakSelf.addressLabel.frame.origin.x;
                    CGFloat height = size.height - pHeight;
                    [weakSelf setHeightWithView:weakSelf height:weakSelf.bounds.size.height - height];
                });
            }
        }
    }];
    [self.sessionTask resume];
}

- (void)updateCarState:(NSString *)status time:(NSString *)time
{
    if (!time){
        time = @"0";
    }

    NSString *timeStr = [NSString stringWithFormat:@"1%@", NSLocalizedString(@"Min",nil)];
    int seconds = ((int)[time doubleValue]) /1000;
    if (seconds > 60) {
        long min = seconds / 60;
        timeStr = [NSString stringWithFormat:@"%ld%@", min, NSLocalizedString(@"Min",nil)];
        if (min > 60) {
            min = (seconds / 60) % 60;
            long hour = (seconds / 60) / 60;
            timeStr = [NSString stringWithFormat:@"%ld%@%ld%@", hour, NSLocalizedString(@"Hour",nil), min, NSLocalizedString(@"Min",nil)];
            if (hour > 24) {
                hour = ((seconds / 60) / 60) % 24;
                long day = (((seconds / 60) / 60) / 24);
                timeStr = [NSString stringWithFormat:@"%ld%@%ld%@", day, NSLocalizedString(@"Day",nil), hour, NSLocalizedString(@"Hour",nil)];
            }
        }
    }

    UIColor *color = ZJColorFromRGB(0x29b473);
    NSString *statusText = @"";
    if([status isEqualToString:@"0"]){
        statusText = [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"Offline",nil), timeStr];
        color = ZJColorFromRGB(0x747474);
    } else if([status isEqualToString:@"1"]){
        statusText = [NSString stringWithFormat:@"%@ %@", NSLocalizedString(@"Still",nil), timeStr];
        color = ZJColorFromRGB(0xcc0000);
    } else if([status isEqualToString:@"2"]){
        statusText = NSLocalizedString(@"Driving",nil);
    } else if([status isEqualToString:@"3"]){
        statusText = NSLocalizedString(@"Online",nil);
    }

    self.deviceNameLabel.textColor = color;
    self.deviceNameLabel.text = statusText;
}

- (void)updateSignalStatus:(NSString *)status
{
    int signal = [status intValue];
    switch (signal) {
        case 1:
            self.signalImageView.image = [UIImage imageNamed:@"frame_gps_signal_1"];
            break;
        case 2:
            self.signalImageView.image = [UIImage imageNamed:@"frame_gps_signal_2"];
            break;
        case 3:
            self.signalImageView.image = [UIImage imageNamed:@"frame_gps_signal_3"];
            break;
        case 4:
            self.signalImageView.image = [UIImage imageNamed:@"frame_gps_signal_4"];
            break;
        default:
            self.signalImageView.image = [UIImage imageNamed:@"frame_gps_signal_0"];
            break;
    }
}

@end
