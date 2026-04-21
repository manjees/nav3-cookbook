---
name: nav3-setup
description: "Navigation 3 dependency setup, Gradle additions, version catalog updates, compileSdk 36 configuration, KotlinX Serialization plugin — all Nav3 project initialization work. Use when the user asks 'set up Nav3', 'add Nav3 dependency', 'add navigation3 to Gradle', 'update libs.versions.toml', 'Nav3 설정해줘', '의존성 추가', 'Gradle에 Navigation 추가', 'libs.versions.toml 업데이트'."
---

## Nav3 Dependency Setup Guide

**compileSdk 36 is mandatory.** Some Nav3 APIs do not work below 36.

### libs.versions.toml entries

```toml
[versions]
nav3Core = "1.1.0"
lifecycleViewmodelNav3 = "2.11.0-alpha03"
kotlinSerialization = "2.2.21"
kotlinxSerializationCore = "1.9.0"
material3AdaptiveNav3 = "1.3.0-alpha09"

[libraries]
# Required
androidx-navigation3-runtime = { module = "androidx.navigation3:navigation3-runtime", version.ref = "nav3Core" }
androidx-navigation3-ui = { module = "androidx.navigation3:navigation3-ui", version.ref = "nav3Core" }
# ViewModel scoping (needed for per-NavEntry ViewModels)
androidx-lifecycle-viewmodel-navigation3 = { module = "androidx.lifecycle:lifecycle-viewmodel-navigation3", version.ref = "lifecycleViewmodelNav3" }
# Serialization (required when using rememberNavBackStack)
kotlinx-serialization-core = { module = "org.jetbrains.kotlinx:kotlinx-serialization-core", version.ref = "kotlinxSerializationCore" }
# Material3 Adaptive (for ListDetail / TwoPane / SupportingPane)
androidx-material3-adaptive-navigation3 = { group = "androidx.compose.material3.adaptive", name = "adaptive-navigation3", version.ref = "material3AdaptiveNav3" }

[plugins]
jetbrains-kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlinSerialization" }
```

### app/build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization) // required when using @Serializable
}

android {
    compileSdk = 36 // Required! 35 or lower will not work
    // ...
}

dependencies {
    // Two required dependencies
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    // Optional: state saving + ViewModel scoping
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Optional: Material3 Adaptive layouts
    implementation(libs.androidx.material3.adaptive.navigation3)
}
```

### Self-diagnosis of current project state

To check whether Nav3 is already configured in your project:

```bash
# Check whether nav3Core is present in libs.versions.toml
grep -r "nav3Core\|navigation3" gradle/libs.versions.toml

# Check compileSdk
grep "compileSdk" app/build.gradle.kts
```

- No `nav3Core` → Add all the `libs.versions.toml` entries above
- `compileSdk` below 36 → Bump it up to 36
- No `jetbrains.kotlin.serialization` plugin → Add it to the `[plugins]` section

### Artifact roles

| Artifact | Provided classes | When needed |
|----------|------------------|-------------|
| `navigation3-runtime` | NavEntry, NavKey, entryProvider DSL, rememberNavBackStack, NavEntryDecorator, NavMetadataKey | Always |
| `navigation3-ui` | NavDisplay, Scene, SceneStrategy, SceneDecoratorStrategy | Always |
| `lifecycle-viewmodel-navigation3` | rememberViewModelStoreNavEntryDecorator | When scoping ViewModels per screen |
| `kotlinx-serialization-core` | @Serializable | When using rememberNavBackStack |
| `adaptive-navigation3` | ListDetailSceneStrategy, rememberListDetailSceneStrategy | For Material3 adaptive layouts |

For more detailed version information, see `references/versions.md`.
