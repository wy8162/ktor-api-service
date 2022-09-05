package com.wy8162.plugins

import com.wy8162.config.httpClientInstance
import com.wy8162.controller.UserController
import com.wy8162.service.UserService
import com.wy8162.service.UserServiceImpl
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
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
    singleOf(::UserController)
    singleOf(::UserServiceImpl) bind UserService::class
    single { PrometheusMeterRegistry(PrometheusConfig.DEFAULT) }
    single { httpClientInstance() }
}
