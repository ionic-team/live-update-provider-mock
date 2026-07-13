import LiveUpdateProvider

public struct MockProviderConfig {
    /// When true, `sync()` fails so callers can exercise error handling.
    let simulateFailure: Bool

    /// Metadata returned with a successful sync result.
    let metadata: [String: Any]

    public init(_ config: [String: Any]) throws {
        if let rawFailure = config["simulateFailure"] {
            guard let simulateFailure = rawFailure as? Bool else {
                throw ProviderError.invalidConfiguration(message: "simulateFailure must be a boolean")
            }
            self.simulateFailure = simulateFailure
        } else {
            self.simulateFailure = false
        }

        if let rawMetadata = config["metadata"] {
            guard let metadata = rawMetadata as? [String: Any] else {
                throw ProviderError.invalidConfiguration(message: "metadata must be an object")
            }
            self.metadata = metadata
        } else {
            self.metadata = [:]
        }
    }
}
