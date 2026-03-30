# LiveUpdateProviderMock

Mock provider for validating Federated Capacitor + live-updates-provider-sdk integration.

## What this package includes

- iOS provider implementation (`MockLiveUpdateProvider`)
- Minimal Capacitor iOS plugin (`MockLiveUpdateProviderPlugin`) that self-registers provider on load
- 3 embedded dummy web bundles in the provider package:
  - `mock-assets/aurora`
  - `mock-assets/neon`
  - `mock-assets/paper`

This allows FedCap consumers to stay web-config-only after installing this package.

## Install

```bash
npm i @ionic-enterprise/mock-live-update-provider
npx cap sync ios
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

Each sync advances to the next target and persists selection/index via `UserDefaults`.

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
