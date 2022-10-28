package com.wy8162.plugins

import com.wy8162.config.jacksonFeatureConfigurations
import com.wy8162.utils.UniqueIdGenerator
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.path
import org.slf4j.event.Level

fun Application.registerApiModule() {
    install(RequestFilterPlugin)

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        callIdMdc("call-id")
    }

    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate {
            "ID-${UniqueIdGenerator.nextUniqueId(20)}"
        }
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }

    install(ContentNegotiation) {
        jackson {
            jacksonFeatureConfigurations()
        }
    }

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader("x-debug-options")
        anyHost()
    }
}

fun Application.configureShutdownHook() {
    environment.monitor.subscribe(ApplicationStopping) {
        log.info("Shutting down the application...")
        // httpClient.close()
    }
}
