plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        allWarningsAsErrors = true
    }
}

dependencies {
    api(project(":domain"))
    api(libs.gdx)
}
