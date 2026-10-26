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
            commandLine("git", "ls-files", "-z", "-co", "--exclude-standard")
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
                    .split('\u0000')
                    .filter { it.isNotEmpty() }
                    .filter { it.substringAfterLast('.', "") in noCommentsExtensions }
                    .filter { root.file(it).asFile.isFile },
        )
    }

fun registerCheck(
    name: String,
    summary: String,
    arguments: () -> List<String>,
) = tasks.register<JavaExec>(name) {
    group = "verification"
    description = summary
    classpath = checks
    mainClass = "sdlc.checks.CliKt"
    argumentProviders.add(CommandLineArgumentProvider(arguments))
}

fun requiredProperty(name: String) = providers.gradleProperty(name).get()

val gatedModules = listOf("domain", "data")

registerCheck("checkCommitMessage", "Validates one commit message file (-PmessageFile).") {
    listOf("commit-message", requiredProperty("messageFile"))
}

registerCheck("checkCommitMessages", "Validates NUL-separated commit messages (-PmessagesFile).") {
    listOf("commit-messages", requiredProperty("messagesFile"))
}

val checkPullRequestTitle =
    registerCheck("checkPullRequestTitle", "Validates the PR title (-PtitleFile).") {
        listOf("pr-title", requiredProperty("titleFile"))
    }

val checkPullRequestBody =
    registerCheck("checkPullRequestBody", "Validates the PR body (-PbodyFile, -PlabelsFile).") {
        listOf("pr-body", requiredProperty("bodyFile"), requiredProperty("labelsFile"))
    }

val checkTestsChanged =
    registerCheck("checkTestsChanged", "Requires test changes beside production changes (-PpathsFile).") {
        listOf("tests-changed", requiredProperty("pathsFile")) + gatedModules
    }

tasks.register("checkPullRequest") {
    group = "verification"
    description = "Runs the PR title, body and tests-changed checks."
    dependsOn(checkPullRequestTitle, checkPullRequestBody, checkTestsChanged)
}

tasks.named("check") {
    dependsOn(checkNoComments)
}

apply(from = "gradle/ktlint.gradle.kts")
