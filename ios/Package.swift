// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "MockLiveUpdateProvider",
    platforms: [.iOS(.v15)],
    products: [
        .library(
            name: "MockLiveUpdateProvider",
            targets: ["MockLiveUpdateProvider"]
        )
    ],
    dependencies: [
        .package(path: "../../live-updates-provider-sdk")
    ],
    targets: [
        .target(
            name: "MockLiveUpdateProvider",
            dependencies: [
                .product(name: "LiveUpdateProvider", package: "live-updates-provider-sdk")
            ],
            path: "Sources/MockLiveUpdateProvider"
        )
    ]
)
