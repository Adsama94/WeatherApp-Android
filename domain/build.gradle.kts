plugins {
    alias(libs.plugins.android.library)
    id("com.google.devtools.ksp")
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "com.adsama.domain"
    compileSdk = rootProject.extra["compileSdk"] as Int

    defaultConfig {
        minSdk = rootProject.extra["minSdk"] as Int
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = rootProject.extra["javaVersion"] as JavaVersion
        targetCompatibility = rootProject.extra["javaVersion"] as JavaVersion
    }
}

dependencies {
    implementation(project(":model"))
    implementation(project(":database"))

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.android.compiler)

    testImplementation(libs.junit)
}