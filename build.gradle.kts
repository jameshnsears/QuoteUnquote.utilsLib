buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:9.2.1")
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.8")
        classpath("org.jacoco:org.jacoco.core:0.8.15")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.0")
        classpath("com.diffplug.spotless:spotless-plugin-gradle:8.8.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    plugins.withId("com.android.library") {
        apply(plugin = "com.diffplug.spotless")
        extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            kotlin {
                target("src/**/*.kt")
                ktlint("1.8.0")
                trimTrailingWhitespace()
                endWithNewline()
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
