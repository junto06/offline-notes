plugins {
    alias(libs.plugins.convention.androidFeature)
}

android.namespace = "com.mudassar.notes.feature.base"

dependencies {
    implementation(projects.core.designSystem)
}