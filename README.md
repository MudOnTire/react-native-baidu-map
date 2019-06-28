
# react-native-baidu-map-jm [![npm version](https://img.shields.io/npm/v/react-native-baidu-map-jm.svg?style=flat)](https://www.npmjs.com/package/react-native-baidu-map-jm)

- 此版本为lovebing的分支仓库，特此感谢作者的开源。因项目需求，iOS版百度最新的SDK作者未实现Overlay，个人在其基础上参考Android进行修改和完善。

Baidu Map SDK modules and view for React Native(Android & IOS), support react native 0.57+

百度地图 React Native 模块，支持 react native 0.57+，已更新到最新的百度地图SDK版本。

![IOS](https://raw.githubusercontent.com/Eafy/react-native-baidu-map-jm/master/images/ios.jpg)
![Android](https://raw.githubusercontent.com/Eafy/react-native-baidu-map-jm/master/images/android.jpg)


### Dev & Test 开发和测试说明
react-native doesn't support symlinks. see https://stackoverflow.com/questions/44061155/react-native-npm-link-local-dependency-unable-to-resolve-module. Can't install local package by using `npm link`.

react-native 不支持软链，参考：
https://stackoverflow.com/questions/44061155/react-native-npm-link-local-dependency-unable-to-resolve-module
所以不能使用 npm link 的方式安装本地的包


### Environments 环境要求
1.JS
- node: 8.0+

2.Android
- Android SDK: api 28+
- gradle: 4.5
- Android Studio: 3.1.3+

3.IOS
- XCode: 9.0+


### Install 安装
#### 使用本地的包 （以 example 为例）
```shell
mkdir example/node_modules/react-native-baidu-map-jm
cp -R package.json js index.js ios android LICENSE README.md example/node_modules/react-native-baidu-map-jm/
rm -rf example/node_modules/react-native-baidu-map-jm/ios/RCTBaiduMap.xcodeproj

```
#### 使用 npm 源
npm install react-native-baidu-map-jm --save

### 原生模块导入

#### Android Studio
`react-native link react-native-baidu-map-jm`

#### IOS/Xcode
使用 pod

Podfile 增加
```
  pod 'React', :path => '../node_modules/react-native', :subspecs => [
    'Core',
    'CxxBridge',
    'DevSupport', 
    'RCTText',
    'RCTNetwork',
    'RCTWebSocket', 
    'RCTAnimation'
  ]
  pod 'yoga', :path => '../node_modules/react-native/ReactCommon/yoga'
  pod 'DoubleConversion', :podspec => '../node_modules/react-native/third-party-podspecs/DoubleConversion.podspec'
  pod 'glog', :podspec => '../node_modules/react-native/third-party-podspecs/glog.podspec'
  pod 'Folly', :podspec => '../node_modules/react-native/third-party-podspecs/Folly.podspec'

  pod 'react-native-baidu-map-jm', :podspec => '../node_modules/react-native-baidu-map/ios/react-native-baidu-map-jm.podspec'
```

##### AppDelegate.m init 初始化
    #import "RCTBaiduMapViewManager.h"
    - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
    {
        ...
        [RCTBaiduMapViewManager initSDK:@"api key"];
        ...
    }

### Usage 使用方法

    import { MapView, MapTypes, Geolocation, Overlay, Location} from 'react-native-baidu-map'

#### MapView Props 属性
| Prop                    | Type  | Default  | Description
| ----------------------- |:-----:| :-------:| -------
| zoomControlsVisible     | bool  | true     | Android only
| trafficEnabled          | bool  | false    |
| baiduHeatMapEnabled     | bool  | false    |
| mapType                 | number| 1        |
| zoom                    | number| 10       |
| center                  | object| null     | {latitude: 0, longitude: 0}
| buildingsEnabled        | bool  | true     | 
| overlookEnabled         | bool  | true     | 
| trackPlayInfo           | object| null     | 播放轨迹信息数据(已无效)
| visualRange             | array | []       | 
| infoWindows             | object| undefined| 无效
| correctPerspective      | object| undefined| Android only
| onMapStatusChangeStart  | func  | undefined| Android only
| onMapStatusChange       | func  | undefined|
| onMapStatusChangeFinish | func  | undefined| Android only
| onMapLoaded             | func  | undefined|
| onMapClick              | func  | undefined|
| onMapDoubleClick        | func  | undefined|
| onMarkerClick           | func  | undefined|
| onMapPoiClick           | func  | undefined|
| onBubbleOfMarkerClick   | func  | undefined| Android only

#### Overlay 覆盖物
    const { Marker, Arc, Circle, Polyline, Polygon, Text, InfoWindow } = Overlay;

##### Marker Props 属性
| Prop                    | Type  | Default  | Description
| ----------------------- |:-----:| :-------:| -------
| tag                     | int   | -1       | 用于多个Marker时绑定MapView的infoWindows
| title                   | string| null     |
| location                | object| {latitude: 0, longitude: 0}    |
| alpha                   | float | 1        |
| rotate                  | float | 0        |
| flat                    | bool  | null     |
| icon                    | any   | null     | icon图片，同 <Image> 的 source 属性
| visible                 | bool  | true     |
| infoWindow              | shape | null     | 若用此InfoWindow则无需绑定tag

##### Arc Props 属性
| Prop                    | Type  | Default  | Description
| ----------------------- |:-----:| :-------:| -------
| color                   | string| FFFF0088 |
| width                   | int   | 1        |
| poins                   | array | [{latitude: 0, longitude: 0}, {latitude: 0, longitude: 0}, {latitude: 0, longitude: 0}] | 数值长度必须为 3

##### Circle Props 属性
| Prop                    | Type  | Default  | Description
| ----------------------- |:-----:| :-------:| -------
| radius                  | int   |          |
| fillColor               | string|          |
| stroke                  | object| {width: 2, color: 'AA0000FF'} |
| center                  | object| {latitude: 0, longitude: 0}       |

##### Polyline Props 属性
| Prop                    | Type  | Default  | Description
| ----------------------- |:-----:| :-------:| -------
| points                  | array | [{latitude: 0, longitude: 0},{latitude: 0, longitude: 0}]     |
| width                   | int   | 8        |
| visible                 | bool  | false    |

##### Polygon Props 属性
| Prop                    | Type  | Default  | Description
| ----------------------- |:-----:| :-------:| -------
| points                  | array | [{latitude: 0, longitude: 0}]     |
| fillColor               | string|          |
| stroke                  | object| {width: 2, color: 'AA00FF00'} |


##### Text Props 属性（iOS无此属性）
| Prop                    | Type  | Default  | Description
| ----------------------- |:-----:| :-------:| -------
| text                    | string|          |
| fontSize                | int   |          |
| fontColor               | string|          |
| bgColor                 | string|          |
| rotate                  | float |          |
| location                | object|{latitude: 0, longitude: 0}

##### InfoWindow Props 属性(Mode 0)
| Prop                    | Type  | Default  | Description
| ----------------------- |:-----:| :-------:| -------
| location                | object|{latitude: 0, longitude: 0}
| visible                 | bool  | false    | 
| title                   | string| ""       |
| width                   | int   |          |
| height                  | int   |          |

##### InfoWindow Props 属性(Mode 1, This Custom)
| Prop                    | Type  | Default  | Description
| ----------------------- |:-----:| :-------:| -------
| tag                     | int   | -1       | This Flag to link marker
| latitude                | string| "0"      
| longitude               | string| "0"      
| status                  | string| ""       | Car status
| positionTime            | string|  ""      |  定位时间
| communicationTime       | string| ""       | 通信时间
| idelTiem                | string| ""       | 状态持续时长
| speed                   | string| "0"      | 车速
| positionType            | string| ""       | 定位类型
| gpsSigna                | string|  ""      | 信号强度
| gpsNum                  | string| ""       | 定位卫星个数
| isShow                  | bool  | false    | 是否显示此Infowindow

<MapView>
    <Marker/>
    <Arc />
    <Circle />
    <Polyline />
    <Polygon />
    <Text />
    <InfoWindow>
        <View></View>
    </InfoWindow>
</MapView>

#### Geolocation Methods

| Method                    | Result
| ------------------------- | -------
| Promise reverseGeoCode(double lat, double lng) | `{"address": "", "province": "", "cityCode": "", "city": "", "district": "", "streetName": "", "streetNumber": ""}`
| Promise reverseGeoCodeGPS(double lat, double lng) |  `{"address": "", "province": "", "cityCode": "", "city": "", "district": "", "streetName": "", "streetNumber": ""}`
| Promise geocode(String city, String addr) | {"latitude": 0.0, "longitude": 0.0}
| Promise getCurrentPosition() | IOS: `{"latitude": 0.0, "longitude": 0.0, "address": "", "province": "", "cityCode": "", "city": "", "district": "", "streetName": "", "streetNumber": ""}` Android: `{"latitude": 0.0, "longitude": 0.0, "direction": -1, "altitude": 0.0, "radius": 0.0, "address": "", "countryCode": "", "country": "", "province": "", "cityCode": "", "city": "", "district": "", "street": "", "streetNumber": "", "buildingId": "", "buildingName": ""}`

#### Geolocation Methods

| Method                    | Listener | Result
| ------------------------- | -------  | ------
| Promise config(String key) | kLocationModuleCheckPermission |`{"errcode": "0", "errmsg": "Success"}`
| locationTimeout(int timeout) | null | null
| allowsBackground(bool allows) | null |null
| startUpdatingLocation() | kLocationModuleUpdateLocation、kLocationModuleFail、kLocationModuleChangeAuthorization、kLocationModuleUpdateNetworkState | `{"method": "onLocationModuleUpdateLocation", "latitude": 0.0, "longitude": "0.0"}` or `{"method": "onLocationModuleFail", "errcode": 0, "errmsg": "定位发生错误"}` or `{"method": "onLocationModuleChangeAuthorization", "state": 0}` or `{"method": "onLocationModuleUpdateNetworkState", "state": 0}`
| stopUpdatingLocation() | null | null
| startUpdatingHeading() | kLocationModuleUpdateHeading |  `{"magneticHeading": 0.0, "trueHeading": 0.0, "headingAccuracy": 0.0, "timestamp": 0.0}`
| stopUpdatingHeading() | null | null


