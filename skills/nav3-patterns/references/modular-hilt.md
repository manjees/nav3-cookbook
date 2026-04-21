# Modularized Navigation with Hilt

## Overall Structure

```
:app
  └── MainActivity (injects entryBuilders and wires them into NavDisplay)
:common:navigation
  └── Navigator (back stack management)
  └── EntryProviderInstaller (type alias)
:feature:home:api
  └── HomeKey, HomeDetailKey
:feature:home:impl
  └── HomeEntryBuilder (featureHome() extension function)
  └── HomeModule (registers the builder with @IntoSet)
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
fun EntryProviderScope<NavKey>.featureHomeEntryBuilder(navigator: Navigator) {
    entry<HomeKey> {
        HomeScreen(onDetail = { id -> navigator.goTo(HomeDetailKey(id)) })
    }
    entry<HomeDetailKey> { key ->
        HomeDetailScreen(id = key.id)
    }
}

// HomeModule.kt
@Module
@InstallIn(ActivityRetainedComponent::class)
object HomeModule {
    @IntoSet
    @Provides
    fun provideHomeInstaller(navigator: Navigator): EntryProviderInstaller = {
        featureHomeEntryBuilder(navigator)
    }
}
```

## app Module — Application + MainActivity

```kotlin
// App.kt
// @HiltAndroidApp belongs on the Application class, NOT the Activity.
@HiltAndroidApp
class App : Application()

// MainActivity.kt
// Activities that receive @Inject fields must be marked @AndroidEntryPoint.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var entryInstallers: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavDisplay(
                backStack = navigator.backStack,
                onBack = { navigator.goBack() },
                entryDecorators = listOf(
                    // rememberSaveableStateHolderNavEntryDecorator is in
                    // androidx.navigation3.runtime (NOT .ui)
                    rememberSaveableStateHolderNavEntryDecorator(),
                    // rememberViewModelStoreNavEntryDecorator is in
                    // androidx.lifecycle.viewmodel.navigation3
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entryInstallers.forEach { installer -> this.installer() }
                }
            )
        }
    }
}
```

## Binding the Navigator to ActivityRetainedComponent

```kotlin
@Module
@InstallIn(ActivityRetainedComponent::class)
object NavigationModule {
    @Provides
    @ActivityRetainedScoped
    fun provideNavigator(backStack: NavBackStack<NavKey>): Navigator = Navigator(backStack)

    @Provides
    @ActivityRetainedScoped
    fun provideBackStack(): NavBackStack<NavKey> = NavBackStack(HomeKey)
}
```
