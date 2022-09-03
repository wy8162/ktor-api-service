package com.wy8162.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.registerApiV1Routes() {
    routing {
        apiV1Route()
    }
}

private fun Route.apiV1Route() {
    route("/api/v1") {
        get("/hello") {
            val startTime = System.currentTimeMillis()
            call.respond(HttpStatusCode.OK, "Hi")
            call.application.log.info("${call.request.uri} (${System.currentTimeMillis() - startTime}ms)")
        }
    }
}
