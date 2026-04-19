import com.android.build.api.dsl.ApplicationExtension
import com.jero.convention.configureKotlinAndroid
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = 37
                defaultConfig {
                    minSdk = 30
                    targetSdk = 35
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                lint {
                    abortOnError = false
                }
            }

            extensions.getByType<KotlinAndroidProjectExtension>().apply {
                configureKotlinAndroid(this)
            }
        }
    }
}
