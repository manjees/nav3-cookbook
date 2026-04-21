# Changelog

## [Unreleased] - 2026-04-20

### Changed
- Bumped sample app and skills to Nav3 **1.1.0** stable (`androidx.navigation3:navigation3-runtime` / `navigation3-ui`).
- `lifecycle-viewmodel-navigation3` remains on `2.11.0-alpha03` (still alpha upstream).
- `material3-adaptive-navigation3` remains on `1.3.0-alpha09` (still alpha upstream).
- Plugin skills (`nav3-setup`, etc.) and README badges updated to reflect 1.1.0.
- Tested against Nav3 **1.1.0**.
- Plugin version remains at **1.0.0** (no plugin-visible behavior changes).

## [1.0.0] - 2026-04-17

### Added
- Initial release
- Sample app: basic navigation (NavKey + entryProvider + dropUnlessResumed)
- Sample app: multi-tab navigation (BottomNavigationBar + multi-backstack)
- Sample app: list-detail adaptive layout (Material3 ListDetailSceneStrategy)
- Claude Code plugin with 6 skills: nav3-orchestrator, nav3-setup, nav3-backstack, nav3-scenes, nav3-patterns, nav3-review
- 4 specialized agents: nav3-architect, nav3-implementor, nav3-scene-specialist, nav3-reviewer
- Nav2→Nav3 migration guide
- Anti-patterns reference (AP-1 through AP-10)
- Hilt and Koin modularization guides
