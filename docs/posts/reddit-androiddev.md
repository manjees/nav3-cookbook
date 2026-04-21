# Reddit r/androiddev post — copy-paste ready

**Subreddit:** r/androiddev
**Flair:** Library / Open Source (check current sub options)
**Timing:** Weekday US morning (9~11 AM EST) or evening (6~8 PM EST) for best engagement

---

## Title (pick one)

**Option A (problem-first, recommended):**
```
Every AI still generates Nav2 code when you ask for navigation — I built a cookbook + Claude plugin that fixes that
```

**Option B (neutral):**
```
Navigation 3 cookbook: runnable sample app (basic / multi-tab / list-detail) + Claude Code plugin
```

**Option C (shortest):**
```
Nav3 cookbook + Claude Code plugin for correct Nav3 code generation
```

---

## Body

```markdown
Nav3 1.1.0 went stable and I've been using it on a side project. The painful part wasn't the new API — it was that every AI tool I tried kept generating Nav2 code (`NavController`, `NavHost`, `composable<Route>`). Models haven't caught up yet, and the old API is still heavily represented in their training data.

I ended up building two things to fix this for myself and figured I'd share:

1. A runnable sample app with three screens covering the patterns most people actually need
2. A Claude Code plugin (skills + agents) that teaches the AI the correct Nav3 patterns

**Repo:** https://github.com/manjees/nav3-cookbook

### Before / After

What I kept getting:

    val navController = rememberNavController()
    NavHost(navController, startDestination = HomeRoute) {
        composable<HomeRoute> { HomeScreen() }
    }

What Nav3 actually looks like:

    val backStack = rememberNavBackStack(HomeKey)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<HomeKey> { HomeScreen() }
        }
    )

No controller. The back stack is just a list you own.

### The sample app covers

- **basic/** — NavKey + entryProvider + `dropUnlessResumed` for double-tap prevention
- **multitab/** — BottomNavigationBar with independent back stack per tab (`NavigationState` pattern)
- **listdetail/** — Material3 `ListDetailSceneStrategy` with the correct scene key (using the list entry's contentKey, not detail's — prevents unwanted scene animations)

Built against Nav3 1.1.0 stable, compileSdk 36, Kotlin 2.2. Clone and run — `./gradlew :app:installDebug`.

### The Claude plugin

Six skills (setup / backstack / scenes / patterns / review / orchestrator) and four specialized agents. After `/plugin install nav3@nav3-marketplace`, asking Claude for anything Nav3-related pulls in the correct patterns: `rememberNavBackStack`, `dropUnlessResumed`, `OverlayScene` for dialogs/bottom sheets, correct `SceneStrategy` ordering, the decorator chain order, etc.

Every code block in the skills was cross-checked against the official `android/snippets` and `android/nav3-recipes` repos. Ten anti-patterns are documented with before/after examples in the `nav3-review` skill.

### Caveats I want to be upfront about

- `lifecycle-viewmodel-navigation3` and `adaptive-navigation3` are still alpha. Core `navigation3-runtime` / `navigation3-ui` are 1.1.0 stable.
- When those alpha artifacts break on upstream changes, I'll bump the repo. Version badges on the README reflect the tested version.
- The Claude plugin requires Claude Code. If you don't use it, the sample app is still a standalone reference.

### What I want feedback on

The sample covers basic / multi-tab / list-detail. What patterns are missing? I'm considering adding:

- Deep link handling (URL → synthetic back stack)
- Shared element transitions across NavEntries
- SupportingPane adaptive layout
- Result passing between screens (ResultStore pattern)

If there's a pattern you hit in your own Nav3 migration and couldn't find a clean reference for, comment below — I'll prioritize what gets covered next.

Apache 2.0. Issues/Discussions are enabled on the repo.
```

---

## Post-publish checklist

- [ ] Engage with first 5-10 comments within 2 hours (visibility multiplier)
- [ ] Add new issues from comments as GitHub issues with `from-reddit` label
- [ ] If post removed by automod: check karma, crosspost timing, link count
- [ ] 24h later: consider crossposting to r/Kotlin (different audience, same content)

## Backup channels (if r/androiddev removes)

Order: HackerNews (Show HN) → X/Twitter → r/Kotlin
Space each 24h apart.

## Known risks

- **Link count:** post has 1 GitHub link only (URL at top). Should pass link-spam filter.
- **Promotion tone:** post opens with personal pain, not marketing claim. Reduces self-promo flags.
- **Claude mention:** r/androiddev has AI fatigue. I led with the sample app value, Claude plugin is secondary. Adjust ratio if first comments push back.
