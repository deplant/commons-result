import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.from
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.mavenCentral
import org.gradle.kotlin.dsl.repositories

rootProject.name = "commons-result"

pluginManagement.repositories {
    mavenLocal()
    gradlePluginPortal()
}