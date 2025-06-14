import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.baselineprofile)
}

hilt {
    enableAggregatingTask = true
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

fun getApiToken(): String {
    val properties = Properties()
    val secretsFile = File("secret.properties")
    if (secretsFile.exists()) {
        secretsFile.inputStream().use { inputStream ->
            properties.load(inputStream)
        }
    }
    return properties.getProperty("API_TOKEN", providers.environmentVariable("API_TOKEN").getOrElse(""))
}

android {
    namespace = "fi.orkas.rvtest"
    compileSdk = 35

    defaultConfig {
        applicationId = "fi.orkas.rvtest"
        minSdk = 22
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "API_TOKEN", "\"${getApiToken()}\"")

        testInstrumentationRunner = "fi.orkas.rvtest.HiltTestRunner"
    }

    buildTypes {
        release {
            isProfileable = true
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_23
        targetCompatibility = JavaVersion.VERSION_23
    }
    kotlinOptions {
        jvmTarget = "23"
    }
    sourceSets.getByName("test") {
        setRoot("src/sharedTest")
    }
    sourceSets.getByName("androidTest") {
        setRoot("src/sharedTest")
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        unitTests.all {
            it.systemProperty("robolectric.logging", "stdout")
            it.systemProperty("robolectric.logging.enabled", "true")
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    baselineProfile {
        dexLayoutOptimization = true
    }
    buildToolsVersion = "36.0.0"
}

dependencies {
    coreLibraryDesugaring(libs.android.desugar)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.profileinstaller)

    implementation(libs.android.material)

    implementation(libs.androidx.paging.runtime)

    implementation(libs.androidx.constraintlayout)

    implementation(libs.glide)
    implementation(libs.glide.recyclerview) {
        isTransitive = true
    }

    implementation(libs.kotlinx.serialization.json)

    debugImplementation(libs.kotlinx.coroutines.test)

    implementation(libs.hilt.android)
    testImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.hilt.android.testing)

    testImplementation(libs.logback.classic)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.encoding)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.resources)
    implementation(libs.ktor.serialization.kotlinx.json)
    debugImplementation(libs.ktor.client.mock)

    ksp(libs.hilt.compiler)
    ksp(libs.glide.ksp)

    debugImplementation(libs.androidx.fragment.testing.manifest)
    androidTestImplementation(libs.androidx.fragment.testing)

    testImplementation(libs.junit)
    testImplementation(libs.androidx.junit.ktx)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.androidx.test.rules)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.espresso.core)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    debugImplementation(libs.androidx.test.monitor)
    androidTestUtil(libs.androidx.test.services)
    androidTestImplementation(libs.androidx.espresso.core)

    "baselineProfile"(project(":benchmark"))
}
