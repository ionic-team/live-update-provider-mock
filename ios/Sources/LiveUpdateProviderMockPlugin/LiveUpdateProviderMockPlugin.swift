import Foundation
import Capacitor
import LiveUpdateProvider
import LiveUpdateProviderMock

@objc(LiveUpdateProviderMockPlugin)
class LiveUpdateProviderMockPlugin: CAPPlugin {
    override func load() {
        try? LiveUpdateProviderRegistry.shared.register(LiveUpdateProviderMock(id: "mock"))
    }
}
