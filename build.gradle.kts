val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val prometeus_version: String by project
val kotlin_logging_version: String by project
val koin_version: String by project
val arrow_version: String by project
val jackson_version: String by project
val exposed_version: String by project
val postgres_version: String by project
val hikaricp_version: String by project
val flyway_version: String by project
val kotlin_coroutine_version: String by project
val junit_version: String by project
val assertj_version: String by project
val mockk_version: String by project

plugins {
    jacoco
    application
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.kotlin.plugin.serialization") apply true
    id("org.owasp.dependencycheck") apply true
    id("org.jlleitschuh.gradle.ktlint") apply true

    id("io.ktor.plugin") version "2.1.0"
}

group = "com.wy8162"
version = "0.0.1"
application {
    mainClass.set("com.wy8162.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

dependencies {
    // Core
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktor_version")

    // HTTP Client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("io.ktor:ktor-client-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")

    // Authentications
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")

    // Koin
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")

    // Arrow
    implementation("io.arrow-kt:arrow-core:$arrow_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlin_logging_version")

    // Metrics
    implementation("io.ktor:ktor-server-metrics-micrometer-jvm:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeus_version")

    // Commons
    implementation("org.apache.commons:commons-text:1.9")

    // Validations
    implementation("org.hibernate.validator:hibernate-validator:7.0.5.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-core:$jackson_version")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jackson_version")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
    implementation("com.fasterxml.jackson.module:jackson-modules-java8:$jackson_version")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:$jackson_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jackson_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")

    // Database
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.flywaydb:flyway-core:$flyway_version")

    // Webjar - no need for Webjar to just serve the Swagger JSON file.
    implementation("io.ktor:ktor-server-webjars:2.1.0")

    // Swagger
    implementation("org.webjars:swagger-ui:4.14.0")
    // To make webjar resources version agnostic
    implementation("org.webjars:webjars-locator-core:0.52")
    // implementation("io.swagger.parser.v3:swagger-parser:2.1.2")

    // Open Telemetry
    implementation(platform("io.opentelemetry:opentelemetry-bom:1.18.0"))
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-sdk")
    implementation("io.opentelemetry:opentelemetry-extension-kotlin")

    // Fake data
    implementation("com.github.javafaker:javafaker:1.0.2") {
        exclude(group = "org.yaml")
    }
    implementation("org.yaml:snakeyaml:1.30")

    // Test
    implementation("org.testcontainers:postgresql:1.17.6")

    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("io.insert-koin:koin-test:$koin_version")
    testImplementation("io.insert-koin:koin-test-junit5:$koin_version")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
    testImplementation("org.assertj:assertj-core:$assertj_version")
    testImplementation("io.mockk:mockk:$mockk_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            apiVersion = "1.7"
            languageVersion = "1.7"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
    withType<Test> {
        useJUnitPlatform {}
        testLogging.apply {
            events("started", "passed", "skipped", "failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showCauses = true
            showExceptions = true
            showStackTraces = true
            showStandardStreams = true
            info.events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
            )
        }
    }
}

tasks.test {
    // Needed to run TestContainer for Postgresql
    environment("TESTCONTAINERS_RYUK_DISABLED", "true")
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
}

dependencyCheck {
    failOnError = false
}
