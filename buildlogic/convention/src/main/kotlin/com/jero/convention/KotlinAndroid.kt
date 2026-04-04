package com.jero.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal fun Project.configureKotlinAndroid(
    extension: KotlinAndroidProjectExtension,
) {
    extension.apply {
        compilerOptions {
            // Treat all Kotlin warnings as errors (disabled by default)
            allWarningsAsErrors = properties["warningsAsErrors"] as? Boolean ?: false

            freeCompilerArgs.add("-Xcontext-parameters")

            optIn.addAll(
                "kotlin.RequiresOptIn",
                // Enable experimental coroutines APIs, including Flow
                "kotlinx.coroutines.ExperimentalCoroutinesApi",
                // Enable experimental compose APIs
                "androidx.compose.material3.ExperimentalMaterial3Api",
                "androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
                "androidx.compose.animation.ExperimentalSharedTransitionApi",
            )

            jvmTarget = JvmTarget.JVM_17
        }
    }
}
