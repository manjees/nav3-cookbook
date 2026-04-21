# Complete Custom Scene Implementation Guide

## TwoPane Scene

```kotlin
class TwoPaneScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val primaryEntry: NavEntry<T>,
    val secondaryEntry: NavEntry<T>,
) : Scene<T> {
    override val entries = listOf(primaryEntry, secondaryEntry)
    override val content: @Composable () -> Unit = {
        Row(Modifier.fillMaxSize()) {
            Column(Modifier.weight(1f)) { primaryEntry.Content() }
            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
            Column(Modifier.weight(1f)) { secondaryEntry.Content() }
        }
    }
}

class TwoPaneSceneStrategy<T : Any>(
    private val windowSizeClass: WindowSizeClass
) : SceneStrategy<T> {

    object PrimaryKey : NavMetadataKey<Boolean>
    object SecondaryKey : NavMetadataKey<Boolean>

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) return null

        val secondaryEntry = entries.lastOrNull()
            ?.takeIf { it.metadata[SecondaryKey] == true } ?: return null
        val primaryEntry = entries.findLast { it.metadata[PrimaryKey] == true } ?: return null

        return TwoPaneScene(
            key = primaryEntry.contentKey,  // Using the primary key → no animation when secondary changes
            previousEntries = entries.dropLast(1),
            primaryEntry = primaryEntry,
            secondaryEntry = secondaryEntry
        )
    }
}
```

## Scene Strategy Priority Design Principles

```kotlin
NavDisplay(
    sceneStrategies = listOf(
        // 1. OverlayScene family → must come first
        dialogStrategy,
        bottomSheetStrategy,
        // 2. Adaptive layouts (conditional — only return a Scene when the window is large enough)
        listDetailStrategy,
        twoPaneStrategy,
        // 3. SinglePaneSceneStrategy falls back automatically even without being listed
    )
)
```

Each strategy returns `null` in `calculateScene` when its conditions are not met, so the chain automatically delegates to the next strategy.

## Reading Scene Metadata

`NavEntry.metadata` is `Map<String, Any>`. On 1.1.0+, use `NavMetadataKey` objects
with the `get` / `contains` operator extensions from `androidx.navigation3.runtime`:

```kotlin
// import androidx.navigation3.runtime.contains
// import androidx.navigation3.runtime.get

val isDetail: Boolean = lastEntry.metadata.contains(ListDetailSceneStrategy.DetailKey)
val boolValue: Boolean? = lastEntry.metadata[ListDetailSceneStrategy.DetailKey]
```

On 1.0.x (no `NavMetadataKey` interface), entries stored their metadata with raw
string keys. If you are on 1.0.x, fall back to reading strings directly:

```kotlin
val isDetail = lastEntry.metadata["isDetail"] as? Boolean ?: false
```

Internally the 1.1.0 `NavMetadataKey` operator `get` calls `key.toString()`
before looking into the map, so `NavMetadataKey` has no `.key` property — do not
try `MyKey.key`, it does not exist.
