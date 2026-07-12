# DozeTap

<p align="center">
  <img src="assets/icon.png" width="128" height="128" alt="DozeTap App Icon" />
</p>

<p align="center">
  <strong>DozeTap</strong> — Effortlessly manage your Android screen timeout with a single tap.
</p>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License: Apache 2.0" /></a>
  <img src="https://img.shields.io/badge/platform-Android-3DDC84?logo=android" alt="Platform" />
  <img src="https://img.shields.io/badge/minSdk-26-blue" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Kotlin-Compose-7F52FF?logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Material-3-059669?logo=material-design" alt="Material 3" />
</p>

---

## Overview

Changing the system screen timeout on Android typically requires navigating through multiple settings screens. **DozeTap** provides direct toggling and preset selection from:
- **Quick Settings Tile**: Dynamic canvas-rendered icon displaying active timeout duration (`15s`, `30s`, `1m`, `2m`, `5m`, `10m`, `30m`, `∞`).
- **Glance Home Screen Widgets**: Responsive widgets supporting 1x1, 2x1, 4x1, and 4x4 layout sizes.
- **Application Dashboard**: Material 3 interface for configuring custom preset orders, dark mode, and quick timeout selection.

---

## Technical Mechanism

Modifying system screen timeout programmatically requires `android.permission.WRITE_SETTINGS`.

1. On launch or tile invocation, the application checks `Settings.System.canWrite(context)`.
2. If permission is missing, the app directs the user to `Settings.ACTION_MANAGE_WRITE_SETTINGS`.
3. When granted, timeout updates write directly to `Settings.System.SCREEN_OFF_TIMEOUT` via `Settings.System.putInt()`.
4. `DozeTapTileService` and `DozeTapGlanceWidget` observe `ContentObserver` system changes and execute `TileService.requestListeningState()` for state synchronization.

---

## Permission Requirements

| Permission | Type | Purpose |
|---|---|---|
| `android.permission.WRITE_SETTINGS` | System Setting | Modifies system screen off timeout (`SCREEN_OFF_TIMEOUT`). |
| `moe.shizuku.manager.permission.API` | Optional API | Allows one-tap permission granting via active Shizuku service. |

### Option 1: One-Tap Grant via Shizuku (No PC Required)

1. Install and start the **[Shizuku](https://shizuku.rikka.app/)** app.
2. Start the Shizuku service via **Wireless Debugging** in Android Developer Options (Android 11+).
3. Open **DozeTap** -> Tap **"Grant via Shizuku"** on the home dashboard.

### Option 2: Grant Permission via ADB Terminal

```bash
# Via AppOps (Recommended)
adb shell appops set com.dhruvanbhalara.dozetap WRITE_SETTINGS allow

# Or via PM Grant
adb shell pm grant com.dhruvanbhalara.dozetap android.permission.WRITE_SETTINGS
```

---

## Build Instructions

### Prerequisites
- JDK 17+
- Android SDK (API Level 35 compileSdk, API Level 26 minSdk)

### Build Debug APK

```bash
git clone https://github.com/dhruvanbhalara/DozeTap.git
cd DozeTap
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Run Unit Tests

```bash
./gradlew test
```

---

## Architecture & Technology Stack

- **Architecture**: Clean Architecture (Domain, Data, UI, Service, Widget)
- **UI Framework**: Jetpack Compose + Material 3
- **Widgets**: Jetpack Glance (`GlanceAppWidget`)
- **System Integration**: `TileService` (Quick Settings API)
- **Persistence**: Jetpack DataStore Preferences

---

## Security & Privacy

- **Zero Network Access**: `AndroidManifest.xml` declares no internet permissions.
- **Single Setting Scope**: Reads and writes `Settings.System.SCREEN_OFF_TIMEOUT` only.

---

## License

Distributed under the [Apache License 2.0](LICENSE).

---

## Author

**Dhruvan Bhalara** ([@dhruvanbhalara](https://github.com/dhruvanbhalara))
