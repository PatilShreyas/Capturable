rootProject.name = "Capturable"

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository/")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
}

include(":app")
include(":capturable")