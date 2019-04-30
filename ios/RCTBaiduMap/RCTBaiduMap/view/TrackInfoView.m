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
@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, assign) CGFloat adjustmentHeight;

@end

@implementation TrackInfoView

- (CGSize)sizeWithString:(NSString *)str font:(UIFont *)font maxSize:(CGSize)maxSize
{
    CGSize textSize;
    if (CGSizeEqualToSize(maxSize, CGSizeZero)) {
        NSDictionary *attributes = [NSDictionary dictionaryWithObject:font forKey:NSFontAttributeName];
        textSize = [str sizeWithAttributes:attributes];
    } else {
        NSStringDrawingOptions option = NSStringDrawingTruncatesLastVisibleLine | NSStringDrawingUsesLineFragmentOrigin;
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
    view.frame = CGRectMake(frame.origin.x, frame.origin.y + view.frame.size.height - height - self.adjustmentHeight, frame.size.width, height);
    self.adjustmentHeight = 0;
}

#pragma mark -

- (void)awakeFromNib
{
    [super awakeFromNib];

    self.backgroundColor = [UIColor clearColor];
    [self setWidthWithView:self width:[[UIScreen mainScreen] bounds].size.width/3.0*2];
    self.adjustmentHeight = 5.0;

    _imageView = [[UIImageView alloc] initWithFrame:self.bounds];
    self.imageView.image = [UIImage imageNamed:@"frame_map_mark_info_bg"];
    [self insertSubview:self.imageView atIndex:0];

    self.speedImageView.image = [UIImage imageNamed:@"frame_speed"];

    UIColor *color = ZJColorFromRGB(0x747474);
    self.gpsTimeLabel.textColor = color;
    self.hbTimeLabel.textColor = color;
    self.addressHintLabel.textColor = color;
    self.gpsSpeedLabel.textColor = color;
    self.deviceNameLabel.textColor = color;
    self.gpsCountLabel.textColor = color;
    self.addressLabel.textColor = color;
}

- (void)setModelDic:(NSDictionary *)modelDic
{
    if (!modelDic) return;

    self.tag = [[modelDic objectForKey:@"tag"] intValue] + 1;
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
    NSString *posType = [modelDic objectForKey:@"positionType"];
    NSNumber *isShow = [modelDic objectForKey:@"isShow"];  //是否显示
    if (isShow) {
        self.hidden = ![isShow boolValue];
    }
    NSLog(@"%@", modelDic);

    [self updateCarState:status time:idelTiem];
    [self updateSignalStatus:gpsSigna];
    self.gpsTimeLabel.text = [NSString stringWithFormat:@"定位时间: %@", positionTime];
    self.hbTimeLabel.text = [NSString stringWithFormat:@"通讯时间: %@", communicationTime];
    self.gpsSpeedLabel.text = [NSString stringWithFormat:@"%@km/h", speed ? speed : @"0"];
    if ([posType isEqualToString:@"GPS"]) {
        self.gpsCountLabel.text = [NSString stringWithFormat:@"GPS定位: %@", gpsNum];
    } else if ([posType isEqualToString:@"LBS"]) {
        self.gpsCountLabel.text = @"基站定位";
    } else if ([posType isEqualToString:@"WIFI"]) {
        self.gpsCountLabel.text = @"WIFI定位";
    }

    NSString *urlString = [NSString stringWithFormat:@"http://poi.jimicloud.com/poi?_method_=geocoderForBaiDu&latlng=%@,%@&token=3500307a93c6fc335efa71f60438b465&language=%@", lat, lng, @"zh"];
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
                    CGSize size = [weakSelf sizeWithString:weakSelf.addressLabel.text font:weakSelf.addressLabel.font maxSize:CGSizeMake(weakSelf.bounds.size.width-self.addressHintLabel.frame.origin.x-self.addressHintLabel.bounds.size.width-15, MAXFLOAT)];
                    CGFloat pHeight = weakSelf.addressLabel.frame.origin.y + size.height + 25;   //需要的高度+底部三角形高度
                    [weakSelf setHeightWithView:weakSelf height:pHeight];
                    weakSelf.imageView.frame = weakSelf.bounds;
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

    NSString *timeStr = @"1分钟";
    int seconds = ((int)[time doubleValue]) /1000;
    if (seconds > 60) {
        long min = seconds / 60;
        timeStr = [NSString stringWithFormat:@"%ld分钟", min];
        if (min > 60) {
            min = (seconds / 60) % 60;
            long hour = (seconds / 60) / 60;
            timeStr = [NSString stringWithFormat:@"%ld小时%ld分钟", hour, min];
            if (hour > 24) {
                hour = ((seconds / 60) / 60) % 24;
                long day = (((seconds / 60) / 60) / 24);
                timeStr = [NSString stringWithFormat:@"%ld天%ld小时", day, hour];
            }
        }
    }

    UIColor *color = ZJColorFromRGB(0x29b473);
    NSString *statusText = @"";
    if([status isEqualToString:@"0"]){
        statusText = [NSString stringWithFormat:@"离线 %@", timeStr];
        color = ZJColorFromRGB(0x747474);
    } else if([status isEqualToString:@"1"]){
        statusText = [NSString stringWithFormat:@"静止 %@", timeStr];
        color = ZJColorFromRGB(0xcc0000);
    } else if([status isEqualToString:@"2"]){
        statusText = @"行驶中";
    } else if([status isEqualToString:@"3"]){
        statusText = @"在线";
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
