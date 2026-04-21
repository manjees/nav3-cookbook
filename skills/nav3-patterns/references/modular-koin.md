# Modularized Navigation with Koin

## Overall Structure

```
:app
  └── MainActivity (includes koinNavModule and wires NavDisplay)
:common:navigation
  └── Navigator (back stack management)
  └── EntryProviderInstaller (type alias)
:feature:home:api
  └── HomeKey, HomeDetailKey
:feature:home:impl
  └── homeEntryBuilder() (extension function)
  └── homeModule (koinModule)
```

## common/navigation Module

```kotlin
// Navigator.kt
class Navigator(val backStack: NavBackStack<NavKey>) {
    fun goTo(key: NavKey) { backStack.add(key) }
    fun goBack() { backStack.removeLastOrNull() }
}

// EntryProviderInstaller.kt
typealias EntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit
```

## feature/home/impl Module

```kotlin
// HomeEntryBuilder.kt
fun EntryProviderScope<NavKey>.homeEntryBuilder(navigator: Navigator) {
    entry<HomeKey> {
        HomeScreen(onDetail = { id -> navigator.goTo(HomeDetailKey(id)) })
    }
    entry<HomeDetailKey> { key ->
        HomeDetailScreen(id = key.id)
    }
}

// HomeModule.kt
val homeModule = module {
    factory<EntryProviderInstaller> {
        { homeEntryBuilder(get()) }
    }
}
```

## app Module — MainActivity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navigator = getKoin().get<Navigator>()
            val installers = getKoin().getAll<EntryProviderInstaller>()

            NavDisplay(
                backStack = navigator.backStack,
                onBack = { navigator.goBack() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    installers.forEach { installer -> this.installer() }
                }
            )
        }
    }
}
```

## Assembling Koin Modules (Application Class)

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                navigationModule,
                homeModule,
                profileModule,
                // Add new feature modules here
            )
        }
    }
}

// navigationModule
val navigationModule = module {
    single<NavBackStack<NavKey>> { NavBackStack(HomeKey) }
    single { Navigator(get()) }
}
```

## Hilt vs Koin — Decision Criteria

| Situation | Recommendation |
|-----------|----------------|
| Already using Hilt | Stay with Hilt (see `references/modular-hilt.md`) |
| Multi-module + fast builds | Koin (no compile-time code generation) |
| Need to swap modules in tests | Koin (dynamic module swapping is easy) |
| Strict DI validation required | Hilt (compile-time graph validation) |
