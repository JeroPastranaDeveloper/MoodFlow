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
    implementation(projects.core.localdatabase)
    implementation(projects.core.network)
    implementation(projects.core.designsystem)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.work.manager)
    implementation(libs.koin.androidx.workmanager)
}