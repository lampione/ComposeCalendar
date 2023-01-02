plugins {
    id("com.android.application")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.squaredem.composecalendardemo"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    android()

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(AndroidX.core.ktx)
                implementation(compose.ui)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)
                implementation(compose.preview)
                implementation(AndroidX.lifecycle.runtime.ktx)
                implementation(AndroidX.activity.compose)
                implementation(project(":composecalendar"))
            }
        }
    }
}
