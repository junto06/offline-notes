import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("android-library-convention")
            pluginManager.apply("hilt-convention")

            dependencies {
                add("implementation", libs.findLibrary("androidx-fragment-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-ktx").get())
                add("implementation", libs.findLibrary("androidx-navigation-fragment-ktx").get())
                add("implementation", libs.findLibrary("androidx-navigation-ui-ktx").get())
            }
        }
    }
}
