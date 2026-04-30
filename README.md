# nav3-cookbook

![Nav3](https://img.shields.io/badge/Nav3-1.1.0-brightgreen)
![compileSdk](https://img.shields.io/badge/compileSdk-36-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-purple)
![License](https://img.shields.io/badge/license-Apache%202.0-orange)

> Fixes the problem where AI tools generate Nav2 code instead of Nav3.

| Basic | Multi-Tab | List-Detail |
|-------|-----------|-------------|
| <img src="docs/screenshot-basic.png" width="220" /> | <img src="docs/screenshot-multitab.png" width="220" /> | <img src="docs/screenshot-listdetail.png" width="220" /> |

## The Problem

Ask any AI to "build tab navigation" and it generates this:

```kotlin
// ❌ What AI generates (Nav2)
val navController = rememberNavController()
NavHost(navController, startDestination = HomeRoute) {
    composable<HomeRoute> { HomeScreen() }
}
```

Nav3 is fundamentally different. There's no NavController. The back stack is just a list you own.

```kotlin
// ✅ What it should generate (Nav3)
val backStack = rememberNavBackStack(HomeKey)
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider {
        entry<HomeKey> { HomeScreen() }
    }
)
```

This repo fixes that.

## What's Inside

An **Agent Skills library** ([agentskills.io](https://agentskills.io) format), packaged as a Claude Code plugin. The sample app uses every pattern the skills teach.

### 1. Runnable Sample App

Three screens that demonstrate the most common Nav3 patterns:

| Screen | Patterns Covered |
|--------|-----------------|
| `basic/` | NavKey + entryProvider + `dropUnlessResumed` (double-tap prevention) |
| `multitab/` | BottomNavigationBar + per-tab back stack + `NavigationState` |
| `listdetail/` | Material3 `ListDetailSceneStrategy` + adaptive layout |

Built with Nav3 1.1.0, compileSdk 36, Kotlin 2.0+.

### 2. Agent Skills (Claude Code Plugin)

Six `SKILL.md` files that teach Claude the right Nav3 patterns.

| Skill | Purpose |
|-------|---------|
| `nav3-orchestrator` | Routes your request to the right agent/skill |
| `nav3-setup` | Gradle deps + `compileSdk 36` + version catalog |
| `nav3-backstack` | Core patterns + multi-stack + deep links + result passing |
| `nav3-scenes` | Dialog, BottomSheet, ListDetail, TwoPane, animations |
| `nav3-patterns` | Modularization (Hilt/Koin), Nav2→Nav3 migration |
| `nav3-review` | Code review: 10 anti-patterns, Critical/High/Medium checklist |

## Install

### Claude Code (recommended)

```
/plugin install nav3@nav3-marketplace
```

Then ask Claude anything Nav3-related:

```
"Build tab navigation with Nav3"
"Help me migrate from Nav2 to Nav3"
"Review my Nav3 code"
"Convert this Dialog into a BottomSheet"
```

### Other agent runtimes

The `skills/` directory follows the [agentskills.io specification](https://agentskills.io/specification) — an open standard originally developed by Anthropic and now adopted by [35+ runtimes](https://agentskills.io/clients), including **Firebender** (Android-native), **Cursor**, **GitHub Copilot**, **JetBrains Junie**, **OpenAI Codex**, and **Gemini CLI**.

Drop the skills into any of them:

```bash
git clone https://github.com/manjees/nav3-cookbook.git
cp -r nav3-cookbook/skills/* <your-agent-skills-directory>/
```

I've only verified Claude Code myself. If another runtime breaks, [open an issue](https://github.com/manjees/nav3-cookbook/issues).

## Build the Sample App

**Requirements:** Android Studio Meerkat+, Java 17+

```bash
git clone https://github.com/manjees/nav3-cookbook.git
cd nav3-cookbook
./gradlew :app:installDebug
```

> **Note:** Nav3 `lifecycle-viewmodel-navigation3` and `adaptive-navigation3` are still in alpha.
> The version badge at the top of this README reflects the tested version.
> API may change — check [CHANGELOG.md](CHANGELOG.md) for updates.

## Core Nav3 Principles

These come up in every Nav3 codebase. The skills check for them; the sample app uses them all.

### 1. Nav3 has no NavController

The back stack is just a list you own. `androidx.navigation.compose.*` is Nav2 — don't import it.

```kotlin
// ❌ Nav2
val navController = rememberNavController()

// ✅ Nav3
val backStack = rememberNavBackStack(HomeKey)
```

### 2. Wrap navigation calls in `dropUnlessResumed`

Without it, a rapid double tap pushes the screen twice.

```kotlin
// ❌ Bug: double tap navigates twice
Button(onClick = { backStack.add(DetailKey("123")) })

// ✅ Fix
Button(onClick = dropUnlessResumed { backStack.add(DetailKey("123")) })
```

### 3. `rememberSaveableStateHolderNavEntryDecorator` must be first

Otherwise `rememberSaveable` inside screens stops restoring on rotation.

```kotlin
NavDisplay(
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(), // ← MUST be first
        rememberViewModelStoreNavEntryDecorator()
    ),
    ...
)
```

### 4. Dialog/BottomSheet scenes must implement `OverlayScene`

Otherwise `SceneDecoratorStrategy` wraps the dialog with the host scaffold.

```kotlin
// ❌ Bug: app bar shows behind the dialog
class MyDialogScene<T : Any>(...) : Scene<T>

// ✅ Fix: skips decorator chain
class MyDialogScene<T : Any>(...) : OverlayScene<T>
```

### 5. Multi-tab needs per-tab back stacks

A single global stack loses history on tab switches. Keep one `rememberNavBackStack` per tab, swap which one `NavDisplay` reads.

```kotlin
// ❌ Bug: tab switch wipes history
val backStack = rememberNavBackStack(HomeKey)

// ✅ Fix: one stack per tab (see app/src/main/java/com/nav3cookbook/sample/multitab/NavigationState.kt)
val backStacks = tabs.associateWith { tab -> rememberNavBackStack(tab) }
val currentBackStack = backStacks.getValue(activeTab)
```

Full list → [`skills/nav3-review/SKILL.md`](skills/nav3-review/SKILL.md)

## Scope

**In scope:** Nav3 1.1.0+ on Android, Compose, KotlinX Serialization. Adaptive layouts via Material3 `ListDetailSceneStrategy`. ViewModel scoping per `NavEntry`.

**Out of scope:** Nav2, Compose Multiplatform iOS/Desktop targets, custom navigation libraries built on top of AndroidX Navigation.

## Questions & Feedback

→ [Ask in Discussions](https://github.com/manjees/nav3-cookbook/discussions)
→ [File a Bug or Feature Request](https://github.com/manjees/nav3-cookbook/issues)

## Built By

Android Tech Lead (7+ yrs). Previously built multi-model agent pipelines and Compose libraries.

Contributions welcome — especially for patterns not yet covered (SupportingPane, shared element transitions, deep link handling).

## License

Apache 2.0 — see [LICENSE](LICENSE)
