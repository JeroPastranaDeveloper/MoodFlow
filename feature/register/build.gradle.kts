plugins {
    id("jero.moodflow.android.feature")
    id("jero.moodflow.android.koin")
}

android {
    namespace = "com.jero.feature.register"
}

dependencies {
    implementation(libs.accompanist.systemuicontroller)

    /*implementation(projects.utils)
    implementation(projects.core.domain)
    implementation(projects.core.viewmodel)

    implementation(projects.feature.details)*/
}
