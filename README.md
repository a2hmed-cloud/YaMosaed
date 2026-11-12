# HAMADA Offline

Android-first offline messaging prototype. No internet is used for local device discovery and direct message transport. Long-distance delivery requires a chain of participating relay devices; global reach is not guaranteed.

## MVP
- HAMADA ID generated locally
- Liquid-glass-inspired dark UI
- Nearby device discovery (Bluetooth LE)
- Local message storage and delivery states
- Relay protocol planned after direct-device testing

## Build
GitHub Actions builds a debug APK on pushes to `main`.
