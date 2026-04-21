# Nav2 → Nav3 Migration Guide

## Prerequisites

- `compileSdk 36` or higher is required
- Type-Safe Navigation must already be in place
- Test coverage is recommended

## Unsupported Features (Check Before Migrating)

- Nested navigation graphs deeper than two levels
- Screens that move across multiple back stacks (shared destinations)
- Direct access to `NavBackStackEntry.arguments`

## Step-by-Step Migration

### Step 1: Swap Dependencies

```toml
# Remove
[libraries]
# androidx-navigation-compose = ...

# Add
androidx-navigation3-runtime = { module = "androidx.navigation3:navigation3-runtime", version.ref = "nav3Core" }
androidx-navigation3-ui = { module = "androidx.navigation3:navigation3-ui", version.ref = "nav3Core" }
```

### Step 2: Route → NavKey

```kotlin
// Before (Nav2)
@Serializable data object HomeRoute
@Serializable data class DetailRoute(val id: String)

// After (Nav3)
@Serializable data object HomeRoute : NavKey
@Serializable data class DetailRoute(val id: String) : NavKey
// You only need to add the NavKey interface
```

### Step 3: NavController → Navigator + NavBackStack

```kotlin
// Before
val navController = rememberNavController()
navController.navigate(DetailRoute("123"))
navController.navigateUp()

// After
val backStack = rememberNavBackStack(HomeRoute)
backStack.add(DetailRoute("123"))
backStack.removeLastOrNull()
```

### Step 4: NavHost → NavDisplay

```kotlin
// Before
NavHost(navController, startDestination = HomeRoute) {
    composable<HomeRoute> { HomeScreen() }
    composable<DetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<DetailRoute>()
        DetailScreen(route.id)
    }
}

// After
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider {
        entry<HomeRoute> { HomeScreen() }
        entry<DetailRoute> { key ->
            DetailScreen(key.id)  // Access directly from the key — no backStackEntry needed
        }
    }
)
```

### Step 5: Remove NavBackStackEntry Usage

```kotlin
// Before
val navBackStackEntry = navController.getBackStackEntry(DetailRoute::class)
val savedStateHandle = navBackStackEntry.savedStateHandle

// After: access SavedStateHandle via the ViewModel
class DetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() { ... }
// Access through viewModel() inside entryProvider
```

### Step 6: Replace the Result-Passing Pattern

```kotlin
// Before: passing results through SavedStateHandle
navController.previousBackStackEntry?.savedStateHandle?.set("result", value)

// After: use the ResultStore pattern
// See the nav3-backstack skill's references/results.md
```

## Notes

- Do not use Nav2 and Nav3 together — two NavDisplays in one Activity will cause back-gesture conflicts
- `NestedNavGraph` is simply expressed as a back stack in Nav3 (no nested NavDisplay needed)
