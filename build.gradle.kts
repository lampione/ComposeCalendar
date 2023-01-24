import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
    id("com.vanniktech.maven.publish.base") version "0.23.2" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins.withId("com.vanniktech.maven.publish.base") {
        @Suppress("UnstableApiUsage")
        configure<MavenPublishBaseExtension> {
            publishToMavenCentral(SonatypeHost.S01)
            signAllPublications()
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
}