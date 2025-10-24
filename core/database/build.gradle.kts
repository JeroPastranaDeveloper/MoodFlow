plugins {
    id("jero.moodflow.android.library")
    id("jero.moodflow.android.koin")
}

android {
    namespace = "com.jero.core.database"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
}