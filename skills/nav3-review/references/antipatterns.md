# Full List of Navigation 3 Anti-Patterns

## Design Anti-Patterns

### AP-1: Treating NavKey as Any
```kotlin
// Anti-pattern
val backStack = remember { mutableStateListOf<Any>(HomeKey) }
entryProvider = { key: Any -> when (key) { ... } }

// Recommended
sealed interface AppNavKey : NavKey
val backStack = rememberNavBackStack(HomeKey) // NavBackStack<AppNavKey>
entryProvider = entryProvider { entry<HomeKey> { } }
```

### AP-2: A Bloated Single entryProvider
Listing more than 100 screens in one entryProvider. Time to modularize:
- More than 3 features
- Team is split by feature

### AP-3: Complex Business Objects Inside a NavKey
```kotlin
// Anti-pattern: not serializable, duplicates data
@Serializable data class ProductDetailKey(val product: Product) : NavKey

// Recommended: pass only an ID, load in the screen's ViewModel
@Serializable data class ProductDetailKey(val productId: String) : NavKey
```

## Implementation Anti-Patterns

### AP-4: Creating a ViewModel Outside of entryProvider
```kotlin
// Anti-pattern: Activity-scoped ViewModel
val viewModel: DetailViewModel = viewModel()
NavDisplay(
    entryProvider = entryProvider {
        entry<DetailKey> { DetailScreen(viewModel) } // Every screen shares the same ViewModel
    }
)

// Recommended: create inside entryProvider → NavEntry scope
NavDisplay(
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()
    ),
    entryProvider = entryProvider {
        entry<DetailKey> {
            val viewModel: DetailViewModel = viewModel() // NavEntry scope
            DetailScreen(viewModel)
        }
    }
)
```

### AP-5: backStack.clear() Followed by add()
```kotlin
// Anti-pattern: wrong animation + performance cost
backStack.clear()
backStack.add(HomeKey)

// Recommended: keep only what you need, then add
backStack.removeIf { it != HomeKey }
// Or to return to the start screen
while (backStack.size > 1) backStack.removeLastOrNull()
```

### AP-6: Navigation Inside a LaunchedEffect
```kotlin
// Anti-pattern: may run on every recomposition
LaunchedEffect(Unit) {
    backStack.add(ProfileKey) // unconditional
}

// Recommended: use a specific trigger
LaunchedEffect(shouldNavigate) {
    if (shouldNavigate) backStack.add(ProfileKey)
}
```

## Scene Anti-Patterns

### AP-7: Non-OverlayScene Dialog / BottomSheet
```kotlin
// Anti-pattern: OverlayScene not implemented
class MyDialogScene<T : Any>(...) : Scene<T> // SceneDecorator will decorate it

// Recommended
class MyDialogScene<T : Any>(...) : OverlayScene<T>
```

### AP-8: Wrong SceneStrategy Order
```kotlin
// Anti-pattern: OverlayScene strategy placed later
sceneStrategies = listOf(
    listDetailStrategy,  // If this runs first, a dialog could be rendered as list-detail
    dialogStrategy,
)

// Recommended
sceneStrategies = listOf(
    dialogStrategy,      // OverlayScene always first
    bottomSheetStrategy,
    listDetailStrategy,
)
```

### AP-8b: Using `NavMetadataKey.key` — That Property Does Not Exist

`NavMetadataKey<T>` is a plain `public interface NavMetadataKey<T : Any>`. It
has **no members**. The `metadata { put(Key, value) }` DSL stores via
`Key.toString()` internally.

```kotlin
// Anti-pattern: `.key` was never a member of NavMetadataKey
metadata = mapOf(NavDisplay.TransitionKey.key to { _: AnimatedContentTransitionScope<Scene<*>> -> ... })

// Fix (1.1.0): use the metadata DSL
metadata = metadata {
    put(NavDisplay.TransitionKey) { fadeIn() togetherWith fadeOut() }
}

// Fix (version-portable): use the helper functions on NavDisplay
metadata = NavDisplay.transitionSpec { fadeIn() togetherWith fadeOut() }
```

### AP-9: SceneDecoratorStrategy Does Not Manage the Key
```kotlin
// Anti-pattern: no key management → needless animations on scene class changes
class WrapperScene<T : Any>(scene: Scene<T>) : Scene<T> {
    override val key = "wrapper" // fixed value → every screen has the same key
}

// Recommended (first decorator)
override val key = scene::class to scene.key // DerivedKeyScene pattern
```

## Performance Anti-Patterns

### AP-10: Every Tab Always Composed in a Multi Back Stack
```kotlin
// Anti-pattern: renders every tab simultaneously
val allEntries = backStacks.values.flatMap { it.toList() }
NavDisplay(entries = allEntries, ...)

// Recommended: compose only the current tab + the start tab
private fun getTopLevelRoutesInUse(): List<NavKey> =
    if (topLevelRoute == startRoute) listOf(startRoute)
    else listOf(startRoute, topLevelRoute)
```
