plugins {
    alias(libs.plugins.convention.androidLibrary)
    alias(libs.plugins.convention.hilt)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.kotlin.serialization)
}

android.namespace = "com.mudassar.notes.data"

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(projects.domain)
    implementation(projects.core.base)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.serialization.json)
}
