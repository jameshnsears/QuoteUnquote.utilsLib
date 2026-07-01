import java.util.Properties
import org.gradle.api.attributes.Bundling
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

plugins {
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
    id("jacoco")
}

apply(from = "../jacoco.gradle")

// --- Detekt ---
detekt {
    debug = true
    ignoreFailures = false
    buildUponDefaultConfig = true
    config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
    baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
    parallel = true
}

// --- Ktlint ---
val ktlintConfiguration = configurations.create("ktlint")
dependencies {
    ktlintConfiguration("com.pinterest:ktlint:0.51.0-FINAL") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        }
    }
}

tasks.register<JavaExec>("ktlint") {
    group = "verification"
    description = "Check Kotlin code style."
    classpath = ktlintConfiguration
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "--android",
        "--color",
        "src/**/*.kt",
        "**.kts",
        "!**/build/**"
    )
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Fix Kotlin code style deviations."
    classpath = ktlintConfiguration
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "-F",
        "--android",
        "src/**/*.kt",
        "**.kts",
        "!**/build/**"
    )
}

// --- Properties Handling ---
fun Project.loadOrCreateLocalProperties(): Properties {
    val props = Properties()
    val localPropertiesFile = project.file("local.properties")
    if (!localPropertiesFile.exists()) {
        props.setProperty("APPCENTER_KEY_PRODUCTION", "")
        props.setProperty("APPCENTER_KEY_DEVELOPMENT", "")
        localPropertiesFile.bufferedWriter().use { writer ->
            props.store(writer, "Generated automatically. Add keys as needed.")
        }
    } else {
        localPropertiesFile.inputStream().use { props.load(it) }
    }
    return props
}

val localProperties = loadOrCreateLocalProperties()

// --- Android Configuration ---
android {
    namespace = "com.github.jameshnsears.quoteunquote.utils"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildFeatures {
            buildConfig = true
        }
    }

    buildTypes {
        fun getAppCenterKey(key: String): String {
            return System.getenv(key) ?: localProperties.getProperty(key) ?: ""
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "APPCENTER_KEY", "\"${getAppCenterKey("APPCENTER_KEY_PRODUCTION")}\"")
        }

        getByName("debug") {
            enableUnitTestCoverage = true
            buildConfigField("String", "APPCENTER_KEY", "\"${getAppCenterKey("APPCENTER_KEY_DEVELOPMENT")}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    flavorDimensions += "Version"
    productFlavors {
        create("fdroid") { dimension = "Version" }
        create("googleplay") { dimension = "Version" }
        create("espresso") { dimension = "Version" }
        create("uiautomator") { dimension = "Version" }
    }

    sourceSets {
        getByName("fdroid") { java.directories.add("src/fdroid/java") }
        getByName("googleplay") { java.directories.add("src/googleplay/java") }
        getByName("espresso") { java.directories.add("src/fdroid/java") }
        getByName("uiautomator") { java.directories.add("src/fdroid/java") }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    // Jacoco configuration for Robolectric
    tasks.withType<Test>().configureEach {
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    lint {
        abortOnError = true
        checkAllWarnings = true
        htmlReport = true
        warningsAsErrors = false
        xmlReport = false
    }
}

// --- Dependencies ---
dependencies {
    "googleplayImplementation"("com.google.firebase:firebase-crashlytics:20.0.6")

    implementation("androidx.annotation:annotation:1.10.0")
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("com.jakewharton.timber:timber:5.0.1")

    testImplementation("androidx.test.ext:junit:1.3.0")
    testImplementation("androidx.test:runner:1.7.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.16.1")
}
