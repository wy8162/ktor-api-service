package com.wy8162.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.koin.ktor.ext.inject

fun Application.registerMonitoringModule() {
    val metricsRegistry: PrometheusMeterRegistry by inject()
    install(MicrometerMetrics) {
        metricName = "http.server.requests"
        registry = metricsRegistry
    }

    routing {
        get("/metrics") {
            call.respond(metricsRegistry.scrape())
        }
        get("/api/v1/ping") {
            call.respond("OK")
        }
        get("/health") {
            call.respond("OK")
        }
    }
}
