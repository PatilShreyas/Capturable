import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17)
    androidTarget {}

    jvm()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(project(":capturable"))
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation("androidx.activity:activity-compose:1.9.2")
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
        }

    }

}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.shreyaspatil.capturableExample"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    namespace = "dev.shreyaspatil.capturableExample"
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CaputrableExample"
            packageVersion = "1.0.0"

            linux {
//                iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
            }
            windows {
//                iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
            }
            macOS {
//                iconFile.set(project.file("desktopAppIcons/MacosIcon.icns"))
                bundleID = "dev.shreyaspatil.capturableExample.desktopApp"
            }
        }
    }
}
