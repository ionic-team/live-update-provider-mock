// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "LiveUpdateProviderMock",
    platforms: [.iOS(.v15)],
    products: [
        .library(name: "LiveUpdateProviderMock", targets: ["LiveUpdateProviderMock"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/live-update-provider-sdk.git", from: "1.0.0"),
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "8.0.0")
    ],
    targets: [
        .target(
            name: "LiveUpdateProviderMock",
            dependencies: [
                .product(name: "LiveUpdateProvider", package: "live-update-provider-sdk"),
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/LiveUpdateProviderMock",
            resources: [
                .copy("Resources/app")
            ]
        )
    ]
)
