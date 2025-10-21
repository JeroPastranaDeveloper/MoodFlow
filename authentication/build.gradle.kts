plugins {
    id("jero.moodflow.android.feature")
    id("jero.moodflow.android.koin")
}

android {
    namespace = "com.jero.authentication"
}

dependencies {
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(projects.core.model)
    implementation(projects.core.domain)
}
