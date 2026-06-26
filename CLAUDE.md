## Project Context

This repo is a mock/reference implementation of a Live Update provider. It is intended to help internal integrations and external provider authors understand how to package a provider for both Portals and Federated Capacitor.

The package is not primarily designed as a public npm plugin. The npm metadata exists so local apps and Capacitor tooling can consume the mock plugin shape during development.

## Intentional Architecture

- Keep the native provider core independent from Capacitor. Portals should consume the native provider package directly.
- Keep the Capacitor plugin adapter thin. Federated Capacitor uses it to register the native provider with the Live Update provider registry.
- Do not add service-like behavior to the mock unless explicitly requested. The bundled web assets are deterministic fixtures, not a production update service.
- Android `:core` exposes the provider via `api` so `:plugin` can depend on `project(":core")` without a separate Maven artifact.
- iOS uses separate SwiftPM targets and CocoaPods subspecs for the native provider core and Capacitor plugin adapter.

## Repo Cheat Sheet

- TypeScript plugin entry: `src/index.ts`
- iOS provider core: `ios/Sources/LiveUpdateProviderMock`
- iOS plugin adapter: `ios/Sources/LiveUpdateProviderMockPlugin`
- iOS resources: `ios/Sources/LiveUpdateProviderMock/Resources`
- iOS tests: `ios/Tests/LiveUpdateProviderMockTests`
- Android provider core module: `android/core`
- Android plugin adapter module: `android/plugin`
- Package manifests: `package.json`, `Package.swift`, `LiveUpdateProviderMock.podspec`

Useful commands:

```bash
npm run build
npm run build:android
swift test
npm pack --dry-run --json
```

Use `npm pack --dry-run --json` only to validate local package shape. It is not a release-readiness signal for public npm publishing.

## Change Guidance

- Keep this repo minimal and reference-quality. Avoid adding features only to make the mock feel more complete.
- Preserve the Portals vs Federated Capacitor boundary when changing package structure or provider registration.
- Prefer meaningful tests over coverage for obvious behavior.
- Do not stage or commit changes unless explicitly asked.
