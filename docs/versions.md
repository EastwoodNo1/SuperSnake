# Versions

| Item | Version | Release date | Source | Verified on |
|------|---------|--------------|--------|-------------|
| Gradle | 9.8.2 (distribution SHA-256 `21bd5da2de49b642904077f38c0efdc7778e92b3ed0889392dca61b97e389607`) | 2026-10-19 | https://services.gradle.org/versions/current | 2026-10-23 |
| JDK toolchain | Temurin 21.0.12.1 (toolchain 21) | n/a | `java -version` line: openjdk version "21.0.12.1" 2026-08-18 LTS (Temurin-21.0.12.1+1) | 2026-10-23 |
| AGP | 9.4.1 | 2026-09-18 | https://dl.google.com/android/maven2/com/android/tools/build/gradle/maven-metadata.xml | 2026-10-23 |
| Kotlin | 2.4.20 (2.4.21 deliberately not used: capped by CodeQL support) | 2026-09-07 | https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-gradle-plugin/maven-metadata.xml | 2026-10-23 |
| Compose BOM | 2026.09.00 | 2026-09-09 | https://dl.google.com/android/maven2/androidx/compose/compose-bom/maven-metadata.xml | 2026-10-23 |
| activity-compose | 1.13.0 | 2026-03-11 | https://dl.google.com/android/maven2/androidx/activity/activity-compose/maven-metadata.xml | 2026-10-23 |
| core-ktx | 1.19.1 | 2026-09-23 | https://dl.google.com/android/maven2/androidx/core/core-ktx/maven-metadata.xml | 2026-10-23 |
| Kover | 0.9.11 | 2026-09-29 | https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kover-gradle-plugin/maven-metadata.xml | 2026-10-23 |
| JUnit BOM | 6.1.3 | 2026-08-07 | https://repo1.maven.org/maven2/org/junit/junit-bom/maven-metadata.xml | 2026-10-23 |
| ktlint | 1.8.0 | 2025-11-14 | https://repo1.maven.org/maven2/com/pinterest/ktlint/ktlint-cli/maven-metadata.xml | 2026-10-23 |

Release dates for Maven artifacts are the Last-Modified header of the published POM.

## Known issues

- AGP 9.4.1 emits one Gradle deprecation on every cold configuration: `The Configuration.setVisible(boolean) method has been deprecated. This is scheduled to be removed in Gradle 11.` It comes from `com.android.build.gradle.internal.plugins.BasePlugin$Companion.createAndroidJdkImageConfiguration` (BasePlugin.kt:362), found on 2026-10-23 with `./gradlew help -Dorg.gradle.deprecation.trace=true --warning-mode=all`. `scripts/gradle-strict.sh` allowlists exactly this message and fails on any other deprecation. Remove the allowlist when a stable AGP no longer emits it.
