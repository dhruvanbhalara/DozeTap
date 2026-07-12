# AGENTS.md — Repository Guide for AI Agents & Developers

This document serves as the authoritative guide for AI coding agents (Antigravity, Claude, Codex, etc.) and human developers working on the **DozeTap** Android codebase.

---

## 1. Project Overview & Core Responsibilities

**DozeTap** is a privacy-focused, zero-network Android application for managing system screen off timeouts (`Settings.System.SCREEN_OFF_TIMEOUT`).

### System Integration Touchpoints
- **Quick Settings Tile**: `TileService` rendering dynamic canvas badge icons (`DozeTapTileService.kt`).
- **Home Screen Widgets**: Jetpack Glance Bento Widget suite (`DozeTapGlanceWidget.kt`).
- **App Dashboard**: Jetpack Compose Material 3 UI (`MainActivity.kt`, `HomeScreen.kt`, `SettingsScreen.kt`, `TilesScreen.kt`, `WidgetsScreen.kt`).

---

## 2. Architecture & Directory Structure

DozeTap follows **Clean Architecture** with Hilt Dependency Injection and a feature-first directory layout:

```
app/src/main/java/com/dhruvanbhalara/dozetap/
├── di/                      # Hilt DI modules (RepositoryModule.kt, AppModule.kt)
├── domain/                  # Pure Kotlin domain logic (No Android dependencies)
│   ├── model/              # Immutably annotated models (TimeoutOption.kt)
│   ├── repository/         # Repository interfaces (ITimeoutRepository.kt, IPreferencesRepository.kt)
│   └── usecase/            # Single-responsibility use cases
├── data/                    # Data sources & repository implementations
│   └── repository/         # TimeoutRepositoryImpl.kt, PreferencesRepositoryImpl.kt
├── ui/                      # Jetpack Compose UI
│   ├── components/         # Reusable composables (TimeoutChip.kt, SleepingPhoneAnimation.kt)
│   ├── screens/            # Screen composables & ViewModels (home, settings, onboarding, tiles, widgets)
│   └── theme/              # Material 3 color palettes & typography
├── service/                 # System services (DozeTapTileService.kt)
├── util/                    # Platform abstractions & helpers (PlatformSystemManager.kt)
└── widget/                  # Glance widgets (DozeTapGlanceWidget.kt)
```

---

## 3. Strict Coding Conventions & Architectural Rules

### Dependency Injection Rules (Hilt)
- **Hilt Annotation Mandate**: Every ViewModel **must** be annotated with `@HiltViewModel` and use `@Inject constructor(...)`.
- **Android Entry Points**: `DozeTapApp` **must** be annotated with `@HiltAndroidApp`. Activities (`MainActivity`) and Services (`DozeTapTileService`) **must** be annotated with `@AndroidEntryPoint`.
- **No Service Locator**: Static `Application` instance service locators (`DozeTapApp.instance`) and manual ViewModel factories are strictly forbidden.
- **Constructor Injection for UseCases**: UseCases **must** declare `@Inject constructor(...)` so Hilt automatically injects them into ViewModels.

### Startup Performance & Threading Rules
- **Zero Main Looper Thread Blocking**: Never invoke `runBlocking` or blocking thread synchronizations on the main Android UI looper during Activity/Application startup or lifecycle events.
- **Thread Offloading**: `Settings.System` operations (`putInt`/`getInt`) and DataStore preferences **must** be offloaded off the UI thread to `Dispatchers.IO`.

### Compose & State Rules
- **Compose Stability**: All UI state models (`HomeUiState`, `SettingsUiState`, `TimeoutOption`) **must** be annotated with `@Immutable`.
- **Lifecycle Safety**: Always use `collectAsStateWithLifecycle()` from `androidx.lifecycle.compose` instead of raw `collectAsState()`.
- **Navigation Compose**: Top-level routing **must** use Jetpack Navigation Compose (`NavHost`, `rememberNavController()`) instead of manual string state switching.
- **Buffered UI Side-Effects**: One-time UI side-effect streams **must** use `Channel<UiEffect>(Channel.BUFFERED).receiveAsFlow()` to guarantee delivery across configuration changes.
- **Platform API Decoupling**: Framework APIs (`StatusBarManager`, `AppWidgetManager`, System Intents) **must** be encapsulated behind interface abstractions (`PlatformSystemManager`) rather than called directly in composable blocks.

### Documentation Rules
- **KDoc Format**: Use standard `/** ... */` format on all public classes, interfaces, composables, ViewModels, functions, and properties.
- **Composable Documentation**: Detail UI purpose, state expectations, and callback parameters (`@param`).

---

## 4. Testing Conventions & Mirror File Rules

### Strict Mirror File Rule
Unit tests in `app/src/test/java` **must strictly mirror** the package structure, directory path, and class naming of source files in `app/src/main/java`.

#### Mirror Mapping Examples:
- Source: `app/src/main/java/com/dhruvanbhalara/dozetap/domain/model/TimeoutOption.kt`
  - Test: `app/src/test/java/com/dhruvanbhalara/dozetap/domain/model/TimeoutOptionTest.kt`
- Source: `app/src/main/java/com/dhruvanbhalara/dozetap/domain/usecase/ToggleKeepScreenOnUseCase.kt`
  - Test: `app/src/test/java/com/dhruvanbhalara/dozetap/domain/usecase/ToggleKeepScreenOnUseCaseTest.kt`
- Source: `app/src/main/java/com/dhruvanbhalara/dozetap/ui/screens/onboarding/OnboardingViewModel.kt`
  - Test: `app/src/test/java/com/dhruvanbhalara/dozetap/ui/screens/onboarding/OnboardingViewModelTest.kt`

### Implementation Plan Test Case Mandate
Every implementation plan created by an AI agent or human developer **must explicitly write and detail relevant test cases** before execution.
- **Happy Path / Correct Flows**: Must cover expected state updates, successful repository actions, and normal UI side-effects.
- **Edge Cases & Failure Flows**: Must cover missing permissions, service disconnections, IPC/process execution failures, null fallbacks, and lifecycle interruptions.

### Testing Best Practices
- **Fake Repositories**: Prefer using Fake implementations (e.g., `FakeTimeoutRepository`, `FakeShizukuRepository`, `FakePlatformSystemManager`) over complex mock frameworks for ViewModel & UseCase testing.
- **Full Flow Coverage**: Unit tests must cover all happy path branches as well as error/failure handling branches to guarantee high code robustness.

---

## 5. Build & Environment Commands

```bash
# Run unit tests
./gradlew test

# Build debug APK
./gradlew assembleDebug
```

---

## 6. Permissions & Security Rules

- **Permission Scope**: DozeTap requires exactly `android.permission.WRITE_SETTINGS`.
- **Zero Network Access**: `AndroidManifest.xml` must never declare network permissions.
- **Clean Architecture Purity**: Use cases in `domain/` must **never** take an Android `Context` parameter.

<!-- lean-ctx -->
## lean-ctx

lean-ctx is active — the MCP tools replace native equivalents.
Full rules: LEAN-CTX.md (open on demand — do not auto-load).
<!-- /lean-ctx -->
