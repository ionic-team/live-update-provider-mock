# Live Update Provider Mock

A reference [Live Update Provider](https://github.com/ionic-team/live-update-provider-sdk) for [Ionic Portals](https://ionic.io/docs/portals/) and [Federated Capacitor](https://ionic.io/docs/portals/for-capacitor/overview), backed by bundled web assets instead of a live service. It exists to show how to package and wire a provider, and to give the SDK and host apps a deterministic provider to test against.

## Overview

Like any provider, the mock centers on `ProviderManager`: `sync()` prepares a web app and exposes it through `latestAppDirectory` for the host runtime to load. Where a production provider fetches, verifies, and activates assets from a backend, the mock resolves a deterministic bundled fixture.

It supports both integration paths defined by the SDK:

- **Ionic Portals** constructs `MockProviderManager` directly — no Capacitor involved.
- **Federated Capacitor** resolves the provider by its Capacitor plugin name (`LiveUpdateProviderMock`). The plugin class conforms to `LiveUpdateProvider` directly — there is no separate provider object or registration step.

For the contract these types belong to and the full data flow, see the [SDK README](https://github.com/ionic-team/live-update-provider-sdk#readme).

## Requirements

| Target | Minimum |
| --- | --- |
| Capacitor | 8.x |
| iOS | 15.0 |
| Android | API 24 |

The TypeScript package builds to ES2022. The native toolchain versions track the [Capacitor 8 requirements](https://capacitorjs.com/docs/updating/8-0).

## Configuration

A host app selects the mock by its Capacitor plugin name. Which host you are — Portals or Federated Capacitor — is decided by which type you use (`MockProviderManager` directly, or the `LiveUpdateProviderMock` plugin), not by configuration, so `providerConfig` is reserved for test scenarios and is entirely optional:

```ts
liveUpdateConfig: {
  providerId: "LiveUpdateProviderMock",
  autoUpdateMethod: "none",
  providerConfig: {
    // all optional
    simulateFailure: true,            // force sync to fail, to exercise error handling
    metadata: { version: "1.2.3" }    // returned in the sync result
  }
}
```

| Key | Type | Default | Effect |
| --- | --- | --- | --- |
| `simulateFailure` | boolean | `false` | `sync` fails so callers can exercise error handling. |
| `metadata` | object | `{}` | Returned in the `MetadataSyncResult`. |

On success the mock resolves a single bundled fixture (`index.html` + `remoteEntry.js`) and points `latestAppDirectory` at it. The fixture is a deterministic stub for integration testing, not a production web app.

## Implementation

The mock is a single native module on each platform: `MockProviderManager` implements the provider contract, and `LiveUpdateProviderMockPlugin` is the Capacitor plugin whose class conforms to `LiveUpdateProvider` directly. Portals uses the manager without touching the plugin; Federated Capacitor resolves the plugin by name and calls `createManager` on it. The snippets below are condensed; see the source for the full asset resolver.

### The manager

`MockProviderManager` implements the `ProviderManager` contract directly on both platforms. It resolves the bundled app directory, points `latestAppDirectory` at it, and returns the configured metadata. A Portals app constructs this manager directly; nothing else is required.

**iOS** — [`MockProviderManager.swift`](ios/Sources/LiveUpdateProviderMock/MockProviderManager.swift)

```swift
public final class MockProviderManager: ProviderManager {
    public var latestAppDirectory: URL?

    public func sync() async throws -> (any ProviderSyncResult)? {
        if config.simulateFailure {
            throw MockSyncError(message: "Simulated sync failure for LiveUpdateProviderMock")
        }
        guard let resourceRoot = Bundle.module.url(forResource: "app", withExtension: nil) else {
            throw MockSyncError(message: "Can't find resource directory")
        }
        latestAppDirectory = resourceRoot
        return MetadataSyncResult(metadata: config.metadata)
    }
}
```

**Android** — [`MockProviderManager.kt`](android/live-update-provider-mock/src/main/kotlin/io/ionic/liveupdateprovidermock/MockProviderManager.kt)

```kotlin
class MockProviderManager(...) : ProviderManager {
    override var latestAppDirectory: File? = null

    override suspend fun sync(): ProviderSyncResult? = withContext(Dispatchers.IO) {
        if (config.simulateFailure) {
            throw MockSyncException("Simulated sync failure for LiveUpdateProviderMock")
        }
        val appDirectory = assetBundleResolver.resolve()
            ?: throw MockSyncException("bundled assets were not resolved")
        latestAppDirectory = appDirectory
        MetadataSyncResult(metadata = config.metadata)
    }
}
```

The manager implements `ProviderManager.sync` as a plain suspend function and moves its blocking asset-resolution work onto `Dispatchers.IO` itself, since `sync` may be called from any dispatcher (including the caller's own).

### The Federated Capacitor plugin

For Federated Capacitor, the Capacitor plugin class conforms to `LiveUpdateProvider` directly and implements `createManager`. There is no separate provider object and no registration call — the plugin is resolved by its Capacitor plugin name.

**iOS** — [`LiveUpdateProviderMockPlugin.swift`](ios/Sources/LiveUpdateProviderMock/LiveUpdateProviderMockPlugin.swift)

```swift
@objc(LiveUpdateProviderMockPlugin)
class LiveUpdateProviderMockPlugin: CAPPlugin, LiveUpdateProvider {
    public func createManager(configuration: [String: Any]) throws -> any ProviderManager {
        let config = try MockProviderConfig(configuration)
        return MockProviderManager(config: config)
    }
}
```

**Android** — [`LiveUpdateProviderMockPlugin.kt`](android/live-update-provider-mock/src/main/kotlin/io/ionic/liveupdateprovidermock/LiveUpdateProviderMockPlugin.kt)

```kotlin
@CapacitorPlugin(name = "LiveUpdateProviderMock")
class LiveUpdateProviderMockPlugin : Plugin(), LiveUpdateProvider {
    override fun createManager(context: Context, configuration: Map<String, Any>): ProviderManager {
        val providerConfig = MockProviderConfig.from(configuration)
        return MockProviderManager(context = context, config = providerConfig)
    }
}
```

## Project Structure

Each platform builds the provider manager and the Capacitor plugin adapter as a single module — iOS as one SwiftPM target, Android as one Gradle module (`:live-update-provider-mock`):

| Platform | Module |
| --- | --- |
| iOS | `ios/Sources/LiveUpdateProviderMock` |
| Android | `android/live-update-provider-mock` |
| Web | `src/index.ts` |

Bundled web assets live under `ios/Sources/LiveUpdateProviderMock/Resources` and `android/live-update-provider-mock/src/main/assets`.

## Development

**Web (TypeScript)**

```bash
npm run build
```

**Android**

```bash
npm run build:android            # build the Android module
cd android && ./gradlew test     # unit tests
```

Instrumented tests, if added under `android/live-update-provider-mock/src/androidTest`, need a connected device or emulator (`./gradlew :live-update-provider-mock:connectedAndroidTest`). The Android build uses the Capacitor 8 toolchain (Gradle 8.14.3, Android Gradle Plugin 8.13.0, Kotlin 2.2.20, `compileSdk` 36); build with the JDK bundled in Android Studio.

**iOS**

```bash
swift build                      # build the SPM target
```

## From Mock to Production

This repository is a reference for package shape and provider boundaries, not a production update strategy. It satisfies the SDK's [provider responsibilities](https://github.com/ionic-team/live-update-provider-sdk#provider-responsibilities) against static fixtures.

A production provider keeps the same contract but replaces the bundled asset resolver with service-backed logic: check for updates, download web assets, verify the result, store the state needed for rollback, and point `latestAppDirectory` at a local app directory only once it is valid.

## Related Resources

- [Live Update Provider SDK](https://github.com/ionic-team/live-update-provider-sdk) — the contract this repository implements
- [Federated Capacitor live updates](https://ionic.io/docs/portals/for-capacitor/live-updates)
- [Creating a Capacitor plugin](https://capacitorjs.com/docs/plugins/creating-plugins)

## License

Released under the MIT License. See [LICENSE](LICENSE).
