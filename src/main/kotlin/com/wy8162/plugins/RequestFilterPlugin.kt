package com.wy8162.plugins

import com.wy8162.config.AppConfig
import com.wy8162.error.EndpointNotFoundException
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.plugins.origin

private val apiEndpoints: Map<Int, List<String>> = mapOf(
    AppConfig.appServerPort() to AppConfig.CFG().getStringList("ktor.app.serviceEndpoints"),
    AppConfig.appMetricServerPort() to AppConfig.CFG().getStringList("ktor.app.metricsEndpoints")
)

val RequestFilterPlugin = createApplicationPlugin(name = "RequestFilterPlugin") {
    onCall { call ->
        call.request.origin.apply {
            if (apiEndpoints.getOrDefault(port, listOf()).none { uri.startsWith(it) }) {
                throw EndpointNotFoundException()
            }
        }
    }
}
