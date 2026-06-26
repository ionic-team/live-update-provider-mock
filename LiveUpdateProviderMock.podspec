Pod::Spec.new do |s|
  s.name             = 'LiveUpdateProviderMock'
  s.version          = '0.1.0'
  s.summary          = 'Mock implementation of LiveUpdateProvider for iOS integration testing.'
  s.homepage         = 'https://github.com/ionic-team/live-update-provider-mock'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'Ionic Team' => 'hi@ionicframework.com' }
  s.source           = { :git => 'https://github.com/ionic-team/live-update-provider-mock.git', :tag => s.version.to_s }

  s.ios.deployment_target = '15.0'
  s.swift_version    = '5.9'

  s.default_subspec = 'Plugin'

  s.subspec 'Core' do |core|
    core.source_files = 'ios/Sources/LiveUpdateProviderMock/**/*.swift'
    core.resource_bundles = {
      'LiveUpdateProviderMockResourcesPortal' => ['ios/Sources/LiveUpdateProviderMock/Resources/portal/**/*'],
      'LiveUpdateProviderMockResourcesFedCap' => ['ios/Sources/LiveUpdateProviderMock/Resources/federated-capacitor/**/*']
    }
    core.dependency 'LiveUpdateProvider'
  end

  s.subspec 'Plugin' do |plugin|
    plugin.source_files = 'ios/Sources/LiveUpdateProviderMockPlugin/**/*.{swift,m}'
    plugin.dependency 'LiveUpdateProviderMock/Core'
    plugin.dependency 'Capacitor', '~> 8.0'
  end
end
