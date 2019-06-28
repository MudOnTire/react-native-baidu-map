Pod::Spec.new do |s|
  s.name         = "react-native-baidu-map-jm"
  s.version      = "1.1.7"
  s.summary      = "Baidu Map for React Native"

  s.description  = <<-DESC
  Baidu Map views and modules for React Native
                   DESC

  s.homepage     = "https://github.com/Eafy/react-native-baidu-map-jm"
  s.screenshots  = "https://raw.githubusercontent.com/lovebing/react-native-baidu-map/master/images/android.jpg", "https://raw.githubusercontent.com/lovebing/react-native-baidu-map/master/images/ios.jpg"

  s.license      = "MIT"
  s.author       = { "Eafy" => "lizhijian_21@163.com" }
  s.platform     = :ios, "9.0"


  s.source       = { :git => "https://github.com/Eafy/react-native-baidu-map-jm.git", :tag => "#{s.version}" }

  s.source_files  = "ios/RCTBaiduMap/**/*.{h,m}"
  s.exclude_files = ""
  s.frameworks = "CoreLocation", "QuartzCore", "OpenGLES", "SystemConfiguration", "CoreGraphics", "Security", "CoreTelephony" 
  s.libraries = "c++", "sqlite3", "ssl", "crypto"

  s.dependency 'React'
  s.dependency 'BaiduMapKit', '4.2.1'
  s.dependency 'BMKLocationKit', '1.3.0.2'
end