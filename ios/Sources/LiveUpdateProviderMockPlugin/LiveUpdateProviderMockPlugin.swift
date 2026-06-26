import Foundation
#if canImport(Capacitor)
import Capacitor
import LiveUpdateProvider
import LiveUpdateProviderMock

@objc(LiveUpdateProviderMockPlugin)
class LiveUpdateProviderMockPlugin: CAPPlugin {
    override func load() {
        // Registration failure is a programming error (duplicate ID or SDK mismatch), not a recoverable condition.
        try! LiveUpdateProviderRegistry.shared.register(LiveUpdateProviderMock(id: "mock"))
    }
}
#endif
