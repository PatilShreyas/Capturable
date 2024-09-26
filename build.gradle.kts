import com.diffplug.gradle.spotless.SpotlessExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.maven.publish).apply(false)
    alias(libs.plugins.dokka).apply(false)
    alias(libs.plugins.spotless).apply(false)
}

subprojects {
    plugins.apply(rootProject.libs.plugins.spotless.get().pluginId)

    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")

            ktlint().editorConfigOverride(mapOf("android" to "true"))
            licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
        }
    }
}
