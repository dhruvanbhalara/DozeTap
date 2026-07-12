## Description
<!-- Provide a brief description of the changes introduced in this PR and why they are necessary. -->

## Type of Change
- [ ] 🐛 Bug fix (non-breaking change which fixes an issue)
- [ ] ✨ New feature (non-breaking change which adds functionality)
- [ ] 🎨 UI / Styling update (Jetpack Compose / Material 3 tweaks)
- [ ] 🧪 Tests (Adding missing unit tests or refactoring existing tests)
- [ ] 🔧 Maintenance / CI / Build configuration update

## Linked Issue(s)
<!-- Link any related issues, e.g. Fixes #123 -->

## Architectural & Code Quality Checklist (`AGENTS.md` Rules)
- [ ] **Unit Test Mirror Rule**: Unit test file strictly mirrors the source path under `app/src/test/...` matching `app/src/main/...`.
- [ ] **Compose State Stability**: All UI state models/data classes are annotated with `@Immutable`.
- [ ] **Lifecycle Safety**: Observes StateFlow using `collectAsStateWithLifecycle()`.
- [ ] **Thread Offloading**: System settings read/write operations offloaded to `Dispatchers.IO`.
- [ ] **Clean Architecture Purity**: Domain logic in `domain/` contains zero Android `Context` imports.
- [ ] **Zero Network Privacy**: `AndroidManifest.xml` retains zero network permission declarations.
- [ ] **KDoc Formatting**: Added standard `/** ... */` KDoc comments for new public interfaces, composables, or ViewModels.

## How Has This Been Tested?
- [ ] Ran unit tests locally: `./gradlew testDevDebugUnitTest testProdReleaseUnitTest`
- [ ] Built and verified `.dev` flavor (`./gradlew assembleDevDebug`)
- [ ] Built and verified `prod` flavor (`./gradlew assembleProdRelease`)
