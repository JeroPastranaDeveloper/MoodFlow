package com.jero.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose() {
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        // enableStrongSkippingMode = true
        // reportsDestination = layout.buildDirectory.dir("compose_compiler")
    }
}
