import Foundation
import Capacitor
import LiveUpdateProvider

@objc(MockLiveUpdateProviderPlugin)
public class MockLiveUpdateProviderPlugin: CAPPlugin {    
    public override func load() {
        Task {
            await LiveUpdateProviderRegistry.shared.register(MockLiveUpdateProvider(id: "mock"))
            NSLog("mock live update provider registered")
        }
    }

    @objc func ping(_ call: CAPPluginCall) {
        call.resolve(["ok": true])
    }
}
