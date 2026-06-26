# Live Update Provider Mock

Mock implementation of a live update provider for Portals and Federated Capacitor.

This repository is primarily a reference for teams building their own live update provider. It demonstrates the provider contract shape, native package split, and Capacitor adapter needed to support both native Portals apps and Federated Capacitor apps.

For the provider interfaces, see the [Live Update Provider SDK](https://github.com/ionic-team/live-update-provider-sdk).

## Architecture

The mock is split into two layers:

| Layer | Used by | Responsibility |
| --- | --- | --- |
| Native provider core | Portals apps | Implements the provider, manager, config parsing, and asset resolution. |
| Capacitor plugin adapter | Federated Capacitor apps | Registers the native provider with the live update provider registry when the plugin loads. |

Portals apps link the native provider core directly with no Capacitor dependency. Federated Capacitor apps consume the plugin adapter, which handles provider registration at plugin load time.

This separation is intentional. A production provider should keep its core update logic independent from Capacitor unless it only supports Capacitor plugin usage.

## Usage

The mock provider registers with:

```text
providerId = "mock"
```

Use that provider id from the live update configuration and pass the mock-specific `bundleType` under `providerConfig`.

```ts
liveUpdateConfig: {
  providerId: "mock",
  autoUpdateMethod: "none",
  providerConfig: {
    bundleType: "federatedCapacitor"
  }
}
```

## Bundle Types

The mock ships static web assets for two integration targets:

| Bundle type | Config value |
| --- | --- |
| Portals | `portals` |
| Federated Capacitor | `federatedCapacitor` |

The static assets make the provider deterministic for integration testing. They are not intended to model a production update service.

## Development

Build the TypeScript package:

```bash
npm run build
```

Build the Android packages:

```bash
npm run build:android
```

The iOS package is built through Swift Package Manager or CocoaPods from the native package definitions.

## Provider Implementation Notes

Use this repository as a reference for package shape and provider boundaries, not as a production update strategy.

A production provider will usually replace the bundled asset resolver with service-backed logic that checks for updates, downloads web assets, validates the result, stores state needed for rollback, and updates `latestAppDirectory` only after a local app directory is valid.

Portals integrations consume the native provider core directly. Federated Capacitor integrations consume the Capacitor plugin adapter, which registers the provider for lookup by provider id.
