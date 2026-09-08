plugins {
    `kotlin-dsl`
}

group = "com.mudassar.buildlogic"

java {
    val jvm = libs.versions.jvmTarget.get()
    sourceCompatibility = JavaVersion.toVersion(jvm)
    targetCompatibility = JavaVersion.toVersion(jvm)
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.jvmTarget.get())
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.hilt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "android-app-convention"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "android-library-convention"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "hilt-convention"
            implementationClass = "HiltConventionPlugin"
        }
        register("androidFeature") {
            id = "android-feature-convention"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("kotlinJvm") {
            id = "kotlin-jvm-convention"
            implementationClass = "KotlinJvmConventionPlugin"
        }
    }
}
