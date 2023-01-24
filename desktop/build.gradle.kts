plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":app-common"))
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
