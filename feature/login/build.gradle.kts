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
    implementation(projects.core.screen)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
}
