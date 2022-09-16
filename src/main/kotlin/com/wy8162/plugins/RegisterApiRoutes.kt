package com.wy8162.plugins // ktlint-disable filename

import com.wy8162.controller.UserController
import com.wy8162.model.ApiContext
import com.wy8162.rbac.authorize
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

fun Application.registerApiV1Routes() {
    routing {
        apiV1Route()
    }
}

@OptIn(ExperimentalTime::class)
private fun Route.apiV1Route() {
    val userController: UserController by inject()

    route("/api/v1/users") {
        post("") {
            val ctx = ApiContext(call = call)

            val time = measureTime {
                userController.processUserRegistration(ctx)
            }

            call.respond(ctx.httpStatus, ctx.apiResponse)
            call.application.log.info("${call.request.uri} ($time)")
        }
        get("/{userId}") {
            val ctx = ApiContext(call = call)

            val time = measureTime {
                userController.getUser(ctx)
            }

            call.respond(ctx.httpStatus, ctx.apiResponse)
            call.application.log.info("${call.request.uri} ($time)")
        }
        post("/login") {
            val ctx = ApiContext(call = call)

            val time = measureTime {
                userController.processLogin(ctx)
            }

            call.respond(ctx.httpStatus, ctx.apiResponse)
            call.application.log.info("${call.request.uri} ($time)")
        }

        authorize("rbac", "system", "agent") {
            get("/hello") {
//                val principal = call.principal<JWTPrincipal>()
//                val username = principal!!.payload.getClaim("username").asString()
//                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
//                call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
                call.respondText("Hello")
            }
        }
    }
}
