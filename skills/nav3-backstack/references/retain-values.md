# Retaining Values Per NavEntry (without a ViewModel)

Sometimes you want a value to survive recomposition and configuration changes
**for the lifetime of a NavEntry** but not need a full `ViewModel`. The
`retain/` recipe in `android/nav3-recipes` demonstrates a lightweight
`retain { ... }` helper backed by a custom decorator.

Use this when:
- You want a trivially simple cache (lazy computed value, random seed, counter
  that should persist until the screen is popped).
- You don't want to introduce a `ViewModel` just to hold a single value.
- You need cleanup when the entry is popped (which `rememberSaveable` cannot
  trigger on pop — it triggers on composition leave, which includes transient
  pauses).

If the value is already fine with `rememberSaveable`, **use that instead** —
this pattern only wins when you want pop-based cleanup or non-saveable values.

## Minimal implementation

```kotlin
// RetainedValuesStore.kt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator

class RetainedValuesStore {
    private val values = mutableMapOf<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> getOrPut(key: String, init: () -> T): T =
        values.getOrPut(key) { init() } as T

    fun clear() = values.clear()
}

val LocalRetainedValuesStore = compositionLocalOf<RetainedValuesStore> {
    error("No RetainedValuesStore — install RetainedValuesStoreNavEntryDecorator")
}

class RetainedValuesStoreNavEntryDecorator<T : Any> : NavEntryDecorator<T> {
    private val registry = mutableMapOf<Any, RetainedValuesStore>()

    override fun onPop(key: Any) {
        registry.remove(key)?.clear()
    }

    @Composable
    override fun DecorateEntry(entry: NavEntry<T>) {
        val store = remember(entry.contentKey) {
            registry.getOrPut(entry.contentKey) { RetainedValuesStore() }
        }
        CompositionLocalProvider(LocalRetainedValuesStore provides store) {
            entry.content(entry.key)
        }
    }
}

@Composable
inline fun <reified T> retain(key: String, noinline init: () -> T): T {
    val store = LocalRetainedValuesStore.current
    return remember(key) { store.getOrPut(key, init) }
}
```

## Usage

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
        remember { RetainedValuesStoreNavEntryDecorator<NavKey>() },
    ),
    entryProvider = entryProvider {
        entry<DetailKey> { key ->
            // Computed once per NavEntry, cleared when the entry is popped.
            val seed = retain("seed") { System.currentTimeMillis() }
            DetailScreen(id = key.id, seed = seed)
        }
    }
)
```

## Retain vs rememberSaveable vs ViewModel

| Need | Use |
|------|-----|
| Simple primitive that survives rotation | `rememberSaveable` |
| Non-saveable value, cleanup on pop | `retain { }` (this file) |
| Observable state, business logic, DI | `ViewModel` via `rememberViewModelStoreNavEntryDecorator` |
| Value shared between parent + child screens | `SharedViewModelStoreNavEntryDecorator` (see `shared-viewmodel.md`) |

## Gotchas

- **No serialization.** `retain { }` does NOT survive process death. Use
  `rememberSaveable` if you need that.
- **Key uniqueness.** The key string must be unique per entry call site.
  Colliding keys inside the same entry will alias values.
- **Decorator order** still matters: saveable state holder first, then
  ViewModel store, then retained values.
