# Versions

| Item | Version | Release date | Source | Verified on |
|------|---------|--------------|--------|-------------|
| Gradle | 9.8.2 (distribution SHA-256 `21bd5da2de49b642904077f38c0efdc7778e92b3ed0889392dca61b97e389607`) | 2026-10-19 | https://services.gradle.org/versions/current | 2026-10-23 |
| JDK toolchain | Temurin 25.0.5+8 (toolchain 25), installed via mise | 2026-10-22 | https://github.com/adoptium/temurin25-binaries/releases/tag/jdk-25.0.5%2B8 | 2026-10-23 |
| mise | 2026.10.18 | 2026-10-23 | https://github.com/jdx/mise/releases/tag/v2026.10.18 | 2026-10-23 |
| gh | 2.103.0 (via `aqua:cli/cli`) | 2026-10-21 | https://github.com/cli/cli/releases/tag/v2.103.0 | 2026-10-23 |
| zizmor | 1.31.1 (via `aqua:zizmorcore/zizmor`) | 2026-10-15 | https://github.com/zizmorcore/zizmor/releases/tag/v1.31.1 | 2026-10-26 |
| gh-stack extension | v0.2.1 (`gh extension install github/gh-stack --pin v0.2.1`) | 2026-10-09 | https://github.com/github/gh-stack/releases/tag/v0.2.1 | 2026-10-26 |
| Android SDK platform | android-37.0, revision 2 (read from `source.properties`) | n/a | https://developer.android.com/tools/releases/platforms | 2026-10-26 |
| Android SDK build-tools | 37.0.0 (read from `source.properties`) | n/a | https://developer.android.com/tools/releases/build-tools | 2026-10-26 |
| Android SDK cmdline-tools | 23.0 (read from `source.properties`) | n/a | https://developer.android.com/tools/releases/tools | 2026-10-26 |
| AGP | 9.4.1 | 2026-09-18 | https://dl.google.com/android/maven2/com/android/tools/build/gradle/maven-metadata.xml | 2026-10-23 |
| Kotlin | 2.4.20 (2.4.21 deliberately not used: capped by CodeQL support) | 2026-09-07 | https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-gradle-plugin/maven-metadata.xml | 2026-10-23 |
| Compose BOM | 2026.09.00 | 2026-09-09 | https://dl.google.com/android/maven2/androidx/compose/compose-bom/maven-metadata.xml | 2026-10-23 |
| activity-compose | 1.13.0 | 2026-03-11 | https://dl.google.com/android/maven2/androidx/activity/activity-compose/maven-metadata.xml | 2026-10-23 |
| core-ktx | 1.19.1 | 2026-09-23 | https://dl.google.com/android/maven2/androidx/core/core-ktx/maven-metadata.xml | 2026-10-23 |
| Kover | 0.9.11 | 2026-09-29 | https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kover-gradle-plugin/maven-metadata.xml | 2026-10-26 |
| JUnit BOM | 6.1.3 | 2026-08-07 | https://repo1.maven.org/maven2/org/junit/junit-bom/maven-metadata.xml | 2026-10-26 |
| ktlint | 1.8.0 | 2025-11-14 | https://repo1.maven.org/maven2/com/pinterest/ktlint/ktlint-cli/maven-metadata.xml | 2026-10-26 |
| kotlin-test-junit5 | 2.4.20 (follows the Kotlin version) | 2026-09-07 | https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-test-junit5/maven-metadata.xml | 2026-10-26 |
| JUnit platform launcher | 6.1.3 (managed by the JUnit BOM) | 2026-08-07 | https://repo1.maven.org/maven2/org/junit/platform/junit-platform-launcher/maven-metadata.xml | 2026-10-26 |

Release dates for Maven artifacts are the Last-Modified header of the published POM.

## Tool pinning

- `mise.toml` pins every dev tool to an exact version. `mise.lock` records the download URL and SHA-256 of each tool for each platform. Install only with `mise install --locked`, which fails when the lock has no matching entry.
- Locked platforms: macos-arm64, linux-x64, linux-arm64. Adoptium publishes no macOS x64 build of Temurin 25.0.5+8, so macos-x64 is not locked for Java.
- gh and zizmor: provenance is `github-attestations`, recorded in `mise.lock` and verified by mise at install time.
- Temurin: SHA pin only. The release is GPG-signed and mise can't verify GPG signatures for Java. Each locked checksum was cross-checked against two sources:
  - Adoptium API: https://api.adoptium.net/v3/assets/release_name/eclipse/jdk-25.0.5%2B8?image_type=jdk&jvm_impl=hotspot&project=jdk
  - the release's `.sha256.txt` asset: https://github.com/adoptium/temurin25-binaries/releases/download/jdk-25.0.5%2B8/<archive name>.sha256.txt

| Platform | Archive | SHA-256 (lock, API and .sha256.txt agree) |
|----------|---------|-------------------------------------------|
| macos-arm64 | OpenJDK25U-jdk_aarch64_mac_hotspot_25.0.5_8.tar.gz | `36dc14d797aaff6d89aab302cd053fdba6207dfdcf772052fa7b023a94b0e857` |
| linux-x64 | OpenJDK25U-jdk_x64_linux_hotspot_25.0.5_8.tar.gz | `68982529869257d0ee6b824ec5aa270ff8b118bb438670da0b97ebdab178d7fb` |
| linux-arm64 | OpenJDK25U-jdk_aarch64_linux_hotspot_25.0.5_8.tar.gz | `bb5b2ded252f21c6090cf780b0a408ad0d83bbb557e702955643557f13231880` |

- Android SDK stays outside mise: the only backend, vfox-android-sdk, has 4 stars and skips checksums. Google doesn't sign SDK packages, and sdkmanager checks SHA-1.

## Known issues

- AGP 9.4.1 emits one Gradle deprecation on every cold configuration: `The Configuration.setVisible(boolean) method has been deprecated. This is scheduled to be removed in Gradle 11.` It comes from `com.android.build.gradle.internal.plugins.BasePlugin$Companion.createAndroidJdkImageConfiguration` (BasePlugin.kt:362), found on 2026-10-23 with `./gradlew help -Dorg.gradle.deprecation.trace=true --warning-mode=all`. `scripts/gradle-strict.sh` allowlists exactly this message and fails on any other deprecation. Remove the allowlist when a stable AGP no longer emits it.
