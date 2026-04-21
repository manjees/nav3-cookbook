# Twitter / X post — copy-paste ready

**Strategy:** Thread (3~4 tweets). Link in tweet 3 not tweet 1 (Twitter de-ranks posts with links in first tweet).

**Timing:** 한국 저녁 아무때나 (Twitter는 peak 덜 중요). 가능하면 미국 아침 (22:00~24:00 KST).

**Image in tweet 1:** `docs/screenshot-multitab.png` (most visually recognizable — BottomNav + list)

---

## Thread structure

### Tweet 1 (hook, with image)

```
Nav3 1.1.0 is stable but every AI tool I tried still 
generates Nav2 code. NavController, NavHost, composable<Route>.

Built a cookbook repo to fix that.
```
📎 attach: `docs/screenshot-multitab.png`

### Tweet 2 (what's different)

```
Nav3 has no controller. The back stack is just a list you 
own, a NavKey type, and a NavDisplay composable.

3 sample screens:
• basic: NavKey + entryProvider + dropUnlessResumed
• multitab: per-tab back stack via NavigationState
• listdetail: Material3 ListDetailSceneStrategy
```

### Tweet 3 (link + value prop)

```
Clone and run with ./gradlew :app:installDebug
Nav3 1.1.0 stable, compileSdk 36, Kotlin 2.2, Apache 2.0

Also ships as a Claude Code plugin that teaches Claude 
correct Nav3 patterns.

github.com/manjees/nav3-cookbook
```

### Tweet 4 (engagement hook — optional but recommended)

```
What patterns should I add next?

Deep link handling, shared transitions, SupportingPane, 
result passing between screens are on my list — what 
else hit you in your own Nav3 migration?
```

---

## Variant: Single tweet (if you don't want to thread)

```
Built a Navigation 3 cookbook for Android — runnable 
sample app (basic / multi-tab / list-detail) + Claude 
Code plugin that generates correct Nav3 code.

Nav3 1.1.0 stable. Apache 2.0.

github.com/manjees/nav3-cookbook
```
📎 attach: screenshot-multitab.png

---

## Hashtags (only 1~2, more looks spammy)

Use in final tweet only:
- `#AndroidDev` (150K+ followers on that tag)
- `#Kotlin` (larger tag, less Android-specific)
- `#JetpackCompose` (most specific, Compose community)

**Recommended:** `#AndroidDev #JetpackCompose` on tweet 3 or 4.

## After posting

- Pin the tweet (thread) to profile for 1 week
- Reply to any reaction within 1~2h (algorithm boost)
- Retweet with added context 24h later if first post gains traction
- Quote-tweet any Nav3 related thread with "Here's something related I built: [link]" — only if naturally relevant

## Audience amplification

If you know Android devs personally, DM 2~3 people asking them to RT (only people who actually care about Nav3). Cold DMs to strangers = bad look.

Korean devs in network → Korean version of tweet separately (don't mix languages in same thread):
```
Nav3 1.1.0 나왔는데 AI 도구들은 아직 Nav2 코드만 만들어내서 
— 실행 가능한 샘플 앱 + 정확한 Nav3 코드 생성하는 Claude 
Code 플러그인 만들어봤어요

github.com/manjees/nav3-cookbook
```
