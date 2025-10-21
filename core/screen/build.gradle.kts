plugins {
    id("jero.moodflow.android.library")
    id("jero.moodflow.android.feature")
}

android {
    namespace = "com.jero.core.screen"
}

dependencies {
    implementation(libs.accompanist.systemuicontroller)
}
