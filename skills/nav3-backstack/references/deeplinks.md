# Deep Link Implementation

## Basic Deep Links — URI → NavKey Conversion

```kotlin
// Add intent-filter in AndroidManifest.xml
// <intent-filter>
//   <action android:name="android.intent.action.VIEW"/>
//   <category android:name="android.intent.category.DEFAULT"/>
//   <category android:name="android.intent.category.BROWSABLE"/>
//   <data android:scheme="https" android:host="example.com"/>
// </intent-filter>

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val backStack = rememberNavBackStack(HomeKey)
            
            // Parse the deep link
            val deepLinkKey = intent?.data?.let { uri -> parseDeepLink(uri) }
            LaunchedEffect(deepLinkKey) {
                deepLinkKey?.let { backStack.add(it) }
            }
            
            NavDisplay(backStack = backStack, ...)
        }
    }
}

fun parseDeepLink(uri: Uri): AppNavKey? {
    return when {
        uri.path?.startsWith("/product/") == true -> {
            val id = uri.lastPathSegment ?: return null
            ProductDetailKey(id)
        }
        uri.path == "/home" -> HomeKey
        else -> null
    }
}
```

## Advanced Deep Links — Synthetic Back Stack

When a deep link enters a deep screen directly, build a proper back stack so that "back" behaves naturally.

```kotlin
fun buildSyntheticBackStack(deepLinkKey: AppNavKey): List<AppNavKey> {
    return when (deepLinkKey) {
        is ProductDetailKey -> listOf(HomeKey, ProductListKey, deepLinkKey)
        is OrderDetailKey -> listOf(HomeKey, OrderListKey, deepLinkKey)
        else -> listOf(HomeKey, deepLinkKey)
    }
}

// Apply
val syntheticStack = buildSyntheticBackStack(deepLinkKey)
backStack.addAll(syntheticStack.drop(1)) // First item is already on the backStack
```

## Handling onNewIntent (When the Activity Is Already Running)

```kotlin
class MainActivity : ComponentActivity() {
    private val newIntentFlow = MutableSharedFlow<Intent>()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lifecycleScope.launch { newIntentFlow.emit(intent) }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val backStack = rememberNavBackStack(HomeKey)
            
            LaunchedEffect(Unit) {
                newIntentFlow.collect { intent ->
                    intent.data?.let { parseDeepLink(it) }?.let { key ->
                        backStack.add(key)
                    }
                }
            }
            
            NavDisplay(backStack = backStack, ...)
        }
    }
}
```
