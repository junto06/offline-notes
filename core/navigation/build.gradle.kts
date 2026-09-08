plugins {
    alias(libs.plugins.convention.androidLibrary)
}

android.namespace = "com.mudassar.notes.navigation"

dependencies {
    implementation(libs.androidx.navigation.ui.ktx)
}