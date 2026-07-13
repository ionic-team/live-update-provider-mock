import Capacitor
import LiveUpdateProvider

@objc(LiveUpdateProviderMockPlugin)
class LiveUpdateProviderMockPlugin: CAPPlugin, CAPBridgedPlugin, LiveUpdateProvider {
    public let identifier = "LiveUpdateProviderMockPlugin"
    public let jsName = "LiveUpdateProviderMock"
    public let pluginMethods: [CAPPluginMethod] = []

    public func createManager(configuration: [String: Any]) throws -> any ProviderManager {
        let config = try MockProviderConfig(configuration)
        return MockProviderManager(config: config)
    }
}
