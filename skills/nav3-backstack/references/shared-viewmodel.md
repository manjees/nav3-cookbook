# Shared ViewModel Across NavEntries

Nav3's default `rememberViewModelStoreNavEntryDecorator()` scopes each ViewModel
to a single `NavEntry`. When a parent screen needs to share a `ViewModel`
instance with a child screen (e.g. a wizard flow, or a feature root sharing
state with its subscreens), you need a **custom decorator** — Nav3 does not ship
one out of the box.

This pattern is adapted from the `sharedviewmodel/` recipe in the official
`android/nav3-recipes` repo. The decorator provides a parent
`ViewModelStoreOwner` via a `CompositionLocal` so that any child entry marked
with a parent reference can resolve the parent's ViewModel.

## 1. Decorator and helper

```kotlin
// SharedViewModelStoreNavEntryDecorator.kt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.get

/** Composition local exposing the parent entry's ViewModelStoreOwner. */
val LocalSharedViewModelStoreOwner =
    compositionLocalOf<ViewModelStoreOwner?> { null }

class SharedViewModelStoreNavEntryDecorator<T : Any> : NavEntryDecorator<T> {

    // Registry of ViewModelStores keyed by an arbitrary identifier (we use the
    // parent's contentKey). Cleared when entries are popped.
    private val stores = mutableMapOf<Any, ViewModelStore>()

    override fun onPop(key: Any) {
        stores.remove(key)?.clear()
    }

    @Composable
    override fun DecorateEntry(entry: NavEntry<T>) {
        // Every entry gets its own store as usual.
        val ownKey = entry.contentKey
        val ownStore = remember(ownKey) { stores.getOrPut(ownKey) { ViewModelStore() } }
        val ownOwner = remember(ownStore) {
            object : ViewModelStoreOwner {
                override val viewModelStore = ownStore
            }
        }

        // If the entry declares a parent, resolve (or create) that parent's
        // store and expose it via LocalSharedViewModelStoreOwner.
        val parentKey = entry.metadata[ParentKey]
        val parentOwner = remember(parentKey) {
            parentKey?.let { pk ->
                val store = stores.getOrPut(pk) { ViewModelStore() }
                object : ViewModelStoreOwner {
                    override val viewModelStore = store
                }
            }
        }

        CompositionLocalProvider(
            LocalViewModelStoreOwner provides ownOwner,
            LocalSharedViewModelStoreOwner provides parentOwner,
        ) {
            entry.content(entry.key)
        }
    }

    companion object {
        /** Metadata key telling a child which entry should be its ViewModel parent. */
        object ParentKey : NavMetadataKey<Any>

        fun parent(contentKey: Any): Map<String, Any> =
            metadata { put(ParentKey, contentKey) }
    }
}

@Composable
fun <T : Any> rememberSharedViewModelStoreNavEntryDecorator(): SharedViewModelStoreNavEntryDecorator<T> =
    remember { SharedViewModelStoreNavEntryDecorator() }
```

## 2. Wire it into NavDisplay

`rememberSaveableStateHolderNavEntryDecorator()` must remain first. Put the
shared decorator **after** it and replace
`rememberViewModelStoreNavEntryDecorator()` — the shared decorator manages
stores itself.

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(), // first, always
        rememberSharedViewModelStoreNavEntryDecorator<NavKey>(),
    ),
    entryProvider = entryProvider {
        entry<ParentKey> {
            // Parent uses its own ViewModelStore (LocalViewModelStoreOwner).
            val vm: FlowViewModel = viewModel()
            ParentScreen(vm) { backStack.add(ChildKey) }
        }
        entry<ChildKey>(
            // Declare the parent whose ViewModel you want to share.
            metadata = SharedViewModelStoreNavEntryDecorator.parent(ParentKey)
        ) {
            // Child pulls the parent's store via LocalSharedViewModelStoreOwner.
            val parentOwner = LocalSharedViewModelStoreOwner.current
                ?: error("Child declared parent, but no parent store found")
            val vm: FlowViewModel = viewModel(viewModelStoreOwner = parentOwner)
            ChildScreen(vm)
        }
    }
)
```

## Tradeoffs vs alternatives

| Approach | When to use |
|----------|-------------|
| Shared ViewModel (this) | Multi-step flow sharing state (wizard, checkout, onboarding) |
| Hilt `@Scoped` + assisted injection | Feature-level scoping with DI, cleaner for large apps |
| `ResultStore` / `ResultEventBus` (see `results.md`) | Single value handed back; no ongoing shared state |
| Application-scoped ViewModel | Truly global state (user session, feature flags) |

## Gotchas

- **Scope lifetime is manual.** `onPop` clears the parent store only when the
  parent entry itself is popped. If the child is still on the back stack when
  the parent pops, the child's resolved `ViewModel` will be cleared mid-flight.
  Always pop children first, or guard with `if (backStack.contains(ParentKey))`.
- **Do not swap in place of `rememberViewModelStoreNavEntryDecorator`** without
  verifying that all your screens use the same decorator chain; parent
  resolution is opt-in via metadata, but the decorator still owns every entry's
  store.
- Respect decorator order: the saveable state holder must come first.
