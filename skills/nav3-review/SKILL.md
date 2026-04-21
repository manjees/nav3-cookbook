---
name: nav3-review
description: "Navigation 3 code review and anti-pattern detection. Finds bugs, anti-patterns, Nav2 remnants, and performance issues in Nav3 code and suggests fixes. Use when the user asks 'review Nav3 code', 'find anti-patterns', 'check for Nav3 bugs', 'is dropUnlessResumed used correctly', 'audit back stack code', 'Nav3 코드 리뷰해줘', '안티패턴 찾아줘', 'Nav3 관련 버그 체크', 'dropUnlessResumed 맞게 썼나', '백스택 코드 검토'."
---

## Review Process

When reading the code, trace the full flow: NavKey definitions → entryProvider mapping → NavDisplay configuration → Scene strategy wiring. Cross-verify that every NavKey is handled in the entryProvider and that each screen has the correct metadata.

## Critical Checklist

Items that cause runtime crashes or compile errors.

```
□ Every NavKey: @Serializable annotation + NavKey interface implemented
□ Back stack: uses rememberNavBackStack() (remember { mutableStateListOf() } is forbidden)
□ onBack: NavDisplay has onBack = { backStack.removeLastOrNull() }
□ entryProvider: handles every NavKey branch (else → error() or a proper fallback)
□ entryDecorators: rememberSaveableStateHolderNavEntryDecorator() is first in the list

Imports (compile errors if wrong):
□ rememberSaveableStateHolderNavEntryDecorator → androidx.navigation3.runtime (NOT .ui)
□ rememberViewModelStoreNavEntryDecorator    → androidx.lifecycle.viewmodel.navigation3
□ NavDisplay                                  → androidx.navigation3.ui
□ NavKey / NavEntry / NavBackStack / entryProvider / rememberNavBackStack / NavMetadataKey / metadata DSL
                                              → androidx.navigation3.runtime
□ Scene / SceneStrategy / SceneDecoratorStrategy / OverlayScene / DialogSceneStrategy
                                              → androidx.navigation3.scene
□ ListDetailSceneStrategy / rememberListDetailSceneStrategy
                                              → androidx.compose.material3.adaptive.navigation3
□ dropUnlessResumed                           → androidx.lifecycle.compose
□ `entry<T>` is NOT imported — it is a member of EntryProviderScope (DSL receiver)

API shape checks:
□ NavDisplay: sceneStrategies = listOf(...) (plural on 1.1.0; sceneStrategy= is @Deprecated HIDDEN)
□ No use of `NavMetadataKey.key` (property does not exist — use the DSL or NavDisplay.transitionSpec helpers)
□ SceneStrategy.calculateScene is a `SceneStrategyScope<T>` extension (no `onBack` parameter — use the receiver's `onBack`)
□ SceneDecoratorStrategy.decorateScene is a `SceneDecoratorStrategyScope<T>` extension
□ NavBackStack<T> is a wrapper class (MutableList by delegation) — NOT a SnapshotStateList subtype; T is bounded by `NavKey`
```

## High Checklist

Bugs that degrade user experience — duplicate navigation, wrong animations, etc.

```
□ Button / click handlers: wrapped with dropUnlessResumed { }
□ sceneStrategies: OverlayScene strategies (Dialog/BottomSheet) are placed first
□ ListDetail scene key: uses listEntry.contentKey instead of detailEntry.contentKey
□ BottomSheet/Dialog: implements the OverlayScene interface
□ Multi back stacks: uses rememberDecoratedNavEntries (NavDisplay entries parameter)
```

## Medium Checklist

Maintainability and code quality.

```
□ SceneDecoratorStrategy: first decorator applies the DerivedKeyScene pattern
□ NavKey hierarchy: organized as a sealed interface
□ ViewModel scoping: rememberViewModelStoreNavEntryDecorator is used
□ Passing results between screens: uses the ResultStore or Event pattern (never manipulate savedStateHandle directly)
```

## Detecting Nav2 Remnants

Flag immediately on seeing any of the following patterns.

```
□ Use of NavController, NavHost, NavBackStackEntry, rememberNavController
□ navController.navigate(route), navController.navigateUp(), navController.popBackStack()
□ composable<Route> { backStackEntry -> ... } pattern
□ Routes defined via stringResource
□ Passing inter-screen results through SavedStateHandle.get/set
```

## Frequently Found Bug Patterns

### 1. Duplicate Navigation

```kotlin
// Bug: a rapid double tap pushes the screen twice
Button(onClick = { backStack.add(Detail("123")) })

// Fix
Button(onClick = dropUnlessResumed { backStack.add(Detail("123")) })
```

### 2. Back Stack Reset on Rotation

```kotlin
// Bug: back stack always resets to home on rotation
val backStack = remember { mutableStateListOf<NavKey>(HomeKey) }

// Fix
val backStack = rememberNavBackStack(HomeKey)
```

### 3. Dialog Shows the App Bar

```kotlin
// Bug: without OverlayScene, SceneDecoratorStrategy decorates the dialog
class MyDialogScene<T : Any>(...) : Scene<T> { ... }

// Fix: implement OverlayScene
class MyDialogScene<T : Any>(...) : OverlayScene<T> {
    override val overlaidEntries = previousEntries
    ...
}
```

### 4. ListDetail Runs a Full Scene Animation on Every Detail Change

```kotlin
// Bug: using detailEntry.contentKey as the scene key
return ListDetailScene(
    key = detailEntry.contentKey,  // Animation runs every time the detail changes
    ...
)

// Fix: use listEntry.contentKey
return ListDetailScene(
    key = listEntry.contentKey,    // Animation only when the list changes
    ...
)
```

### 5. rememberSaveable Does Not Work Inside a NavEntry

```kotlin
// Bug: entryDecorators is missing SaveableStateHolder
NavDisplay(
    entryDecorators = listOf(rememberViewModelStoreNavEntryDecorator()),
    ...
)

// Fix: SaveableStateHolder must be first
NavDisplay(
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(), // First!
        rememberViewModelStoreNavEntryDecorator()
    ),
    ...
)
```

Full anti-pattern list → `references/antipatterns.md`
