Pod::Spec.new do |s|
  s.name             = 'LiveUpdateProviderMock'
  s.version          = '0.1.0-alpha.1'
  s.summary          = 'Mock implementation of LiveUpdateProvider for iOS integration testing.'
  s.homepage         = 'https://github.com/ionic-team/live-update-provider-mock'
  s.author           = { 'Ionic Team' => 'hi@ionicframework.com' }
  s.source           = { :git => 'https://github.com/ionic-team/live-update-provider-mock.git', :tag => s.version.to_s }

  s.ios.deployment_target = '15.0'
  s.swift_version    = '5.9'

  s.source_files = [
  'ios/Sources/LiveUpdateProviderMock/**/*.swift',
  'ios/Sources/LiveUpdateProviderMockPlugin/**/*.{swift,m}'
]

  s.resource_bundles = {
    'LiveUpdateProviderMockResourcesPortals' => ['webapp/build-portals/**/*'],
    'LiveUpdateProviderMockResourcesFedCap' => ['webapp/build-fedcap/**/*']
  }

  s.dependency 'Capacitor'
  s.dependency 'LiveUpdateProvider'
end
