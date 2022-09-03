package com.wy8162.plugins

import com.wy8162.config.getLogger
import com.wy8162.error.EndpointNotFoundException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.registerErrorHandlingModule() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            getLogger().error(cause.stackTraceToString())
            when (cause) {
                is EndpointNotFoundException -> call.respond(HttpStatusCode.NotFound)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
