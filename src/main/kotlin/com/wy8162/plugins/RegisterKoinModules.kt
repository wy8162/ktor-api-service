package com.wy8162.plugins

import com.wy8162.config.httpClientInstance
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

fun Application.registerKoinModules() {
    install(Koin) {
        SLF4JLogger()
        modules(koinModule)
    }
}

val koinModule = module {
    single { PrometheusMeterRegistry(PrometheusConfig.DEFAULT) }
    single { httpClientInstance() }
}
