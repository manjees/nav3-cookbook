# Returning Results Between Screens

## Approach 1: State-based (Recommended — Survives Configuration Changes)

```kotlin
// ResultStore: provided via CompositionLocal
class ResultStore {
    private val results = mutableStateMapOf<KClass<*>, Any?>()
    
    fun <T> setResult(value: T) {
        results[value!!::class] = value
    }
    
    @Composable
    fun <T> getResultState(klass: KClass<T>): State<T?> {
        @Suppress("UNCHECKED_CAST")
        return derivedStateOf { results[klass] as? T } as State<T?>
    }
    
    fun <T : Any> removeResult(klass: KClass<T>) {
        results.remove(klass)
    }
}

val LocalResultStore = compositionLocalOf<ResultStore> { error("No ResultStore") }

// Wrap the NavDisplay
val resultStore = remember { ResultStore() }
CompositionLocalProvider(LocalResultStore provides resultStore) {
    NavDisplay(...)
}

// Set the result on the detail screen
entry<SelectPersonKey> {
    val resultStore = LocalResultStore.current
    SelectPersonScreen(
        onPersonSelected = { person ->
            resultStore.setResult(person)
            backStack.removeLastOrNull()
        }
    )
}

// Receive the result on the home screen
entry<HomeKey> {
    val resultStore = LocalResultStore.current
    val selectedPerson by resultStore.getResultState(Person::class)
    HomeScreen(selectedPerson = selectedPerson)
}
```

## Approach 2: Event-based (One-Shot Events)

```kotlin
// Deliver events through a SharedFlow
class ResultEventBus {
    private val _events = MutableSharedFlow<Any>(extraBufferCapacity = 1)
    val events: SharedFlow<Any> = _events.asSharedFlow()
    
    fun send(event: Any) { _events.tryEmit(event) }
}

// Provide via ViewModel or CompositionLocal, then use it
resultEventBus.send(SelectedPerson(id = "123", name = "Alice"))

// Receiver side
LaunchedEffect(Unit) {
    resultEventBus.events
        .filterIsInstance<SelectedPerson>()
        .collect { person -> /* handle */ }
}
```

## Choosing Between the Two

- **State-based**: when the result must remain visible on screen and must survive configuration changes
- **Event-based**: one-shot feedback such as showing a snackbar — the result should disappear after consumption
