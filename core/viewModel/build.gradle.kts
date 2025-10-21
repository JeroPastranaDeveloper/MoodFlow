plugins {
    id("jero.moodflow.android.library")
}

android {
    namespace = "com.jero.core.viewmodel"
}

dependencies {
    api(libs.androidx.lifecycle.viewModelCompose)
}
