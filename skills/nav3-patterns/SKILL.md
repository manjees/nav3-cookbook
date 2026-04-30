---
name: nav3-patterns
description: "Guide to advanced Navigation 3 patterns. Covers modularization (api/impl separation, Hilt/Koin DI), Nav2 to Nav3 migration, common UI patterns (bottom navigation + multi back stacks), and shared Scaffold patterns. Use when the user asks 'modularize navigation', 'migrate from Nav2 to Nav3', 'split navigation with Hilt', 'implement tab navigation', 'common UI pattern', '모듈화해줘', 'Nav2에서 Nav3로 마이그레이션', 'Hilt로 네비게이션 분리', '탭 네비게이션 구현', 'common UI 패턴'."
license: Apache-2.0
---

## Modularization — api/impl Pattern

### Module dependency structure

```
:feature:home:api      → HomeKey, HomeDetailKey (NavKey definitions only)
:feature:home:impl     → HomeScreen, HomeDetailScreen, entryProvider builder
:feature:profile:api   → ProfileKey
:feature:profile:impl  → ProfileScreen
:app                   → NavDisplay, depends on every :impl
```

`home:impl` is allowed to depend on `profile:api` → navigation from home to profile.
`profile:api` does not depend on any impl → no cycles.

### EntryBuilder Pattern

```kotlin
// feature/home/impl/HomeEntryBuilder.kt
fun EntryProviderScope<NavKey>.homeEntryBuilder(
    navigator: Navigator  // navigation callback injection
) {
    entry<HomeKey> {
        HomeScreen(
            onProfile = { navigator.navigate(ProfileKey) }
        )
    }
    entry<HomeDetailKey> { key ->
        HomeDetailScreen(key.id)
    }
}
```

### Hilt DI Integration

Detailed implementation → `references/modular-hilt.md`

### Koin DI Integration

Detailed implementation → `references/modular-koin.md`

---

## Common UI — Bottom Navigation + Multi Back Stacks

```kotlin
@Serializable data object TabHome : NavKey
@Serializable data object TabSearch : NavKey
@Serializable data object TabProfile : NavKey

val TOP_LEVEL_ROUTES = setOf(TabHome, TabSearch, TabProfile)

@Composable
fun MainNavigation() {
    val navigationState = rememberNavigationState(
        startRoute = TabHome,
        topLevelRoutes = TOP_LEVEL_ROUTES
    )
    val navigator = remember { Navigator(navigationState) }
    val decoratedEntries = navigationState.toDecoratedEntries { key ->
        // entryProvider
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                TOP_LEVEL_ROUTES.forEach { tab ->
                    NavigationBarItem(
                        selected = navigationState.topLevelRoute == tab,
                        onClick = { navigator.navigate(tab) },
                        icon = { /* tab icon */ },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavDisplay(
            entries = decoratedEntries,
            onBack = { navigator.goBack() },
            modifier = Modifier.padding(padding)
        )
    }
}
```

Full multi back stack implementation → `references/multiple-backstacks.md` of the nav3-backstack skill

---

## Nav2 → Nav3 Migration

Core principle: **atomic changes** — never mix Nav2 and Nav3 at the same time.

Migration steps:
1. Upgrade to `compileSdk 36`
2. Add dependencies (see the nav3-setup skill)
3. Replace the existing NavController with a Navigator class
4. Replace `NavHost` with `NavDisplay`
5. Replace String-based routes with `@Serializable` NavKeys
6. Replace `NavBackStackEntry` with direct access inside `entryProvider`
7. Remove Nav2 dependencies

Detailed step-by-step guide → `references/migration.md`
