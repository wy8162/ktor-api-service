package com.wy8162.plugins

import com.wy8162.config.AppConfig
import com.wy8162.error.EndpointNotFoundException
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.application.pluginOrNull
import io.ktor.server.plugins.origin
import io.ktor.server.webjars.Webjars

private val apiEndpoints: Map<Int, List<Regex>> = mapOf(
    AppConfig.appServerPort() to (
        AppConfig.CFG().getStringList("ktor.app.serviceEndpoints")
            .map { it.toRegex() }.toList()
        ),
    AppConfig.appMetricServerPort() to (
        AppConfig.CFG().getStringList("ktor.app.metricsEndpoints")
            .map { it.toRegex() }
        )
)

val RequestFilterPlugin = createApplicationPlugin(name = "RequestFilterPlugin") {
    onCall { call ->
        call.request.origin.apply {
            if (apiEndpoints.getOrDefault(port, listOf()).none { it.matches(uri) }) {
                throw EndpointNotFoundException()
            }
        }
    }
    onCallRespond { call ->
        transformBody { data ->
            if (call.request.origin.uri == "/webjars/swagger-ui/swagger-initializer.js") {
                // Swap the response from Webjar, which is the default petstore API spec.
                getSwaggerInitializer(AppConfig.CFG().getString("swagger.apiSpec"))
            } else {
                data
            }
        }
    }
    on(MonitoringEvent(ApplicationStarted)) { application ->
        if (application.pluginOrNull(Webjars) == null) {
            application.log.info("Registering Webjar.")
            application.install(Webjars)
        }
    }
}

/**
 * Copied from webjar swagger-ui. Replace the URL so that it points to the
 * specified OpenAPI JSON specification file.
 */
private suspend fun getSwaggerInitializer(apiSpecName: String): String {
    return """
        window.onload = function() {
          //<editor-fold desc="Changeable Configuration Block">

          // the following lines will be replaced by docker/configurator, when it runs in a docker-container
          window.ui = SwaggerUIBundle({
            url: "/swagger-ui/$apiSpecName.json",
            dom_id: '#swagger-ui',
            deepLinking: true,
            presets: [
              SwaggerUIBundle.presets.apis,
              SwaggerUIStandalonePreset
            ],
            plugins: [
              SwaggerUIBundle.plugins.DownloadUrl
            ],
            layout: "StandaloneLayout"
          });

          //</editor-fold>
        };

    """.trimIndent()
}
