# Implementing Multi Back Stacks

A pattern where each tab maintains its own independent back stack, like tab navigation.

Nav3's `NavDisplay` has a second overload that accepts a pre-decorated
`entries: List<NavEntry<T>>` list instead of a `backStack`. This is the
official hook for multi-backstack patterns — call `rememberDecoratedNavEntries`
from `androidx.navigation3.runtime` per stack, then concatenate what you want
to compose and pass it as `entries`.

Note: `MutableStateSerializer` and `NavKeySerializer` below are **not** types
shipped by Nav3 — they are custom helpers written by your app. Pick any
`rememberSaveable`-friendly way to persist `topLevelRoute`. The core Nav3 APIs
used here are `rememberNavBackStack`, `rememberDecoratedNavEntries`, and the
`entries`-parameter `NavDisplay` overload.

## NavigationState Design

```kotlin
@Composable
fun rememberNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): NavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) {
        mutableStateOf(startRoute)
    }
    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }
    return remember(startRoute, topLevelRoutes) {
        NavigationState(startRoute, topLevelRoute, backStacks)
    }
}

class NavigationState(
    val startRoute: NavKey,
    topLevelRoute: MutableState<NavKey>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute: NavKey by topLevelRoute

    @Composable
    fun toDecoratedEntries(
        entryProvider: (NavKey) -> NavEntry<NavKey>
    ): List<NavEntry<NavKey>> {
        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider
            )
        }
        return getTopLevelRoutesInUse()
            .flatMap { decoratedEntries[it] ?: emptyList() }
    }

    // Return only the current tab combined with the start tab
    private fun getTopLevelRoutesInUse(): List<NavKey> =
        if (topLevelRoute == startRoute) listOf(startRoute)
        else listOf(startRoute, topLevelRoute)
}
```

## Navigator

```kotlin
class Navigator(val state: NavigationState) {
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route  // Switch the top-level tab
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)  // Push onto the current tab
        }
    }

    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Stack for ${state.topLevelRoute} not found")
        if (currentStack.last() == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute  // On a tab root, go back to the first tab
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
```

## Hooking up NavDisplay

```kotlin
val navigationState = rememberNavigationState(
    startRoute = RouteA,
    topLevelRoutes = setOf(RouteA, RouteB, RouteC)
)
val navigator = remember { Navigator(navigationState) }

val decoratedEntries = navigationState.toDecoratedEntries(
    entryProvider = { key -> /* return NavEntry */ }
)

// Pass entries directly to NavDisplay (instead of backStack)
NavDisplay(
    entries = decoratedEntries,
    onBack = { navigator.goBack() },
    sceneStrategies = listOf(...)
)
```

## Notes

- Call `rememberDecoratedNavEntries` independently for each stack.
- State is preserved across tab switches because each tab's NavBackStack is managed independently via `rememberNavBackStack`.
- `getTopLevelRoutesInUse` only combines the start tab and the current tab so that the remaining tabs — which are offscreen — stay out of composition for a performance win.
