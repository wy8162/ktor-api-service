package com.wy8162.plugins // ktlint-disable filename

import com.wy8162.config.ROLE_USER
import com.wy8162.controller.HrController
import com.wy8162.error.UnauthorizedAccessException
import com.wy8162.model.ApiContext
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.auth.authenticate
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.AttributeKey
import org.koin.ktor.ext.inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun Application.registerHrApiV1Routes() {
    routing {
        apiV1Route()
    }
}

@OptIn(ExperimentalTime::class)
private fun Route.apiV1Route() {
    val hrController: HrController by inject()

    route("/api/v1/hr") {
        authenticate("auth-jwt") {
            get("/{employeeId}") {
                // Username and role are set in security module
                val role = call.attributes[AttributeKey("role")]

                if (role != ROLE_USER) {
                    throw UnauthorizedAccessException()
                }

                val ctx = ApiContext()
                ctx += "call" to call

                val time = measureTime {
                    hrController.getEmployee(ctx)
                }

                call.respond(ctx.httpStatus, ctx.apiResponse.response)
                call.application.log.info("${call.request.uri} ($time)")
            }
        }
    }
}
