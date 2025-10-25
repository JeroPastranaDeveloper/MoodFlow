plugins {
    id("jero.moodflow.android.library")
    id("jero.moodflow.android.koin")
}

android {
    namespace = "com.jero.core.network"
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}