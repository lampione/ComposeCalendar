/*
 * Copyright 2022 Matteo Miceli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Properties
import java.net.URI

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
}

android {
    namespace = "com.squaredem.composecalendar"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
                // compose-ui
                implementation(compose.ui)
                implementation(compose.preview)

                // compose-material
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)

                // pager
                implementation("ca.gosyer:accompanist-pager:_")

                // DateTime
                api(KotlinX.datetime)
            }
        }
    }
}

//apply(from = "${rootProject.projectDir}/scripts/publish-module.gradle.kts")

//ext {
//    PUBLISH_GROUP_ID = 'com.squaredem'
//    PUBLISH_ARTIFACT_ID = 'composecalendar'
//    PUBLISH_VERSION = '1.0.4'
//}
//
//apply from: "${rootProject.projectDir}/scripts/publish-module.gradle"

val localProperties = Properties().apply {
    load(File(rootProject.rootDir, "local.properties").inputStream())
}

group = localProperties.getProperty("group", "com.squaredem")
version = "1.0.4"

val dokkaOutputDir = buildDir.resolve("dokka")

tasks.dokkaHtml.configure {
    outputDirectory.set(dokkaOutputDir)
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            url = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = localProperties.getProperty("ossrhUsername", "")
                password = localProperties.getProperty("ossrhPassword", "")
            }
        }
    }
    publications.withType<MavenPublication> {
        artifact(javadocJar)
        pom {
            name.set("ComposeCalendar")
            description.set("A basic library that provides an Android composable view which allows selection of a date from a calendar.")
            url.set("https://github.com/lampione/ComposeCalendar")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("lampione")
                    name.set("Matteo Miceli")
                    email.set("lampione95@gmail.com")
                }
                developer {
                    id.set("sproctor")
                    name.set("Sean Proctor")
                    email.set("sproctor@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:github.com/lampione/ComposeCalendar.git")
                developerConnection.set("scm:git:ssh://github.com/lampione/ComposeCalendar.git")
                url.set("https://github.com/lampione/ComposeCalendar/tree/main")
            }
        }
    }
}

ext["signing.keyId"] = localProperties.getProperty("signing.keyId", "")
ext["signing.password"] = localProperties.getProperty("signing.password", "")
ext["signing.secretKeyRingFile"] = localProperties.getProperty("signing.secretKeyRingFile", "")

signing {
    sign(publishing.publications)
}