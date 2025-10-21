plugins {
    id("jero.moodflow.android.library")
    id("jero.moodflow.android.library.compose")
}

android {
    namespace = "com.jero.core.designsystem"
}

dependencies {
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.animation)
    api(libs.androidx.material3.android)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    implementation(projects.core.model)
    implementation(libs.androidx.material.icons.extended)
}
