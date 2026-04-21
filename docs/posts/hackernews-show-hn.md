# HackerNews Show HN — copy-paste ready

**URL:** https://news.ycombinator.com/submit
**Timing:** 한국 저녁 ≈ 미국 아침(EST). 화·수·목 오전 9~11 AM EST가 피크. 월·금은 약함.
**Rules:** https://news.ycombinator.com/showhn.html
- 실제로 작동하는 것만 (샘플 앱 빌드 가능 ✓)
- `Show HN:` 접두사 필수
- 제목에 링크 금지 (URL 필드 따로)
- 제목에 "the best", "awesome" 등 마케팅 단어 금지

---

## Title (pick one, all under 80 chars)

**Option A (권장 — 문제 중심):**
```
Show HN: Nav3 cookbook – runnable sample app + Claude Code plugin for Android
```

**Option B (기능 중심):**
```
Show HN: A Navigation 3 cookbook for Android Compose with 3 working samples
```

**Option C (짧음):**
```
Show HN: Navigation 3 cookbook for Android
```

## URL field
```
https://github.com/manjees/nav3-cookbook
```

---

## Body (HN은 plain text — 마크다운 제한적)

```
Nav3 1.1.0 went stable this month. I've been using it on a side project and hit a workflow pain point worth sharing.

Every AI coding tool I tried kept generating Nav2 code (NavController, NavHost, composable<Route>). Nav3 is fundamentally different — no controller, the back stack is just a list you own — but the new API hasn't propagated into model training data yet.

I built two things:

1. A runnable sample Android app with three screens covering the patterns most people need:
   - basic: NavKey + entryProvider + dropUnlessResumed (prevents double-tap navigation bugs)
   - multitab: BottomNavigationBar with independent back stack per tab (the NavigationState pattern)
   - listdetail: Material3 ListDetailSceneStrategy with correct scene key (using list entry's contentKey, not detail's, to avoid unwanted scene animations)

2. A Claude Code plugin (skills + agents) that teaches the AI the correct Nav3 patterns. Every code block was cross-checked against the official android/snippets and android/nav3-recipes repos.

The sample app clones and runs (./gradlew :app:installDebug). Built against Nav3 1.1.0 stable, compileSdk 36, Kotlin 2.2. Apache 2.0.

Caveats: lifecycle-viewmodel-navigation3 and adaptive-navigation3 are still alpha. Core navigation3-runtime and navigation3-ui are stable. Version badges on the README reflect tested version.

Feedback especially welcome on:
- Patterns missing from the sample (deep links, shared transitions, SupportingPane, result passing)
- Anti-patterns not in the review skill's list
- Whether the Nav2-to-Nav3 migration guide covers your project's shape
```

---

## HN 게시 팁

- **제출 후 첫 1시간이 프론트페이지 진입 결정** — 다른 브라우저로 링크 공유 받아 upvote, 댓글 달기
- **본인 댓글 1개를 포스팅 직후 달기** — 맥락 보강 "왜 만들었는지" or "무엇이 빠졌는지". 댓글 1개 = 참여도 신호
- **한국 저녁 시간 = 미국 새벽** — 프론트페이지 경쟁 적은 시간대라 오히려 유리할 수 있음
- **댓글 응답은 1~2시간 내 30분 간격으로** — HN 알고리즘이 작성자 활성도 점수 매김

## 예상 반응

HN 특성상 다음 질문 자주 나옴 — 미리 답변 준비:

**"왜 Claude 전용? 다른 AI는?"**
→ "플러그인은 Claude Code 전용이지만 스킬 내용 자체는 플레인 마크다운 + Kotlin 코드라 Cursor/Copilot에도 프롬프트로 복붙 가능. 샘플 앱은 AI 무관하게 독립 레퍼런스."

**"Nav3가 정말 Nav2 대비 이득인가?"**
→ "Google 공식 방향. 백스택을 직접 소유하니 조건부 네비게이션 / 커스텀 Scene / 멀티백스택 훨씬 단순. 하지만 2단계 이상 중첩 그래프 같은 일부 케이스는 아직 unsupported. README caveats 참조."

**"alpha 의존성 있는데 왜 stable 주장?"**
→ "core runtime/ui는 1.1.0 stable. 옵션인 adaptive-navigation3, lifecycle-viewmodel-navigation3만 alpha. stable 부분만 써도 대부분 usecase 커버됨."

## 포스팅 직후 체크리스트

- [ ] 본인이 첫 댓글 달기 (배경 설명 or 미해결 질문)
- [ ] 30분 후 upvote/comment count 체크
- [ ] 첫 하드 질문에 1시간 내 응답
- [ ] 링크 저장: `https://news.ycombinator.com/item?id=<id>`

## 실패 시

- 첫 1시간 upvote 5 미만이면 프론트페이지 진입 거의 불가능
- 24시간 후 Lobsters (https://lobste.rs) 고려 (Android 태그 미약하지만 품질 콘텐츠에 관대)
- 그 다음 r/Kotlin
- 마지막 r/androiddev (카르마 80+ 달성 후)
