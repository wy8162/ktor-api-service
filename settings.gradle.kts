rootProject.name = "ktor-api-service"

pluginManagement {
    val ktlint_version: String by settings
    val kotlin_version: String by settings
    val dependency_check_version: String by settings

    plugins {
        id("org.owasp.dependencycheck") version "$dependency_check_version"
        id("org.jlleitschuh.gradle.ktlint") version "$ktlint_version"
        id("org.jetbrains.kotlin.plugin.serialization") version "$kotlin_version"
    }

    repositories {
        gradlePluginPortal()
    }
}
