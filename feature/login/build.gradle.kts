plugins {
    id("jero.moodflow.android.feature")
    id("jero.moodflow.android.koin")
}

android {
    namespace = "com.jero.feature.login"
}

dependencies {
    implementation(libs.accompanist.systemuicontroller)

    implementation(projects.core.utils)
    implementation(projects.core.domain)
    implementation(projects.core.viewmodel)
    implementation(libs.androidx.material.icons.extended)
}
