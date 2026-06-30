plugins {
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt")
    id("jacoco")
}

// --- Detekt ---
detekt {
    debug = true
    ignoreFailures = false
    buildUponDefaultConfig = true
    config.setFrom(files("${project.projectDir}/../detekt.yml"))
    parallel = true
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
}

// Manual ktlint tasks for backward compatibility or specific use cases
val ktlintConfiguration = configurations.create("ktlint")
dependencies {
    ktlintConfiguration("com.pinterest.ktlint:ktlint-cli:1.8.0") {
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
        "--color",
        "src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
}

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Fix Kotlin code style deviations."
    classpath = ktlintConfiguration
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "-F",
        "src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
}

// --- Jacoco ---
jacoco {
    toolVersion = "0.8.15"
}

project.afterEvaluate {
    val android = extensions.getByType<com.android.build.api.dsl.LibraryExtension>()
    val buildTypes = android.buildTypes.map { it.name }
    val productFlavors = android.productFlavors.map { it.name }.toMutableList()

    if (productFlavors.isEmpty()) productFlavors.add("")

    productFlavors.forEach { productFlavorName ->
        buildTypes.forEach { buildTypeName ->
            val variantName =
                if (productFlavorName.isNotEmpty()) {
                    "${productFlavorName}${buildTypeName.replaceFirstChar { it.uppercase() }}"
                } else {
                    buildTypeName
                }
            val capitalizedVariantName = variantName.replaceFirstChar { it.uppercase() }
            val testTaskName = "test${capitalizedVariantName}UnitTest"

            val fileExclusions =
                listOf(
                    "**/R.class",
                    "**/R$*.class",
                    "**/BuildConfig.*",
                    "**/Manifest*.*",
                    "**/*_Impl*.*",
                    "**/*Binding.*",
                    "**/*Double.*",
                    "**/*Test.*",
                    "**/*Test$*.class",
                    "**/databinding/**",
                    "**/di/**",
                )

            tasks.register<JacocoReport>("test${capitalizedVariantName}Coverage") {
                group = "quoteunquote"
                description = "Generate JaCoCo coverage report for the '$variantName' variant."

                dependsOn(testTaskName)

                val buildDir = layout.buildDirectory.get().asFile
                val kotlinClasses =
                    fileTree("$buildDir/intermediates/built_in_kotlinc/$variantName/compile${capitalizedVariantName}Kotlin/classes") {
                        setExcludes(fileExclusions)
                    }
                val javaClasses =
                    fileTree("$buildDir/intermediates/javac/$variantName/compile${capitalizedVariantName}JavaWithJavac/classes") {
                        setExcludes(fileExclusions)
                    }

                classDirectories.setFrom(files(kotlinClasses, javaClasses))
                sourceDirectories.setFrom(
                    files(
                        "$projectDir/src/main/java",
                        "$projectDir/src/main/kotlin",
                    ),
                )
                executionData.setFrom(
                    fileTree(buildDir) {
                        include(
                            "jacoco/$testTaskName.exec",
                            "outputs/unit_test_code_coverage/" +
                                "${variantName}UnitTest/$testTaskName.exec",
                        )
                    },
                )

                reports {
                    xml.required.set(true)
                    xml.outputLocation.set(file("$buildDir/reports/jacoco/$variantName/$variantName.xml"))
                    html.required.set(true)
                    html.outputLocation.set(file("$buildDir/reports/jacoco/$variantName/html"))
                }
            }
        }
    }
}

// --- Android Configuration ---
android {
    namespace = "com.github.jameshnsears.quoteunquote.utils"
    compileSdk = 37

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildFeatures {
            buildConfig = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        getByName("debug") {
            enableUnitTestCoverage = true
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
