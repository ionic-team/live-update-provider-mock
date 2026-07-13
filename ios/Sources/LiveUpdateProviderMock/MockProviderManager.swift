import Foundation
import LiveUpdateProvider

private struct MockSyncError: LocalizedError {
    let message: String
}

public final class MockProviderManager: ProviderManager {
    public var latestAppDirectory: URL?

    private let config: MockProviderConfig

    public init(config: MockProviderConfig) {
        self.config = config
    }

    public func sync() async throws -> (any ProviderSyncResult)? {
        if config.simulateFailure {
            throw MockSyncError(message: "Simulated sync failure for LiveUpdateProviderMock")
        }

        try syncSPM()
        return MetadataSyncResult(metadata: config.metadata)
    }

    private func syncSPM() throws {
        guard let resourceRoot = Bundle.module.url(forResource: Self.resourceDirectoryName, withExtension: nil) else {
            throw MockSyncError(message: "Can't find resource directory")
        }

        self.latestAppDirectory = resourceRoot
    }

    /// Directory bundled via SwiftPM (`Resources/app`).
    private static let resourceDirectoryName = "app"
}
