# OverlayScene — Dialog / BottomSheet

## OverlayScene Interface

```kotlin
// package androidx.navigation3.scene
interface OverlayScene<T : Any> : Scene<T> {
    val overlaidEntries: List<NavEntry<T>> // Entries to show behind the overlay
    // Optional animation hook invoked before removal; default is an empty suspend.
    suspend fun onRemove() {}
}
```

Implementing `OverlayScene` makes `SceneDecoratorStrategy` skip that scene — app
bar / navigation UI will not attach to dialogs or bottom sheets.

## Full BottomSheet Implementation

```kotlin
// import androidx.navigation3.runtime.NavMetadataKey
// import androidx.navigation3.runtime.get
// import androidx.navigation3.runtime.metadata
// import androidx.navigation3.scene.OverlayScene
// import androidx.navigation3.scene.SceneStrategy
// import androidx.navigation3.scene.SceneStrategyScope

@OptIn(ExperimentalMaterial3Api::class)
data class BottomSheetScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,  // Screens shown behind
    private val entry: NavEntry<T>,
    private val properties: ModalBottomSheetProperties,
    private val onBack: () -> Unit,
) : OverlayScene<T> {
    override val entries = listOf(entry)
    override val content: @Composable () -> Unit = {
        val lifecycleOwner = rememberLifecycleOwner()
        ModalBottomSheet(
            onDismissRequest = onBack,
            properties = properties,
        ) {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                entry.Content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    // SceneStrategy.calculateScene is a scope extension (SceneStrategyScope<T>)
    // with no onBack parameter — `onBack` is available via the scope receiver.
    override fun SceneStrategyScope<T>.calculateScene(
        entries: List<NavEntry<T>>
    ): Scene<T>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val properties = lastEntry.metadata[BottomSheetKey] ?: return null
        return BottomSheetScene(
            key = lastEntry.contentKey,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),  // Visible behind the sheet
            entry = lastEntry,
            properties = properties,
            onBack = onBack, // from SceneStrategyScope<T>
        )
    }

    public companion object {
        public object BottomSheetKey : NavMetadataKey<ModalBottomSheetProperties>

        // Builds a Map<String, Any> via the metadata DSL — mirrors
        // DialogSceneStrategy.dialog(...) in the AndroidX source.
        public fun bottomSheet(
            properties: ModalBottomSheetProperties = ModalBottomSheetProperties()
        ): Map<String, Any> = metadata { put(BottomSheetKey, properties) }
    }
}
```

Key pattern notes confirmed against `DialogSceneStrategy` (Nav3 1.1.0):
- `calculateScene` is declared on `SceneStrategyScope<T>`. The `onBack` callback
  is read from that receiver, not from a parameter.
- Metadata keys are `NavMetadataKey<T>` objects; read with
  `lastEntry.metadata[Key]` (operator from `androidx.navigation3.runtime`).
- The companion helper returns `Map<String, Any>` built via the
  `metadata { put(Key, value) }` DSL. `NavMetadataKey` has no `.key` property —
  the DSL internally uses `key.toString()` to store into the underlying map.

## Usage

```kotlin
val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

NavDisplay(
    sceneStrategies = listOf(
        bottomSheetStrategy,     // OverlayScene → must be first
        listDetailStrategy,      // Regular Scene
    ),
    entryProvider = entryProvider {
        entry<HomeRoute> { HomeScreen() }
        entry<FilterRoute>(
            metadata = BottomSheetSceneStrategy.bottomSheet()
        ) {
            FilterBottomSheet(onDismiss = { backStack.removeLastOrNull() })
        }
    }
)
```

## Built-in DialogSceneStrategy

`DialogSceneStrategy<T>` is shipped in `navigation3-ui`. Its companion exposes
`DialogSceneStrategy.dialog(DialogProperties = DialogProperties())` which returns
a `Map<String, Any>` just like the pattern above.

```kotlin
import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.scene.DialogSceneStrategy

val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }

NavDisplay(
    sceneStrategies = listOf(dialogStrategy, /* others */),
    entryProvider = entryProvider {
        entry<DialogRoute>(
            // DialogProperties has dismissOnBackPress, dismissOnClickOutside,
            // securePolicy, etc. — it does NOT have a `windowTitle` property.
            metadata = DialogSceneStrategy.dialog(
                DialogProperties(dismissOnClickOutside = false)
            )
        ) { /* dialog content */ }
    }
)
```

## Why CompositionLocalProvider(LocalLifecycleOwner)

`ModalBottomSheet` / `Dialog` render in a new window context, so
`LocalLifecycleOwner` is disconnected. Without explicitly providing it via
`rememberLifecycleOwner()` (from `androidx.lifecycle.compose`), internal Compose
state may not work correctly. The official `DialogScene` source does this
exact wrapping.
