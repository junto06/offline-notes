plugins {
    alias(libs.plugins.convention.androidLibrary)
    alias(libs.plugins.convention.hilt)
}

android.namespace = "com.mudassar.notes.sync"

dependencies {
    implementation(projects.domain)

    implementation(libs.androidx.work)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
