plugins {
    alias(libs.plugins.convention.androidLibrary)
    alias(libs.plugins.convention.hilt)
}

android.namespace = "com.mudassar.notes.common"

dependencies {
    implementation(projects.core.base)
    api(projects.core.navigation)
    implementation(projects.core.storage)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.timber)
}