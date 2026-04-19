plugins {
    id("jero.moodflow.android.feature")
    id("jero.moodflow.android.koin")
}

android {
    namespace = "com.jero.feature.editnote"
}

dependencies {

    implementation(libs.accompanist.systemuicontroller)

    implementation(projects.core.viewmodel)
    implementation(projects.core.domain)
    implementation(projects.core.screen)
    implementation(projects.core.utils)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.compose.rich.editor)
}