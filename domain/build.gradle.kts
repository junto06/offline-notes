plugins {
    alias(libs.plugins.convention.kotlinJvm)
}

dependencies {
    implementation(projects.core.base)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
}
