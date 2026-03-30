# LiveUpdateProviderMock

Mock provider for validating Federated Capacitor + live-update-provider-sdk integration.

## What this package includes

- iOS provider implementation (`MockLiveUpdateProvider` in Swift)
- Android provider implementation (`MockLiveUpdateProvider` in Kotlin)
- Minimal Capacitor plugin for both platforms that self-registers provider on load
- 3 embedded dummy web bundles in the provider package:
  - `mock-assets/aurora`
  - `mock-assets/neon`
  - `mock-assets/paper`

This allows FedCap consumers to stay web-config-only after installing this package.

## Install

### iOS

```bash
npm i @ionic-enterprise/mock-live-update-provider
npx cap sync ios
```

### Android

```bash
npm i @ionic-enterprise/mock-live-update-provider
npx cap sync android
```

## Use in Federated Capacitor config

```ts
liveUpdateConfig: {
  providerId: 'mock',
  autoUpdateMethod: 'none',
  providerConfig: {
    didUpdate: true,
    syncTo: 'mock-assets/aurora',
    persistKey: 'mock.checkout',
    metadata: { demo: 'aurora' }
  }
}
```

## Rotate through all 3 dummy bundles

```ts
providerConfig: {
  didUpdate: true,
  syncTargets: ['mock-assets/aurora', 'mock-assets/neon', 'mock-assets/paper'],
  persistKey: 'mock.rotating.demo'
}
```

Each sync advances to the next target and persists selection/index via platform storage (iOS: `UserDefaults`, Android: `SharedPreferences`).

## `providerConfig` options

- `didUpdate: boolean` (default `false`)
- `latestAppDirectory: string` absolute path or packaged resource path
- `syncTo: string` force a specific target path on sync
- `syncTargets: string[]` rotate across paths on each sync
- `persistSelection: boolean` (default `true`)
- `persistKey: string` key used for persisted path/index
- `metadata: Record<string, unknown>`
- `failWithMessage: string` to force sync failure
- `syncDelayMs: number` to simulate latency

## Platform-Specific Notes

### iOS
- Web assets are bundled as CocoaPod resource bundles
- Assets are accessed directly from the app bundle
- Persistence uses `UserDefaults`

### Android
- Web assets are bundled in the AAR assets directory
- Assets are copied to the app's cache directory on first access
- Cache is invalidated after 24 hours to prevent stale data
- Persistence uses `SharedPreferences`
- Assets must be pre-built and bundled before distribution
