import Foundation
import LiveUpdateProvider

public final class LiveUpdateProviderMock: LiveUpdateProvider {
    public let id: String
    
    public init(id: String) {
        self.id = id
    }
    
    public func createManager(config: [String: Any]) throws -> any LiveUpdateProviderManager {
        let cfg = try LiveUpdateProviderMockConfig(config)
        return LiveUpdateProviderMockManager(config: cfg)
    }
}

public final class LiveUpdateProviderMockManager: LiveUpdateProviderManager {
    public var latestAppDirectory: URL?
    
    private let config: LiveUpdateProviderMockConfig
    
    public init(config: LiveUpdateProviderMockConfig) {
        self.config = config
    }
    
    public func sync() async throws -> any LiveUpdateProviderSyncResult {
#if SWIFT_PACKAGE
        try syncSPM()
#else
        try syncCocoaPods()
#endif
        
        // Metadata is arbitrary fixture data. A production provider would return meaningful
        // values here, such as app version, checksum, or CDN URL.
        switch config.bundleType {
        case .federatedCapacitor:
            return DefaultMetadataSyncResult(
                metadata: ["key1": "value", "key2": 0, "key3": true]
            )
        case .portal:
            return DefaultMetadataSyncResult(metadata: [:])
        }
    }
    
    private func syncSPM() throws {
        guard let resourceRoot = Bundle.module.url(forResource: config.bundleType.spmResourceDirectoryName, withExtension: nil) else {
            throw LiveUpdateProviderError.syncFailed("Sync failed for LiveUpdateProviderMock", underlyingError: nil)
        }

        self.latestAppDirectory = resourceRoot
    }
    
    private func syncCocoaPods() throws {
        let hostBundle = Bundle(for: Self.self)

        guard
            let bundleURL = hostBundle.url(forResource: config.bundleType.resourceBundleName, withExtension: "bundle"),
            let resourceBundle = Bundle(url: bundleURL),
            let resourceRoot = resourceBundle.resourceURL
        else {
            throw LiveUpdateProviderError.syncFailed("Sync failed for LiveUpdateProviderMock", underlyingError: nil)
        }

        self.latestAppDirectory = resourceRoot
    }
}
