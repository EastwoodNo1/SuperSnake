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

extra["trackedFiles"] = trackedFiles

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

class ProvidedArguments(
    @get:Input val arguments: Provider<List<String>>,
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> = arguments.get()
}

fun registerCheck(
    name: String,
    summary: String,
    arguments: Provider<List<String>>,
) = tasks.register<JavaExec>(name) {
    group = "verification"
    description = summary
    classpath = checks
    mainClass = "sdlc.checks.CliKt"
    argumentProviders.add(ProvidedArguments(arguments))
}

fun gradleProp(name: String) = providers.gradleProperty(name)

registerCheck(
    "checkCommitMessage",
    "Validates one commit message file (-PmessageFile).",
    gradleProp("messageFile").map { listOf("commit-message", it) },
)

registerCheck(
    "checkCommitMessages",
    "Validates NUL-separated commit messages (-PmessagesFile).",
    gradleProp("messagesFile").map { listOf("commit-messages", it) },
)

val checkPullRequestTitle =
    registerCheck(
        "checkPullRequestTitle",
        "Validates the PR title (-PtitleFile).",
        gradleProp("titleFile").map { listOf("pr-title", it) },
    )

val checkPullRequestBody =
    registerCheck(
        "checkPullRequestBody",
        "Validates the PR body (-PbodyFile, -PlabelsFile).",
        gradleProp("bodyFile").zip(gradleProp("labelsFile")) { body, labels -> listOf("pr-body", body, labels) },
    )

val checkTestsChanged =
    registerCheck(
        "checkTestsChanged",
        "Requires test changes beside production changes (-PpathsFile).",
        gradleProp("pathsFile").map { listOf("tests-changed", it, "domain", "data") },
    )

tasks.register("checkPullRequest") {
    group = "verification"
    description = "Runs the PR title, body and tests-changed checks."
    dependsOn(checkPullRequestTitle, checkPullRequestBody, checkTestsChanged)
}

tasks.named("check") {
    dependsOn(checkNoComments)
}

apply(from = "gradle/ktlint.gradle.kts")
