package com.wy8162 // ktlint-disable filename

import com.wy8162.config.AppConfig
import com.wy8162.config.initializeDatabase
import com.wy8162.plugins.registerApiModule
import com.wy8162.plugins.registerApiV1Routes
import com.wy8162.plugins.registerErrorHandlingModule
import com.wy8162.plugins.registerHrApiV1Routes
import com.wy8162.plugins.registerKoinModules
import com.wy8162.plugins.registerMonitoringModule
import com.wy8162.plugins.registerSecurityModule
import com.wy8162.plugins.registerSwaggerRoutes
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val env = applicationEngineEnvironment {
        log.info("Application is starting in environment: ${AppConfig.applicationEnvironment()}")

        @Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
        module() {
            registerKoinModules()
            registerApiModule()
            registerMonitoringModule()
            registerApiV1Routes()
            registerHrApiV1Routes()
            registerSwaggerRoutes()
            registerErrorHandlingModule()
            initializeDatabase()
            registerSecurityModule()
        }

        connector {
            port = AppConfig.appServerPort()
        }
        connector {
            port = AppConfig.appMetricServerPort()
        }
    }

    embeddedServer(Netty, environment = env).start(wait = true)
}
