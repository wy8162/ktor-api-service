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
import io.ktor.client.HttpClient
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.time.Duration

fun appEnv() = applicationEngineEnvironment {
    val javaAgent = getAgentArgument()
    log.info("Application is starting in environment: ${AppConfig.applicationEnvironment()}")
    log.info("Java agent : $javaAgent")

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

    // Don't do this for production.
    developmentMode = true

    connector {
        port = AppConfig.appServerPort()
    }
    connector {
        port = AppConfig.appMetricServerPort()
    }
}

fun main() {
    val preWait: Duration = Duration.ofSeconds(2)
    val graceShutdown: Duration = Duration.ofSeconds(2)
    val timeout: Duration = Duration.ofSeconds(5)

    val httpClient: HttpClient by lazy { GlobalContext.get().get() }

    val engine = embeddedServer(Netty, environment = appEnv())
    engine.addShutdownHook {
        gracefulShutdownHook(engine, httpClient, preWait, graceShutdown, timeout)
    }
    engine.start(wait = true)
}

private fun getAgentArgument(): String? {
    val runtimeMxBean: RuntimeMXBean = ManagementFactory.getRuntimeMXBean()
    for (arg in runtimeMxBean.getInputArguments()) {
        if (arg.startsWith("-javaagent")) {
            return arg
        }
    }
    return "Agent jar not found"
}

private fun gracefulShutdownHook(
    engine: NettyApplicationEngine,
    httpClient: HttpClient,
    preWait: Duration,
    graceShutdown: Duration,
    timeout: Duration
) {
    engine.environment.log.info("Shutting down in progress...")

    runBlocking {
        delay(preWait.toMillis())
        engine.stop(graceShutdown.toMillis(), timeout.toMillis())
    }
    httpClient.close()
}
