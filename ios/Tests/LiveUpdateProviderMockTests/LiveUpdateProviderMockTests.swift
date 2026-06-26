import XCTest
import LiveUpdateProvider
@testable import LiveUpdateProviderMock

final class LiveUpdateProviderMockTests: XCTestCase {
    func testPortalsSyncUsesPackagedResources() async throws {
        let manager = try LiveUpdateProviderMock(id: "mock").createManager(config: [
            "bundleType": "portals"
        ])

        _ = try await manager.sync()

        let latestAppDirectory = try XCTUnwrap(manager.latestAppDirectory)
        XCTAssertTrue(FileManager.default.fileExists(atPath: latestAppDirectory.appendingPathComponent("index.html").path))
    }

    func testFederatedCapacitorSyncUsesPackagedResources() async throws {
        let manager = try LiveUpdateProviderMock(id: "mock").createManager(config: [
            "bundleType": "federatedCapacitor"
        ])

        _ = try await manager.sync()

        let latestAppDirectory = try XCTUnwrap(manager.latestAppDirectory)
        XCTAssertTrue(FileManager.default.fileExists(atPath: latestAppDirectory.appendingPathComponent("remoteEntry.js").path))
    }

    func testMissingBundleTypeThrows() throws {
        XCTAssertThrowsError(try LiveUpdateProviderMock(id: "mock").createManager(config: [:])) { error in
            guard case LiveUpdateProviderError.invalidConfiguration = error else {
                return XCTFail("Expected invalidConfiguration, got \(error)")
            }
        }
    }

    func testInvalidBundleTypeThrows() throws {
        XCTAssertThrowsError(try LiveUpdateProviderMock(id: "mock").createManager(config: ["bundleType": "invalid"])) { error in
            guard case LiveUpdateProviderError.invalidConfiguration = error else {
                return XCTFail("Expected invalidConfiguration, got \(error)")
            }
        }
    }
}
