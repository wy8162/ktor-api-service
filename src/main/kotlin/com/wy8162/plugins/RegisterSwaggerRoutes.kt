package com.wy8162.plugins // ktlint-disable filename

import com.wy8162.config.AppConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.net.URL

/**
 * Credit goes to https://github.com/SMILEY4/ktor-swagger-ui
 */

fun Application.registerSwaggerRoutes() {
    routing {
        apiSpecRoute()
    }
}

const val swaggerRoot = "swagger-ui"
private val apiSpecName = AppConfig.CFG().getString("swagger.apiSpec")

private fun Route.apiSpecRoute() {
    route("/$swaggerRoot") {
        get("") {
            // Points to webjar-ui resources
            call.respondRedirect("/webjars/swagger-ui/index.html")
        }
        get("/{file}") {
            when (val file = call.parameters["file"]!!) {
                "$apiSpecName.json" -> {
                    val json = this.javaClass.getResource("/apispec/$apiSpecName.json")
                    call.respond(ResourceContent(json))
                }

                else -> call.respond(HttpStatusCode.NotFound, "$file could not be found")
            }
        }
    }
}

private class ResourceContent(val resource: URL) : OutgoingContent.ByteArrayContent() {
    private val bytes by lazy { resource.readBytes() }

    override val contentType: ContentType? = ContentType.Application.Json

    override val contentLength: Long? by lazy {
        bytes.size.toLong()
    }

    override fun bytes(): ByteArray = bytes

    override fun toString() = "ResourceContent \"$resource\""
}
