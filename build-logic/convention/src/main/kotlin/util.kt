import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.configureKotlinAndroid(extension: CommonExtension) {
    extension.apply {
        compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

        defaultConfig.apply {
            minSdk = libs.findVersion("minSdk").get().toString().toInt()
        }

        val jvmTargetValue = libs.findVersion("jvmTarget").get().toString()

        compileOptions.apply {
            sourceCompatibility = JavaVersion.toVersion(jvmTargetValue)
            targetCompatibility = JavaVersion.toVersion(jvmTargetValue)
        }

        tasks.withType(KotlinCompile::class.java).configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(jvmTargetValue))
                freeCompilerArgs.addAll(
                    compilerArgs
                )
            }
        }
    }
}

fun Project.configureKotlinJvm() {
    val jvmTargetValue = libs.findVersion("jvmTarget").get().toString()

    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.toVersion(jvmTargetValue)
        targetCompatibility = JavaVersion.toVersion(jvmTargetValue)
    }

    tasks.withType(KotlinCompile::class.java).configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(jvmTargetValue))
            freeCompilerArgs.addAll(
                compilerArgs
            )
        }
    }
}

fun Project.configureComposeAndroid(extension: CommonExtension) {
    apply(plugin = "org.jetbrains.kotlin.plugin.compose")
    extension.apply {
        buildFeatures.apply {
            compose = true
        }
    }

    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("implementation", libs.findLibrary("androidx-compose-ui").get())
        add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
        add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        add("implementation", libs.findLibrary("androidx-compose-material3").get())
        add("implementation", libs.findLibrary("androidx-compose-material-icons-core").get())
        add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
    }
}

private val compilerArgs = listOf(
    "-Xexplicit-backing-fields",
    "-XXLanguage:+PropertyParamAnnotationDefaultTargetMode",
    "-Xcontext-parameters",
)