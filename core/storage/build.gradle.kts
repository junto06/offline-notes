plugins {
    alias(libs.plugins.convention.androidLibrary)
}

android.namespace = "com.mudassar.notes.storage"

dependencies {
    api(libs.androidx.datastore.core)
}
