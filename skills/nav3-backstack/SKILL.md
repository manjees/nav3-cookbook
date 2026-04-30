---
name: nav3-backstack
description: "Complete guide to implementing Navigation 3 back stacks. Covers NavKey definitions, entryProvider, NavDisplay, rememberNavBackStack, state saving, ViewModel scoping (per NavEntry), entryDecorators setup, multi back stacks, conditional navigation, returning results, passing arguments to ViewModels, and deep link parsing. Always reference this skill when writing Nav3 code. Use when the user asks 'implement back stack', 'add Nav3 screen', 'scope ViewModel per screen', 'multi back stack', 'conditional navigation', '백스택 구현', '화면 추가', 'NavKey 정의', 'ViewModel 스코핑'."
license: Apache-2.0
---

## Core Pattern — Minimal Working Code

```kotlin
// --- Required imports (pay attention to packages) ---
// NavKey, NavEntry, entryProvider DSL, rememberNavBackStack, NavBackStack,
// rememberSaveableStateHolderNavEntryDecorator, NavMetadataKey:
//     androidx.navigation3.runtime.*
// NavDisplay:
//     androidx.navigation3.ui.NavDisplay
// rememberViewModelStoreNavEntryDecorator:
//     androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
// dropUnlessResumed:
//     androidx.lifecycle.compose.dropUnlessResumed
//
// Do NOT import `entry` — it is a member of `EntryProviderScope` and only
// available inside the `entryProvider { ... }` DSL receiver.

// 1. Define NavKeys
@Serializable data object HomeKey : NavKey
@Serializable data class DetailKey(val id: String) : NavKey

// 2. Back stack
val backStack = rememberNavBackStack(HomeKey)

// 3. NavDisplay
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() }, // Required!
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(), // Must be first
        rememberViewModelStoreNavEntryDecorator()
    ),
    entryProvider = entryProvider {
        entry<HomeKey> {
            HomeScreen(
                onDetail = { id -> backStack.add(DetailKey(id)) }
            )
        }
        entry<DetailKey> { key ->
            DetailScreen(id = key.id)
        }
    }
)
```

## NavKey Design

### Standard pattern
```kotlin
@Serializable sealed interface AppNavKey : NavKey

@Serializable data object Home : AppNavKey                        // Screen with no parameters
@Serializable data class Profile(val userId: String) : AppNavKey  // Screen with parameters
@Serializable data object Settings : AppNavKey
```

### Why NavKey needs @Serializable
`rememberNavBackStack` uses `rememberSaveable` internally for serialization/deserialization. Without it, the back stack cannot be restored after process death.

### NavBackStack is a wrapper, not a SnapshotStateList

`NavBackStack<T : NavKey>` is a concrete class in `androidx.navigation3.runtime`
that **wraps** a `SnapshotStateList<T>` via delegation (`MutableList<T> by base,
StateObject by base`). It does NOT extend `SnapshotStateList`. You cannot pass
a `SnapshotStateList<NavKey>` where a `NavBackStack<NavKey>` is expected.

Constructors:
- `NavBackStack()` — empty
- `NavBackStack(vararg elements: T)` — initialized
- `NavBackStack(base: SnapshotStateList<T>)` — internal

Typically you construct one via `rememberNavBackStack(startKey)`, which returns
`NavBackStack<NavKey>` and persists it with `rememberSerializable`.

## dropUnlessResumed — Why It Is Mandatory

```kotlin
// Wrong pattern: rapid taps add the screen twice
Button(onClick = { backStack.add(Detail("123")) })

// Correct pattern
Button(onClick = dropUnlessResumed { backStack.add(Detail("123")) })
```

`dropUnlessResumed` only processes the click when the current screen is in the `RESUMED` state. During transition animations the previous screen is `PAUSED`, so button clicks are ignored.

## ViewModel Scoping

Per-NavEntry ViewModels — the ViewModel is destroyed when the screen is removed from the back stack.

```kotlin
// entryDecorators configuration
entryDecorators = listOf(
    rememberSaveableStateHolderNavEntryDecorator(), // Required first
    rememberViewModelStoreNavEntryDecorator()        // ViewModel scoping
)

// Usage inside a NavEntry
entry<DetailKey> { key ->
    val viewModel: DetailViewModel = viewModel() // Auto-bound to NavEntry scope
    DetailScreen(viewModel)
}
```

### Passing arguments to a ViewModel with Hilt
```kotlin
entry<ProfileKey> { key ->
    val viewModel: ProfileViewModel = hiltViewModel(
        creationCallback = { factory: ProfileViewModel.Factory ->
            factory.create(key.userId)
        }
    )
    ProfileScreen(viewModel)
}
```

## Multi Back Stacks → see `references/multiple-backstacks.md`
## Conditional Navigation → see `references/conditional-nav.md`
## Deep Links → see `references/deeplinks.md`
## Returning Results → see `references/results.md`
## Shared ViewModel Between Screens → see `references/shared-viewmodel.md`
## Per-Entry Retained Values (no ViewModel) → see `references/retain-values.md`
