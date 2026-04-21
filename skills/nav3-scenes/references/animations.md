# Navigation 3 Animations

## Global Animation Configuration

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    // Forward (push)
    transitionSpec = {
        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) togetherWith
        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
    },
    // Backward (pop)
    popTransitionSpec = {
        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) togetherWith
        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
    },
    // Predictive back gesture — receives an `@NavigationEvent.SwipeEdge Int`
    predictivePopTransitionSpec = {
        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) togetherWith
        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
    },
    entryProvider = entryProvider { ... }
)
```

`transitionSpec` and `popTransitionSpec` have receiver
`AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform`.
`predictivePopTransitionSpec` has receiver
`AnimatedContentTransitionScope<Scene<T>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform`
(the swipe edge is passed as an argument, not captured via the scope).

## Per-Screen Animation Overrides (1.1.0 metadata DSL)

Use the type-safe `metadata { put(...) }` DSL and the `NavMetadataKey` objects
nested inside `NavDisplay`.

```kotlin
// import androidx.navigation3.runtime.metadata
// import androidx.navigation3.ui.NavDisplay

entry<ModalScreen>(
    metadata = metadata {
        put(NavDisplay.TransitionKey) {
            // AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform
            slideInVertically(initialOffsetY = { it }) togetherWith
                ExitTransition.KeepUntilTransitionsFinished
        }
        put(NavDisplay.PopTransitionKey) {
            EnterTransition.None togetherWith
                slideOutVertically(targetOffsetY = { it })
        }
        put(NavDisplay.PredictivePopTransitionKey) {
            // Takes a SwipeEdge Int argument in the receiver lambda
            EnterTransition.None togetherWith
                slideOutVertically(targetOffsetY = { it })
        }
    }
) { ModalContent() }
```

The three `NavMetadataKey` objects live under `NavDisplay`:

| Key | Signature |
|-----|-----------|
| `NavDisplay.TransitionKey` | `NavMetadataKey<AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform>` |
| `NavDisplay.PopTransitionKey` | `NavMetadataKey<AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform>` |
| `NavDisplay.PredictivePopTransitionKey` | `NavMetadataKey<AnimatedContentTransitionScope<Scene<*>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform?>` |

## Per-Screen Animation Overrides — Version-Portable Helpers

The `NavDisplay` companion exposes helper functions that build a
`Map<String, Any>` directly. These exist on both 1.0.x and 1.1.x, so they work
without the DSL. Merge them with `+` if you need multiple.

```kotlin
entry<ModalScreen>(
    metadata =
        NavDisplay.transitionSpec {
            slideInVertically(initialOffsetY = { it }) togetherWith
                ExitTransition.KeepUntilTransitionsFinished
        } + NavDisplay.popTransitionSpec {
            EnterTransition.None togetherWith slideOutVertically(targetOffsetY = { it })
        } + NavDisplay.predictivePopTransitionSpec { _ ->
            EnterTransition.None togetherWith slideOutVertically(targetOffsetY = { it })
        }
) { ModalContent() }
```

Use the DSL form on 1.1.0+; use the helper form when you must stay
compatible with 1.0.x.

## Inter-Scene NavEntry SharedTransition

When the same NavEntry exists on both sides across a scene switch (e.g. single
pane <-> list-detail), `sharedTransitionScope` provides a smooth transition.
The `sharedTransitionScope` parameter was added to `NavDisplay` in 1.1.0.

```kotlin
SharedTransitionLayout {
    NavDisplay(
        sharedTransitionScope = this, // SharedTransitionScope
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider { ... }
    )
}
```

## When Animations Trigger

- When the Scene's **class** or its **`key`** property changes
- Same class + same key -> only recomposition occurs (no animation)
- This is why ListDetail uses `listEntry.contentKey` as the scene key: the scene
  key stays the same when only the detail changes, so the detail swaps without
  any animation.
