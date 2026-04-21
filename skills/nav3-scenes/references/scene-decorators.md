# SceneDecoratorStrategy Detailed Guide

## Execution Order and Data Flow

```
sceneDecoratorStrategies = listOf(d1, d2, d3)

calculateScene() → rawScene
d1.decorateScene(rawScene) → scene1
d2.decorateScene(scene1) → scene2
d3.decorateScene(scene2) → finalScene
finalScene.content() is rendered
```

OverlayScene is excluded from this chain.

## The Three Core Patterns

### 1. DerivedKeyScene — Applied Only on the First Decorator

```kotlin
class FirstDecorator<T : Any> : SceneDecoratorStrategy<T> {
    override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> {
        return DerivedKeyScene(scene)
    }
}

class DerivedKeyScene<T : Any>(private val scene: Scene<T>) : Scene<T> {
    // Original class + original key composed as the new key → guarantees key stability across the decorator chain
    override val key = scene::class to scene.key
    override val entries = scene.entries
    override val previousEntries = scene.previousEntries
    override val metadata = scene.metadata
    override val content: @Composable () -> Unit = { scene.content() }
}
```

### 2. CopyingScene — Copies the Key on Subsequent Decorators

```kotlin
class SubsequentDecorator<T : Any> : SceneDecoratorStrategy<T> {
    override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> {
        return CopyingScene(scene)
    }
}

class CopyingScene<T : Any>(private val scene: Scene<T>) : Scene<T> {
    override val key = scene.key          // Copy the key from the previous decorator as-is
    override val entries = scene.entries
    override val previousEntries = scene.previousEntries
    override val metadata = scene.metadata
    override val content: @Composable () -> Unit = {
        // Shared UI wrapping example: navigation bar
        Column {
            Box(Modifier.weight(1f)) { scene.content() }
            BottomNavigationBar()
        }
    }
}
```

### 3. Conditional Decoration — Decorate Only Specific Screens

```kotlin
class ConditionalDecorator<T : Any> : SceneDecoratorStrategy<T> {
    override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> {
        // Decide whether to decorate based on metadata
        val hideTopBar = scene.metadata["hideTopBar"] as? Boolean ?: false
        return if (hideTopBar) scene  // Skip decoration
        else AppBarScene(scene)
    }
}
```

## Recommended Real-World Structure (App-wide Shared UI)

```kotlin
NavDisplay(
    sceneDecoratorStrategies = listOf(
        DerivedKeyDecoratorStrategy(),    // First: stabilize the key
        AppBarDecoratorStrategy(),        // Second: app bar
        BottomNavDecoratorStrategy(),     // Third: bottom navigation
    ),
    sceneStrategies = listOf(
        dialogStrategy,                   // OverlayScene → excluded from decoration
        bottomSheetStrategy,              // OverlayScene → excluded from decoration
        listDetailStrategy,
    )
)
```
