enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("buildlogic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MoodFlow"
include(":app")
include(":core")
include(":core:navigation")
include(":core:designsystem")
include(":core:data")
include(":core:model")
include(":core:domain")
include(":core:utils")
include(":core:viewmodel")
include(":core:screen")
include(":core:database")
include(":feature")
include(":feature:login")
include(":feature:home")
include(":feature:register")
include(":authentication")
