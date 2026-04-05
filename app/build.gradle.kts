plugins {
    id("jero.moodflow.android.application")
    id("jero.moodflow.android.application.compose")
    id("jero.moodflow.android.koin")
    id("com.google.gms.google-services")
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

android {
    namespace = "com.jero.moodflow"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jero.moodflow"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xno-param-assertions",
                "-Xno-call-assertions",
                "-Xno-receiver-assertions"
            )
        }
    }
}

dependencies {
    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3.android)
    implementation(libs.accompanist.systemuicontroller)

    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.koin.androidx.workmanager)

    // Core
    implementation(projects.core.navigation)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.localdatabase)
    implementation(projects.core.viewmodel)
    implementation(projects.core.domain)
    implementation(projects.core.network)

    // Feature
    implementation(projects.feature.login)
    implementation(projects.feature.register)
    implementation(projects.feature.home)
    implementation(projects.feature.editnote)
    implementation(projects.feature.settings)
    implementation(projects.feature.trash)

    // Auth
    implementation(projects.authentication)

    // Database
    implementation(projects.core.database)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.material3.adaptive.navigation3)

    // Widget
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3) {
        exclude(group = "androidx.compose.material3", module = "material3")
    }
}