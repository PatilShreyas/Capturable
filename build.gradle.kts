
plugins {
    id("com.diffplug.spotless") version "6.25.0"
    id("org.jetbrains.kotlin.multiplatform") version "1.9.22" apply false
    id("org.jetbrains.compose") version "1.6.11" apply false
    id("com.android.library") version "8.3.2" apply false

    id("com.vanniktech.maven.publish") version "0.28.0" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
}
