import javax.inject.Inject

abstract class ExtractNatives : DefaultTask() {
    @get:InputFiles
    abstract val jars: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Inject
    abstract val archives: ArchiveOperations

    @get:Inject
    abstract val files: FileSystemOperations

    @TaskAction
    fun extract() {
        files.delete { delete(outputDir) }
        jars.forEach { jar ->
            val abi = jar.name.substringAfter("natives-").removeSuffix(".jar")
            files.copy {
                from(archives.zipTree(jar)) { include("*.so") }
                into(outputDir.dir(abi))
            }
        }
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.eastwoodno1.supersnake"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.eastwoodno1.supersnake"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
    lint {
        warningsAsErrors = true
        abortOnError = true
        checkDependencies = true
        disable += "NewerVersionAvailable"
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        allWarningsAsErrors = true
    }
}

val natives = configurations.create("natives")

androidComponents {
    onVariants { variant ->
        val extract =
            tasks.register<ExtractNatives>("extract${variant.name.replaceFirstChar { it.uppercase() }}Natives") {
                jars.from(natives)
            }
        variant.sources.jniLibs?.addGeneratedSourceDirectory(extract, ExtractNatives::outputDir)
    }
}

dependencies {
    implementation(project(":board"))
    implementation(project(":data"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.fragment.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.gdx.backend.android)
    natives(variantOf(libs.gdx.platform) { classifier("natives-arm64-v8a") })
    natives(variantOf(libs.gdx.platform) { classifier("natives-armeabi-v7a") })
    natives(variantOf(libs.gdx.platform) { classifier("natives-x86_64") })
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.junit4)
    constraints {
        androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    }
}
