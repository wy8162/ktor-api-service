package com.wy8162.plugins // ktlint-disable filename

import com.wy8162.controller.UserController
import com.wy8162.model.ApiContext
import com.wy8162.rbac.RbacRole
import com.wy8162.rbac.authorize
import com.wy8162.service.HelloMessage
import com.wy8162.service.HelloService
import com.wy8162.service.HrService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.slf4j.MDC
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
    val helloService: HelloService by inject()
    val httpClient: HttpClient by inject()
    val hrService: HrService by inject()

    route("/api/v1/users") {
        post("") {
            val ctx = ApiContext {
                call = this@post.call
                set("extraData", "SomeOtherData")
            }

            val time = measureTime {
                userController.processUserRegistration(ctx)
            }

            call.respond(ctx.httpStatus, ctx.apiResponse.response)
            call.application.log.info("${call.request.uri} ($time)")
        }
        get("/{userId}") {
            val ctx = ApiContext()
            ctx.call = call

            MDC.put("status", "beginning")
            val time = measureTime {
                userController.getUser(ctx)
            }

            ctx.apiResponse["status"] = "OK"
            call.respond(ctx.httpStatus, ctx.apiResponse.response)
            call.application.log.info("${call.request.uri} ($time)")
        }
        post("/login") {
            val ctx = ApiContext()
            ctx += "call" to call

            val time = measureTime {
                userController.processLogin(ctx)
            }

            call.respond(ctx.httpStatus, ctx.apiResponse.response)
            call.application.log.info("${call.request.uri} ($time)")
        }

        get("/testkoin") {
            val h1 = hrService.getEmployee(101)
            val h2 = hrService.getEmployee(102)
            val h3 = hrService.getEmployee(103)

            call.application.log.info("$h1")
            call.application.log.info("$h2")
            call.application.log.info("$h3")

            call.respond(h1)
        }

        authorize("rbac", RbacRole("system", "admin"), RbacRole("agent", "identity")) {
            get("/hello/{name}") {
                val message = helloService.sayHi(call.parameters["name"]!!)
                call.respond(status = HttpStatusCode.OK, message)
            }
            get("/remotehello/{name}") {
                val message = helloService.sayHi(call.parameters["name"]!!)
                val response = httpClient.request("http://localhost:8081/api/v1/users/hello/101") {
                    header("role", "system")
                    header("requester", "admin")
                }

                val msg = response.body<HelloMessage>()

                call.respond(status = HttpStatusCode.OK, mapOf("local" to message, "remote" to msg))
            }
        }
    }
}
