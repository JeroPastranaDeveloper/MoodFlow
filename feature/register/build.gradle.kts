plugins {
    id("jero.moodflow.android.feature")
    id("jero.moodflow.android.koin")
}

android {
    namespace = "com.jero.feature.register"
}

dependencies {
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.material.icons.extended)
    implementation(projects.core.screen)
    implementation(projects.core.domain)
    implementation(projects.core.utils)
    implementation(projects.core.viewmodel)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
}
