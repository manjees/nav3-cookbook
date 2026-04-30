---
name: nav3-scenes
description: "Complete guide to the Navigation 3 Scene system. Covers Scene, SceneStrategy, SceneDecoratorStrategy, OverlayScene, Dialog, BottomSheet, ListDetail (custom / Material3), TwoPane, SupportingPane, transition animations, and SharedTransition. Use this skill for adaptive layouts, scene decorators, bottom sheets, dialog screens, and custom animations. Use when the user asks 'add bottom sheet', 'dialog screen', 'adaptive layout', 'list-detail', 'two pane', 'custom scene animation', '바텀시트 추가', '다이얼로그', '어댑티브 레이아웃', '씬 데코레이터'."
license: Apache-2.0
---

## Scene System Core Concepts

Scene = the unit that decides how one or more NavEntries are arranged.
SceneStrategy = the strategy that decides which NavEntry combination renders as which Scene.

**NavDisplay processing order:**
1. Try `sceneStrategies` in order; a `null` result falls through to the next strategy
2. If all strategies return `null`, fall back to `SinglePaneSceneStrategy`
3. Apply `sceneDecoratorStrategies` to the resulting Scene in order (OverlayScene is skipped)
4. Render the final `Scene.content()`

---

## Dialog — Built-in

```kotlin
val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }

NavDisplay(
    sceneStrategies = listOf(dialogStrategy), // Must be first since it is an OverlayScene
    entryProvider = entryProvider {
        entry<RouteA> { /* background screen */ }
        entry<DialogRoute>(
            metadata = DialogSceneStrategy.dialog(
                // DialogProperties has dismissOnBackPress, dismissOnClickOutside,
                // securePolicy — no `windowTitle` property.
                DialogProperties(dismissOnClickOutside = false)
            )
        ) { /* dialog content */ }
    }
)
```

---

## BottomSheet — Custom Implementation

You must implement `OverlayScene` so that SceneDecoratorStrategy skips it and the app bar does not attach.

Detailed implementation: `references/overlay-scenes.md`

---

## Material3 ListDetail (Recommended)

```kotlin
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AdaptiveNavigation() {
    val backStack = rememberNavBackStack(ProductList)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailStrategy),
        entryProvider = entryProvider {
            entry<ProductList>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = { Text("Select an item") }
                )
            ) { ProductListScreen() }

            entry<ProductDetail>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key -> ProductDetailScreen(key.id) }

            entry<Profile>(
                metadata = ListDetailSceneStrategy.extraPane()
            ) { ProfileScreen() }
        }
    )
}

// When switching detail, remove the existing detail before adding
fun NavBackStack<NavKey>.replaceDetail(detail: ProductDetail) {
    removeIf { it is ProductDetail }
    add(detail)
}
```

---

## Custom Scene Implementation

### Scene key design is the key

```kotlin
class ListDetailScene<T : Any>(
    override val key: Any,     // Use listEntry.contentKey — not detailEntry.contentKey!
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>,
) : Scene<T> {
    override val entries = listOf(listEntry, detailEntry)
    override val content: @Composable () -> Unit = {
        Row(Modifier.fillMaxSize()) {
            Column(Modifier.weight(0.4f)) { listEntry.Content() }
            Column(Modifier.weight(0.6f)) { detailEntry.Content() }
        }
    }
}
```

**Why use `listEntry.contentKey` as the scene key:** when only the detail changes, the scene key stays the same, so NavDisplay does not run a full scene transition animation. If you use `detailEntry.contentKey`, the animation runs on every detail change.

Full custom Scene implementation → `references/custom-scenes.md`

---

## SceneDecoratorStrategy — Shared App Bar / Navigation UI

```kotlin
class AppScaffoldDecorator<T : Any> : SceneDecoratorStrategy<T> {
    override fun SceneDecoratorStrategyScope<T>.decorateScene(scene: Scene<T>): Scene<T> {
        return AppScaffoldScene(scene)
    }
}

class AppScaffoldScene<T : Any>(private val scene: Scene<T>) : Scene<T> {
    override val key = scene::class to scene.key  // DerivedKeyScene pattern — required!
    override val entries = scene.entries
    override val previousEntries = scene.previousEntries
    override val metadata = scene.metadata
    override val content: @Composable () -> Unit = {
        Scaffold(
            topBar = { TopAppBar(title = { Text("My App") }) }
        ) { padding ->
            Box(Modifier.padding(padding)) { scene.content() }
        }
    }
}

NavDisplay(
    sceneDecoratorStrategies = listOf(AppScaffoldDecorator()),
    // OverlayScene (Dialog/BottomSheet) is automatically excluded from decoration
)
```

Detailed decorator patterns → `references/scene-decorators.md`

---

## Animations

Global + per-scene metadata overrides → `references/animations.md`
