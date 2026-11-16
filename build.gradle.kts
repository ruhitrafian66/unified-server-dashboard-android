// Top-level build file where you can add configuration options common to all sub-projects/modules.

// The Android Gradle plugin compiles Kotlin itself and ships its own Kotlin Gradle plugin.
// Putting ours on the classpath is how a newer compiler is chosen (gradle/libs.versions.toml).
buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}
