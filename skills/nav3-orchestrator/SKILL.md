---
name: nav3-orchestrator
description: "Orchestrator for all Navigation 3 (Nav3) related Android work. Covers NavKey, NavDisplay, NavEntry, BackStack, Scene, SceneStrategy, BottomSheet, Dialog, deep links, multi back stacks, adaptive layouts, ListDetail, TwoPane, animations, modularization, Nav2 to Nav3 migration, ViewModel scoping, conditional navigation, and screen transitions. Use this skill for any Nav3 related task. Simple concept questions (e.g. 'what is NavKey?') can be answered directly. Reruns, updates, partial fixes, and improvements of prior results are also handled here. Use when the user asks 'orchestrate Nav3 work', 'design Nav3 feature', 'Nav3 화면 만들어줘', 'Nav3 구현해줘', '네비게이션 설계', '화면 이동 구현', '백스택 설계'."
license: Apache-2.0
---

## Execution Mode: Hybrid

- **Complex design/implementation** (new features, multi-stack, Scene system): agent team
- **Simple implementation** (adding 1 screen, adding a dependency, adding 1 key): sub-agent

---

## Phase 0: Context Check

Check whether `_workspace/` exists.

- No `_workspace/` → **Initial run**: proceed from Phase 1
- `_workspace/` exists + partial modification request → **Partial rerun**: re-invoke only the relevant agent
- `_workspace/` exists + new request → **New run**: move `_workspace/` to `_workspace_prev/` and start

---

## Phase 1: Request Classification

Classify the request as one of the following:

| Category | Example | Execution Mode |
|----------|---------|----------------|
| **Setup** | "Add Nav3 dependency", "Gradle setup" | Run `nav3-setup` skill directly |
| **Architecture** | "Design screen structure", "Modularization strategy" | Agent team (architect + reviewer) |
| **Feature** | "Add list-detail", "Tab navigation" | Agent team (architect + implementor + scene + reviewer) |
| **Scene** | "Add bottom sheet", "Dialog screen" | Agent team (scene + implementor + reviewer) |
| **Pattern** | "Implement deep links", "Conditional navigation" | Agent team (architect + implementor + reviewer) |
| **Review** | "Review Nav3 code", "Find anti-patterns" | Sub-agent (reviewer) |
| **Quick** | "Add 1 screen", "Add a NavKey" | Sub-agent (implementor) |
| **Migration** | "Migrate from Nav2 to Nav3" | Full agent team |

---

## Phase 2: Team Composition and Task Execution

### Agent team pattern (complex requests)

```
TeamCreate → TaskCreate → Agents self-coordinate via SendMessage → Collect results
```

**Team composition example (new feature):**
1. `nav3-architect`: Design → `_workspace/01_architect_design.md`
2. `nav3-implementor` + `nav3-scene-specialist`: Parallel implementation → `.kt` files
3. `nav3-reviewer`: Review after implementation → `_workspace/03_reviewer_report.md`

**Team size:**
- Simple feature (1-2 screens): architect + implementor + reviewer
- Scene-involved feature: + scene-specialist
- Migration: full team of 4

### Sub-agent pattern (simple requests)

```kotlin
Agent(
    description = "Nav3 implementation",
    subagent_type = "general-purpose",
    model = "opus",
    prompt = "As the nav3-implementor agent, perform [specific task]. Reference the nav3-backstack skill."
)
```

---

## Phase 3: Data Flow

```
_workspace/01_architect_design.md    ← nav3-architect output
         ↓
Actual .kt files                      ← nav3-implementor, nav3-scene-specialist output
         ↓
_workspace/03_reviewer_report.md     ← nav3-reviewer output
```

---

## Phase 4: Result Reporting

1. List of created/modified files
2. Review summary (counts of Critical/High/Medium items)
3. If Critical items exist, report after fixes are applied
4. Specify any required Gradle dependencies

---

## Error Handling

- On agent failure, retry once; if it fails again, continue without that result and note it in the report
- Critical review items must be relayed to the implementor immediately, then re-reviewed after fixes
- If requirements are ambiguous, always confirm the back stack strategy (single/multi) and whether an adaptive layout is needed first

---

## Test Scenarios

**Normal flow:** "Build a home-list-detail screen structure with Nav3"
1. Phase 1: Feature classification
2. architect → Design key hierarchy, decide scene strategy
3. implementor → Implement NavKey, entryProvider, NavDisplay
4. scene-specialist → Implement ListDetailSceneStrategy
5. reviewer → Check dropUnlessResumed, @Serializable, etc.
6. Report final file list + review summary

**Error flow:** reviewer finds a Critical item (missing dropUnlessResumed)
1. reviewer → SendMessage the implementor to request a fix
2. implementor → Modify the file
3. reviewer → Re-review
4. Report once no Critical items remain

**Partial rerun:** "Only modify the scene animations"
1. Phase 0: Check `_workspace/` existence
2. Re-invoke only scene-specialist (skip architect, implementor)
3. reviewer re-checks only the modified files
