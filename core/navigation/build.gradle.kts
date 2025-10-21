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

    api(libs.androidx.navigation.compose)

    // json parsing
    implementation(libs.kotlinx.serialization.json)

    implementation(projects.core.model)
    implementation(libs.gson)
}