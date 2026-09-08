plugins {
    alias(libs.plugins.convention.androidLibrary)
    alias(libs.plugins.convention.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android.namespace = "com.mudassar.notes.auth.data"

dependencies {
    implementation(projects.domain)
    implementation(projects.core.base)
    implementation(projects.core.storage)

    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.serialization.json)
}
