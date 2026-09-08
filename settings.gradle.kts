@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
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

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "offline-notes-app"
include(":app")
include(":domain")
include(":feature:base")
include(":feature:notes:presentation")
include(":feature:notes:data")
include(":core:base")
include(":core:common")
include(":core:navigation")
include(":core:design-system")
include(":offline-sync")
include(":feature:auth:data")
include(":feature:auth:presentation")
include(":core:storage")
