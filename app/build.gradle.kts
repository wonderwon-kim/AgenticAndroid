plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.agenticandroid.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.agenticandroid.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 20000
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions += "nightly"
    productFlavors {
        create("focus") {
            dimension = "nightly"
            applicationIdSuffix = ".focus"
            versionNameSuffix = "-focus"
            buildConfigField("String", "NIGHTLY_CASE", "\"FOCUS\"")
        }
        create("planner") {
            dimension = "nightly"
            applicationIdSuffix = ".planner"
            versionNameSuffix = "-planner"
            buildConfigField("String", "NIGHTLY_CASE", "\"PLANNER\"")
        }
        create("accessibility") {
            dimension = "nightly"
            applicationIdSuffix = ".accessibility"
            versionNameSuffix = "-accessibility"
            buildConfigField("String", "NIGHTLY_CASE", "\"ACCESSIBILITY\"")
        }
        create("recovery") {
            dimension = "nightly"
            applicationIdSuffix = ".recovery"
            versionNameSuffix = "-recovery"
            buildConfigField("String", "NIGHTLY_CASE", "\"RECOVERY\"")
        }
        create("turbo") {
            dimension = "nightly"
            applicationIdSuffix = ".turbo"
            versionNameSuffix = "-turbo"
            buildConfigField("String", "NIGHTLY_CASE", "\"TURBO\"")
        }
        create("experimental") {
            dimension = "nightly"
            applicationIdSuffix = ".experimental"
            versionNameSuffix = "-experimental"
            buildConfigField("String", "NIGHTLY_CASE", "\"EXPERIMENTAL\"")
        }
        create("orbit") {
            dimension = "nightly"
            applicationIdSuffix = ".orbit"
            versionNameSuffix = "-orbit"
            buildConfigField("String", "NIGHTLY_CASE", "\"ORBIT\"")
        }
        create("atlas") {
            dimension = "nightly"
            applicationIdSuffix = ".atlas"
            versionNameSuffix = "-atlas"
            buildConfigField("String", "NIGHTLY_CASE", "\"ATLAS\"")
        }
        create("sentinel") {
            dimension = "nightly"
            applicationIdSuffix = ".sentinel"
            versionNameSuffix = "-sentinel"
            buildConfigField("String", "NIGHTLY_CASE", "\"SENTINEL\"")
        }
        create("lucid") {
            dimension = "nightly"
            applicationIdSuffix = ".lucid"
            versionNameSuffix = "-lucid"
            buildConfigField("String", "NIGHTLY_CASE", "\"LUCID\"")
        }
        create("forge") {
            dimension = "nightly"
            applicationIdSuffix = ".forge"
            versionNameSuffix = "-forge"
            buildConfigField("String", "NIGHTLY_CASE", "\"FORGE\"")
        }
        create("synthesis") {
            dimension = "nightly"
            applicationIdSuffix = ".synthesis"
            versionNameSuffix = "-synthesis"
            buildConfigField("String", "NIGHTLY_CASE", "\"SYNTHESIS\"")
        }
        create("ultimate") {
            dimension = "nightly"
            applicationIdSuffix = ".ultimate"
            versionNameSuffix = "-ultimate"
            buildConfigField("String", "NIGHTLY_CASE", "\"ULTIMATE\"")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

tasks.whenTaskAdded {
    if (name.startsWith("lint")) {
        enabled = false
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
