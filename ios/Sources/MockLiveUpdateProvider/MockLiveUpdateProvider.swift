import Foundation
import LiveUpdateProvider

public struct MockLiveUpdateProvider: LiveUpdateProviding {
    public let id: String
    
    public func createManager(config: [String: any Sendable]?) throws -> any LiveUpdateManaging {
        guard let config else {
            throw MockLiveUpdateError.invalidConfiguration("Missing provider config")
        }
        guard let managerKey = config["managerKey"] as? String else {
            throw MockLiveUpdateError.invalidConfiguration("Missing required config key: managerKey")
        }
        guard let syncTo = config["syncTo"] as? String else {
            throw MockLiveUpdateError.invalidConfiguration("Missing required config key: syncTo")
        }
        guard let persistSync = config["persistSync"] as? Bool else {
            throw MockLiveUpdateError.invalidConfiguration("Missing required config key: persistSync")
        }
        
        return MockLiveUpdateManager(
            appType: "fedcap",
            managerKey: managerKey,
            syncTo: syncTo,
            persistSync: persistSync
        )
    }
}

public final class MockLiveUpdateManager: LiveUpdateManaging {
    public nonisolated(unsafe) var latestAppDirectory: URL?
    
    private let appType: String
    private let managerKey: String
    private let syncTo: String
    private let persistSync: Bool
    
    public init(appType: String, managerKey: String, syncTo: String, persistSync: Bool) {
        self.appType = appType
        self.managerKey = managerKey
        self.syncTo = syncTo
        self.persistSync = persistSync
        
        if persistSync {
            if let persistedSyncTo = UserDefaults.standard.string(forKey: managerKey) {
                latestAppDirectory = resolveDirectory(persistedSyncTo)
                if latestAppDirectory == nil {
                    latestAppDirectory = resolveDirectory(syncTo)
                }
            }
        }
    }
    
    public func sync() async throws -> any SyncResult {
        latestAppDirectory = resolveDirectory(syncTo)
        if persistSync {
            UserDefaults.standard.set(syncTo, forKey: managerKey)
        }
        
        return MockFederatedCapacitorSyncResult(didUpdate: true, metadata: [:])
    }
    
    private func resourceBundleName(appType: String) -> String {
        if appType == "portals" {
            return "MockLiveUpdateProviderResourcesPortals"
        } else {
            return "MockLiveUpdateProviderResourcesFedCap"
        }
    }
    
    private func resolveDirectory(_ syncTo: String) -> URL? {
#if SWIFT_PACKAGE
        return nil
#else
        let myBundle = Bundle(for: Self.self)
        let bundleName = resourceBundleName(appType: appType)
        guard let resourceBundleURL = myBundle.url(forResource: bundleName, withExtension: "bundle"),
              let resourceBundle = Bundle(url: resourceBundleURL),
              let resourceRoot = resourceBundle.resourceURL else {
            return nil
        }

        let directoryPath = resourceRoot.appendingPathComponent(syncTo, isDirectory: true)
        if FileManager.default.fileExists(atPath: directoryPath.path) {
            return directoryPath
        }

        // CRA/CRACO outputs are often at bundle root. If so, use bundle root as app root.
        let rootIndex = resourceRoot.appendingPathComponent("index.html")
        if FileManager.default.fileExists(atPath: rootIndex.path) {
            return resourceRoot
        }

        return nil
#endif
    }
}

public struct MockFederatedCapacitorSyncResult: FederatedCapacitorSyncResult, @unchecked Sendable {
    public let didUpdate: Bool
    public let metadata: [String: any Sendable]?
    
    public init(didUpdate: Bool, metadata: [String: Any]) {
        self.didUpdate = didUpdate
        self.metadata = metadata
    }
}

enum MockLiveUpdateError: LocalizedError {
    case invalidConfiguration(String)
}
