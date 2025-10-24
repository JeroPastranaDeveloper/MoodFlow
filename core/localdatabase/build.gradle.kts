plugins {
    id("jero.moodflow.android.library")
    id("jero.moodflow.android.koin")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.jero.core.localdatabase"
    defaultConfig {
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(libs.firebase.database)

    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.compiler)
}