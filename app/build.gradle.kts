plugins {
    alias(libs.plugins.convention.androidApp)
    alias(libs.plugins.convention.hilt)
}

apply(from = "../version.gradle")

android {
    namespace = "com.mudassar.notes"

    defaultConfig {
        applicationId = "com.mudassar.notes"
        versionCode = project.extra["versionCode"] as Int
        versionName = project.extra["versionName"] as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
}

dependencies {
    implementation(projects.feature.auth.presentation)
    implementation(projects.feature.auth.data)
    implementation(projects.feature.notes.presentation)
    implementation(projects.feature.notes.data)
    implementation(projects.offlineSync)
    implementation(projects.domain)

    implementation(projects.core.common)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.work)
    implementation(libs.androidx.hilt.work)

    implementation(libs.timber)
}
