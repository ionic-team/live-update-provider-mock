# Live Update Provider Mock

Reference Live Update Provider for testing and demonstrating third-party Live Update Provider implementations across iOS and Android.

This package is used by the Live Update Provider SDK test workflow, and it is also intended to serve as a practical example for implementers building their own Live Update Provider implementation for Federated Capacitor and Portals applications.

For the Live Update Provider interfaces and SDK source, see the [Live Update Provider SDK](https://github.com/ionic-team/live-update-provider-sdk).

## Overview

Live Update Provider Mock implements the native interfaces expected by the Live Update Provider SDK. It registers a Live Update Provider with the id `mock`, resolves a bundled web application for the requested app type, and reports that directory through the SDK manager contract.

The implementation demonstrates the core responsibilities of a Live Update Provider:

- Registering a native Live Update Provider with the Live Update Provider registry.
- Parsing implementation-specific configuration.
- Creating a platform manager for each app configuration.
- Resolving or downloading app assets.
- Updating `latestAppDirectory` after sync.
- Returning a sync result through the SDK callback/result APIs.

This package is intentionally simple. It does not contact a remote update service. Instead, it ships static web assets with the package so SDK integrations can be tested deterministically.

## Supported App Types

The mock Live Update Provider supports two app types:

| App type | Config value | Bundled asset path |
| --- | --- | --- |
| Portals | `portals` | `webapp/build-portals` |
| Federated Capacitor | `federatedCapacitor` | `webapp/build-fedcap` |

## Installation

```bash
npm install live-update-provider-mock
npx cap sync
```

After Capacitor sync, the native plugin registers itself as a Live Update Provider with:

```text
providerId = "mock"
```

## Configuration

Use the mock Live Update Provider from your Live Update configuration by setting `providerId` to `mock` and passing implementation-specific config under `providerConfig`.

```ts
liveUpdateConfig: {
  providerId: "mock",
  autoUpdateMethod: "none",
  providerConfig: {
    appType: "federatedCapacitor",
    autoSync: true
  }
}
```

### Provider Config

| Key | Type | Required | Description |
| --- | --- | --- | --- |
| `appType` | `"portals"` or `"federatedCapacitor"` | Yes | Selects which bundled web application should be resolved. |
| `autoSync` | `boolean` | Yes | When `true`, the Live Update Provider resolves assets during manager creation. Explicit `sync()` is still supported. |

## Package Structure

```text
.
├── android/
│   ├── build.gradle
│   └── src/main/kotlin/io/ionic/liveupdateprovidermock/
├── ios/
│   └── Sources/
│       ├── LiveUpdateProviderMock/
│       └── LiveUpdateProviderMockPlugin/
├── src/
├── webapp/
│   ├── build-fedcap/
│   └── build-portals/
├── LiveUpdateProviderMock.podspec
├── Package.swift
└── package.json
```

### TypeScript

`src/index.ts` exports the Capacitor plugin registration and the implementation config type used by consumers.

### iOS

The iOS implementation is split into two Swift targets:

- `LiveUpdateProviderMock`: the Live Update Provider and manager implementation.
- `LiveUpdateProviderMockPlugin`: the Capacitor plugin that registers the Live Update Provider.

For CocoaPods consumers, static web assets are packaged as resource bundles:

- `LiveUpdateProviderMockResourcesPortals.bundle`
- `LiveUpdateProviderMockResourcesFedCap.bundle`

On sync, the manager resolves the selected resource bundle and assigns its root URL to `latestAppDirectory`.

Swift Package Manager support is scaffolded, but bundled resource support should be completed before relying on SPM for asset-backed sync behavior.

### Android

The Android implementation is a single Gradle library under `android/`.

Key classes:

- `MockLiveUpdatePlugin`: Capacitor plugin and Live Update Provider registration entry point.
- `LiveUpdateConfig`: implementation config parser and validation.
- `MockLiveUpdateManager`: SDK manager implementation.
- `AssetBundleResolver`: copies packaged APK assets to cache and returns a filesystem directory.

Android packages assets under:

```text
android/src/main/assets/portals
android/src/main/assets/federated-capacitor
```

At runtime, the manager copies the selected asset tree to:

```text
<cache>/mock_bundles/portals
<cache>/mock_bundles/federated-capacitor
```

This copy step is necessary because Android APK assets are not directly represented as normal filesystem directories.

## Building

Build the TypeScript package:

```bash
npm run build
```

Build the web assets:

```bash
npm run build:webapp
```

Build and verify Android:

```bash
npm run sync:android
```

`sync:android` builds the web assets, then runs the Android Gradle build. During Android `preBuild`, Gradle copies:

- `webapp/build-portals` to `android/src/main/assets/portals`
- `webapp/build-fedcap` to `android/src/main/assets/federated-capacitor`

If Gradle fails with a Java version error, use a supported Android JDK, such as Android Studio's bundled JBR.

## SDK Dependencies

This package targets:

- Capacitor `>= 8`
- Android Live Update Provider SDK `io.ionic:liveupdateprovider:0.1.0-alpha.2`
- iOS Live Update Provider SDK package `live-update-provider-sdk` from `0.1.0-alpha.2`

SDK source: [ionic-team/live-update-provider-sdk](https://github.com/ionic-team/live-update-provider-sdk)

Implementers should keep their app, Live Update Provider implementation, and SDK dependency aligned to the same SDK artifact/version. Mixing SDK coordinates can produce duplicate native classes on Android.

## Building a Live Update Provider Implementation

Use this repository as a reference for the shape of a Live Update Provider implementation, not as a production update strategy.

A production implementation will usually replace the mock asset resolver with logic that:

- Authenticates with the implementer's update service.
- Checks for an available update.
- Downloads or selects the correct web bundle.
- Verifies integrity before activation.
- Stores update metadata and rollback state.
- Updates `latestAppDirectory` only after the bundle is ready for use.
- Returns implementation-specific metadata in the sync result.

The important contract is that the manager exposes a local app directory through `latestAppDirectory` and reports sync success or failure through the SDK result APIs.

## Release Checklist

Before publishing this package:

1. Run `npm run build`.
2. Run `npm run build:webapp`.
3. Run the Android build with a supported JDK.
4. Verify package contents with `npm pack --dry-run`.
5. Confirm `package.json`, `LiveUpdateProviderMock.podspec`, and release tags use the intended version.

The npm package must include `webapp/build-portals` and `webapp/build-fedcap`; those directories are required by the native asset packaging paths.
