plugins {
    base
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kover)
}

dependencies {
    kover(project(":domain"))
}

val checks = configurations.create("checks")

dependencies {
    checks("sdlc:checks")
}

val noCommentsExtensions = setOf("kt", "kts", "xml", "toml", "yml", "yaml", "sh", "pro", "properties")

val trackedFiles =
    providers
        .exec {
            commandLine("git", "ls-files", "-co", "--exclude-standard")
        }.standardOutput.asText

val checkNoComments =
    tasks.register<JavaExec>("checkNoComments") {
        group = "verification"
        description = "Fails when a file in scope contains a comment."
        classpath = checks
        mainClass = "sdlc.checks.CliKt"
        val root = layout.projectDirectory
        args(
            listOf("no-comments") +
                trackedFiles
                    .get()
                    .lines()
                    .filter { it.substringAfterLast('.', "") in noCommentsExtensions }
                    .filter { root.file(it).asFile.isFile },
        )
    }

tasks.named("check") {
    dependsOn(checkNoComments)
}

apply(from = "gradle/ktlint.gradle.kts")
