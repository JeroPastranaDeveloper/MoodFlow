plugins {
    alias(libs.plugins.kotlinx.serialization)
    id("jero.moodflow.android.library")
    id("jero.moodflow.android.library.compose")
    id("jero.moodflow.android.koin")
}

android {
    namespace = "com.jero.core.navigation"
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.kotlinx.coroutines.android)

    // json parsing
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.model)
    implementation(libs.gson)

    // Navigation 3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    // Material 3 Adaptive
    implementation(libs.androidx.compose.material3.adaptive)
}