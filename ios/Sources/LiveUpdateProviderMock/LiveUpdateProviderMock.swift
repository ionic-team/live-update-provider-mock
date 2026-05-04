import Foundation
import LiveUpdateProvider

public final class LiveUpdateProviderMock: LiveUpdateProviding {
    public let id: String
    
    public init(id: String) {
        self.id = id
    }
    
    public func createManager(config: [String: Any]) throws -> any LiveUpdateManaging {
        let cfg = try LiveUpdateConfig(config)
        return LiveUpdateManagerMock(config: cfg)
    }
}

public final class LiveUpdateManagerMock: LiveUpdateManaging {
    public var latestAppDirectory: URL?
    
    private let config: LiveUpdateConfig
    
    public init(config: LiveUpdateConfig) {
        self.config = config
    }
    
    public func sync() async throws -> any SyncResult {
#if SWIFT_PACKAGE
        syncSPM()
#else
        try syncCocoaPods()
#endif
        
        switch config.appType {
        case .federatedCapacitor:
            return DefaultFederatedCapacitorSyncResult(
                metadata: ["key1": "value", "key2": 0, "key3": true]
            )
        case .portals:
            return DefaultFederatedCapacitorSyncResult(metadata: [:])
        }
    }
    
    private func syncSPM() {
        
    }
    
    func syncCocoaPods() throws -> Bool {
        let hostBundle = Bundle(for: Self.self)
        
        guard
            let bundleURL = hostBundle.url(forResource: config.appType.resourceBundleName, withExtension: "bundle"),
            let resourceBundle = Bundle(url: bundleURL),
            let resourceRoot = resourceBundle.resourceURL
        else {
            throw LiveUpdateProviderError.syncFailed("Sync failed for LiveUpdateProviderMock", underlyingError: nil)
        }
        
        self.latestAppDirectory = resourceRoot
        return true
    }
}
