Pod::Spec.new do |s|
  s.name             = 'IonicEnterpriseMockLiveUpdateProvider'
  s.version          = '0.1.0'
  s.summary          = 'Mock implementation of LiveUpdateProvider for iOS integration testing.'
  s.homepage         = 'homepage'
  s.author           = { 'Ionic Team' => 'hi@ionicframework.com' }
  s.source           = { :git => 'https://github.com/ionic-team/mock-live-update-provider.git', :tag => s.version.to_s }

  s.ios.deployment_target = '15.0'
  s.swift_version    = '5.0'

  s.source_files = [
  'ios/Sources/MockLiveUpdateProvider/**/*.swift',
  'ios/Sources/MockLiveUpdateProviderPlugin/**/*.swift',
  'ios/Sources/MockLiveUpdateProviderPlugin/**/*.m'
]

  s.resource_bundles = {
    'MockLiveUpdateProviderResourcesPortals' => ['webapp/build-portals/**/*'],
    'MockLiveUpdateProviderResourcesFedCap' => ['webapp/build-fedcap/**/*']
  }

  s.dependency 'Capacitor'
  s.dependency 'LiveUpdateProvider'
end
