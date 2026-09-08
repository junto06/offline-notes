plugins {
    alias(libs.plugins.convention.kotlinJvm)
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}

