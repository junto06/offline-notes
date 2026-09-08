plugins {
    alias(libs.plugins.convention.androidFeature)
    alias(libs.plugins.navigation.safe.args.plugin)
}

android.namespace = "com.mudassar.notes.presentation"

dependencies {
    implementation(projects.feature.base)
    implementation(projects.domain)

    implementation(projects.core.designSystem)
    implementation(projects.core.navigation)
}
