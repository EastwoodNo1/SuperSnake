import org.gradle.api.attributes.Bundling

val ktlint =
    configurations.create("ktlint") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.SHADOWED))
        }
    }

dependencies {
    ktlint("com.pinterest.ktlint:ktlint-cli:1.8.0")
}

val ktlintArgs = listOf("**/*.kt", "**/*.kts", "!**/build/**")

val ktlintCheck =
    tasks.register<JavaExec>("ktlintCheck") {
        group = "verification"
        description = "Lints all Kotlin sources and scripts with the ktlint CLI."
        classpath = ktlint
        mainClass = "com.pinterest.ktlint.Main"
        jvmArgs("--sun-misc-unsafe-memory-access=allow")
        args(ktlintArgs)
    }

val ktlintFormat =
    tasks.register<JavaExec>("ktlintFormat") {
        group = "formatting"
        description = "Formats all Kotlin sources and scripts with the ktlint CLI."
        classpath = ktlint
        mainClass = "com.pinterest.ktlint.Main"
        jvmArgs("--sun-misc-unsafe-memory-access=allow")
        args(listOf("-F") + ktlintArgs)
    }

tasks.named("check") {
    dependsOn(ktlintCheck)
}
