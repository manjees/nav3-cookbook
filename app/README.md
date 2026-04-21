# Sample App

This Android app demonstrates the three most common Navigation 3 patterns. Gradle files will be added separately.

## Screen Modules

### `basic/`

**Patterns covered:** NavKey + entryProvider + `dropUnlessResumed`

A simple two-screen flow (Home → Detail) that demonstrates:
- Defining `@Serializable` NavKey types (`HomeKey`, `DetailKey`)
- Wiring `NavDisplay` with `entryProvider`
- Wrapping navigation calls in `dropUnlessResumed` to prevent double-tap navigation
- `rememberNavBackStack` for configuration-safe back stack state
- `entryDecorators` setup with `rememberSaveableStateHolderNavEntryDecorator` first

### `multitab/`

**Patterns covered:** BottomNavigationBar + per-tab back stack + `NavigationState`

A three-tab app (Home, Search, Profile) that demonstrates:
- `NavigationState` holding one `NavBackStack` per tab
- `rememberDecoratedNavEntries` combining all tab stacks for `NavDisplay`
- `Navigator` class routing between top-level tabs and within-tab pushes
- Tab state preservation across tab switches
- Performance optimization: only active tab + start tab are composed

### `listdetail/`

**Patterns covered:** Material3 `ListDetailSceneStrategy` + adaptive layout

A product list/detail screen that demonstrates:
- `rememberListDetailSceneStrategy` for automatic single-pane / dual-pane switching
- `ListDetailSceneStrategy.listPane()` and `.detailPane()` metadata
- Using `listEntry.contentKey` (not `detailEntry.contentKey`) as Scene key to avoid spurious animations
- `replaceDetail()` helper to swap detail without stacking
- `detailPlaceholder` for empty-state on large screens

## Build Instructions

Requirements: Android Studio Meerkat+, Java 17+

```bash
./gradlew :app:installDebug
```
