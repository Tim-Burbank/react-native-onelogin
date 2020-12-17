
Pod::Spec.new do |s|
  s.name         = "RNReactNativeOneLogin"
  s.version      = "1.0.0"
  s.summary      = "RNReactNativeOneLogin"
  s.description  = <<-DESC
                  RNReactNativeOneLogin
                   DESC
  s.homepage     = "https://github.com/Tim-Burbank/react-native-onelogin"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNReactNativeOneLogin.git", :tag => "master" }
  s.source_files  = "ios/*.{h,m}"
  s.resource = "ios/Onelogin.bundle"
  s.requires_arc = true


  s.dependency "React"
  s.dependency "GTOneLoginSDK"
  s.dependency "MBProgressHUD"
  #s.dependency "others"

end

