# Reddit r/Kotlin post — copy-paste ready

**Subreddit:** r/Kotlin (68K subscribers)
**Flair:** Library / Project (check current sub options)
**Timing:** 한국 저녁 22~24시 = 미국 아침/점심 = r/Kotlin 피크
**Why r/Kotlin first (not r/androiddev):** less strict on low-karma accounts, friendlier to "I built this" posts, audience overlaps significantly with Android devs.

---

## Title (pick one)

**Option A (기술 중심 — 권장):**
```
Navigation 3 cookbook — runnable sample app covering basic, multi-tab, and list-detail for Nav3 1.1.0 stable
```

**Option B (짧음):**
```
Nav3 1.1.0 cookbook — sample app + Claude Code plugin
```

**Option C (문제 중심, AI 비중 낮춤):**
```
Made a Nav3 cookbook because I kept writing the same patterns across projects
```

---

## Body

```markdown
Nav3 1.1.0 went stable a few weeks ago. I've been using it across a few projects and kept rewriting the same patterns — so I pulled them into one repo.

**https://github.com/manjees/nav3-cookbook**

Three runnable sample screens:

- **basic/** — NavKey + `entryProvider` + `dropUnlessResumed` (prevents double-tap nav bugs)
- **multitab/** — BottomNavigationBar with per-tab independent back stack (`NavigationState` pattern)
- **listdetail/** — Material3 `ListDetailSceneStrategy`, with the correct scene key using the list entry's `contentKey` (not detail's) so you don't get unwanted scene animations when only the detail pane changes

Clone and run — `./gradlew :app:installDebug`. Tested against Nav3 1.1.0 stable, compileSdk 36, Kotlin 2.2.

### Why I built it this way

Nav3 is fundamentally different from Nav2 — there's no `NavController`, the back stack is just a list you own. Most of the confusion I've seen in my team comes from mixing old mental models with the new API:

    // Nav2 — what most people still reflexively write
    val navController = rememberNavController()
    NavHost(navController, startDestination = HomeRoute) {
        composable<HomeRoute> { HomeScreen() }
    }

    // Nav3 — the back stack is a SnapshotStateList wrapper you own
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

The decorator order matters (`SaveableStateHolder` must be first), `dropUnlessResumed` is the idiomatic way to prevent double-tap, and `OverlayScene` is what you implement for dialogs/bottom sheets so the `SceneDecoratorStrategy` chain doesn't wrap them with an app bar.

### Also ships as a Claude Code plugin

If you use Claude Code, the repo is also a plugin — `/plugin install nav3@nav3-marketplace` — that ships skills for setup, back stack, scenes, modularization (Hilt + Koin), migration, and code review. Every code block was cross-checked against the official android/snippets and android/nav3-recipes repos so what Claude generates compiles against 1.1.0.

### Caveats

- `lifecycle-viewmodel-navigation3` and `adaptive-navigation3` are still alpha upstream. Core `navigation3-runtime` / `navigation3-ui` are 1.1.0 stable.
- When alpha artifacts break on upstream, I'll bump the repo. Version badges reflect tested version.
- The Claude plugin is optional — the sample app is a standalone reference.

### Feedback wanted

Patterns missing from my sample that you hit in your own Nav3 work?

Deep link handling, shared element transitions across scenes, SupportingPane adaptive, result passing between screens are on my shortlist. What else?

Apache 2.0. Issues/Discussions enabled.
```

---

## Post-publish checklist

- [ ] First comment: add 1 self-comment within 5 min giving context ("Happy to explain specific design choices if useful")
- [ ] Reply to every top-level comment within 1~2 hours
- [ ] Harvest "what's missing" suggestions → file as GitHub issues labeled `from-reddit`
- [ ] 24h later: if post has 50+ upvotes, consider HN crosspost (after HN account approved)
- [ ] If removed by automod: check r/Kotlin rules, DM mods politely for reason

## Likely questions to preemptively answer

**"Nav3 vs Compose Navigation (Jetpack)?"**
→ Nav3 IS Compose Navigation now. Old `androidx.navigation:navigation-compose` is Nav2. Nav3 is the new artifact set `androidx.navigation3:*`. Different dependency coordinate, not coexistable in one NavDisplay/NavHost setup.

**"Does it work with Compose Multiplatform?"**
→ Core artifacts are Android-only currently. CMP support is a known gap — this repo targets Android first. For CMP, look at `voyager` or Decompose.

**"Why not Voyager/Decompose?"**
→ Not against them — Nav3 is the official Google/AndroidX direction for Android-only apps. If you're cross-platform, those remain strong picks.

## Risk flags

- **AI / Claude mention:** r/Kotlin is more tolerant than r/androiddev but still keep it secondary. Lead with sample app value.
- **"Just another cookbook":** 위험 낮음. Nav3 recent, patterns actually evolving. Fresh content.
- **Self-promotion ratio:** 40 karma OK for r/Kotlin but don't post follow-ups for 1~2 weeks. Participate in others' threads.
