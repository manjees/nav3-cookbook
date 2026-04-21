# nav3-cookbook

![Nav3](https://img.shields.io/badge/Nav3-1.1.0-brightgreen)
![compileSdk](https://img.shields.io/badge/compileSdk-36-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-purple)
![License](https://img.shields.io/badge/license-Apache%202.0-orange)

> AI 도구가 Nav3 대신 Nav2 코드를 생성하는 문제를 해결합니다.

| Basic | Multi-Tab | List-Detail |
|-------|-----------|-------------|
| <img src="docs/screenshot-basic.png" width="220" /> | <img src="docs/screenshot-multitab.png" width="220" /> | <img src="docs/screenshot-listdetail.png" width="220" /> |

## 문제

AI에게 "탭 네비게이션 만들어줘"라고 하면 이렇게 나옵니다:

```kotlin
// ❌ AI가 생성하는 코드 (Nav2)
val navController = rememberNavController()
NavHost(navController, startDestination = HomeRoute) {
    composable<HomeRoute> { HomeScreen() }
}
```

Nav3는 구조가 완전히 다릅니다. NavController가 없습니다. 백스택은 그냥 리스트입니다.

```kotlin
// ✅ 올바른 Nav3 코드
val backStack = rememberNavBackStack(HomeKey)
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider = entryProvider {
        entry<HomeKey> { HomeScreen() }
    }
)
```

이 레포가 그 문제를 해결합니다.

## 구성

### 1. 실행 가능한 샘플 앱

Nav3의 핵심 패턴을 보여주는 3개 화면:

| 화면 | 다루는 패턴 |
|------|-----------|
| `basic/` | NavKey + entryProvider + `dropUnlessResumed` (중복 탭 방지) |
| `multitab/` | BottomNavigationBar + 탭별 백스택 + `NavigationState` |
| `listdetail/` | Material3 `ListDetailSceneStrategy` + 어댑티브 레이아웃 |

Nav3 1.1.0, compileSdk 36, Kotlin 2.0+으로 빌드.

### 2. Claude Code 플러그인

올바른 Nav3 패턴을 Claude에게 가르치는 스킬 패키지.

| 스킬 | 역할 |
|------|------|
| `nav3-orchestrator` | 요청을 올바른 에이전트/스킬로 라우팅 |
| `nav3-setup` | Gradle 의존성 + `compileSdk 36` + 버전 카탈로그 |
| `nav3-backstack` | 핵심 패턴 + 멀티백스택 + 딥링크 + 결과 전달 |
| `nav3-scenes` | Dialog, BottomSheet, ListDetail, TwoPane, 애니메이션 |
| `nav3-patterns` | 모듈화 (Hilt/Koin), Nav2→Nav3 마이그레이션 |
| `nav3-review` | 코드 리뷰: 안티패턴 10개, Critical/High/Medium 체크리스트 |

## 플러그인 설치

```
/plugin install nav3@nav3-marketplace
```

설치 후 Claude에게 Nav3 관련 작업을 요청하세요:

```
"Nav3로 탭 네비게이션 만들어줘"
"Nav2 → Nav3 마이그레이션 도와줘"
"내 Nav3 코드 리뷰해줘"
"Dialog를 BottomSheet로 바꿔줘"
```

## 샘플 앱 빌드

**요구사항:** Android Studio Meerkat+, Java 17+

```bash
git clone https://github.com/manjees/nav3-cookbook.git
cd nav3-cookbook
./gradlew :app:installDebug
```

> **참고:** Nav3의 `lifecycle-viewmodel-navigation3`와 `adaptive-navigation3`는 아직 알파 단계입니다.
> README 상단 뱃지가 테스트된 버전을 나타냅니다.
> API가 변경되면 [CHANGELOG.md](CHANGELOG.md)를 확인하세요.

## 질문 & 피드백

→ [Discussions에서 질문하기](https://github.com/manjees/nav3-cookbook/discussions)
→ [버그/기능 요청](https://github.com/manjees/nav3-cookbook/issues)

## 만든 사람

Android Tech Lead (7년+). 멀티모델 에이전트 파이프라인과 Compose 라이브러리 개발 경험.

기여 환영합니다 — 특히 아직 다루지 않은 패턴들 (SupportingPane, 공유 요소 전환, 딥링크 처리).

## 라이선스

Apache 2.0 — [LICENSE](LICENSE) 참조
