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
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":composecalendar"))
                implementation(compose.ui)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(AndroidX.activity.compose)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}

compose {
    desktop {
        application {
            mainClass = "com.squaredem.composecalendardemo.MainKt"
        }
    }
}
