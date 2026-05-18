// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
}

extra["compileSdk"] = libs.versions.compileSdk.get().toInt()
extra["minSdk"] = libs.versions.minSdk.get().toInt()
extra["targetSdk"] = libs.versions.targetSdk.get().toInt()
extra["javaVersion"] = JavaVersion.VERSION_17
extra["jvmTarget"] = libs.versions.javaVersion.get()