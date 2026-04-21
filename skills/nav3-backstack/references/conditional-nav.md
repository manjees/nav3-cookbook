# Conditional Navigation

A pattern for branching flows based on a specific condition, like login or onboarding.

## ConditionalNavKey Pattern

```kotlin
// Encode whether authentication is required on the NavKey
sealed class ConditionalNavKey(val requiresLogin: Boolean) : NavKey

@Serializable data object Home : ConditionalNavKey(requiresLogin = false)
@Serializable data object Profile : ConditionalNavKey(requiresLogin = true)
@Serializable data class Login(val redirectToKey: ConditionalNavKey? = null) : ConditionalNavKey(requiresLogin = false)
```

## Navigator with Condition Checks

```kotlin
class ConditionalNavigator(
    val backStack: NavBackStack<ConditionalNavKey>,
    val isLoggedIn: () -> Boolean,
    val onNavigateToRestrictedKey: (ConditionalNavKey) -> ConditionalNavKey = { key ->
        Login(redirectToKey = key)
    }
) {
    fun navigate(key: ConditionalNavKey) {
        if (key.requiresLogin && !isLoggedIn()) {
            backStack.add(onNavigateToRestrictedKey(key))
        } else {
            backStack.add(key)
        }
    }
}
```

## Redirect After Successful Login

```kotlin
entry<Login> { key ->
    LoginScreen(
        onLoginSuccess = {
            // Remove the login screen
            backStack.removeIf { it is Login }
            // Navigate to the originally intended screen
            key.redirectToKey?.let { backStack.add(it) }
        }
    )
}
```

## Conditional Navigation Reacting to State Changes

```kotlin
// Observe auth state in a ViewModel
@Composable
fun AppNavigation() {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val backStack = rememberNavBackStack(Home)

    // On logout, reset the back stack and go to Login
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            backStack.clear()
            backStack.add(Login())
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        // ...
    )
}
```
