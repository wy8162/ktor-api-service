package com.wy8162.plugins // ktlint-disable filename

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.withCharset
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
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
        swaggerRoute()
    }
}

const val swaggerRoot = "swagger-ui"
const val swaggerUiVersion = "4.14.0" // See the dependency of org.webjars:swagger-ui

private fun Route.swaggerRoute() {
    route("/$swaggerRoot") {
        get("") {
            call.respondRedirect("$swaggerRoot/index.html")
        }
        get("/{fileName}") {
            when (val file = call.parameters["fileName"]!!) {
                "swagger-initializer.js" -> {
                    val js = this.javaClass.getResource("/static/swagger-initializer.js").readText()
                    call.respondText(js, ContentType.Application.JavaScript, HttpStatusCode.OK)
                }

                "swagger.json" -> {
                    val json = this.javaClass.getResource("/static/swagger.json").readText()
                    call.respondText(json, ContentType.Application.Json, HttpStatusCode.OK)
                }

                else -> serveStaticResource(file, call)
            }
        }
    }
}

private suspend fun serveStaticResource(filename: String, call: ApplicationCall) {
    val resource =
        object {}.javaClass.getResource("/META-INF/resources/webjars/swagger-ui/$swaggerUiVersion/$filename")
    if (resource == null) {
        call.respond(HttpStatusCode.NotFound, "$filename could not be found")
    } else {
        call.respond(ResourceContent(resource))
    }
}

private class ResourceContent(val resource: URL) : OutgoingContent.ByteArrayContent() {
    private val bytes by lazy { resource.readBytes() }

    override val contentType: ContentType? by lazy {
        val extension = resource.file.substring(resource.file.lastIndexOf('.') + 1)
        contentTypes[extension] ?: ContentType.Text.Html
    }

    override val contentLength: Long? by lazy {
        bytes.size.toLong()
    }

    override fun bytes(): ByteArray = bytes

    override fun toString() = "ResourceContent \"$resource\""
}

private val contentTypes = mapOf(
    "html" to ContentType.Text.Html,
    "css" to ContentType.Text.CSS,
    "js" to ContentType.Application.JavaScript,
    "json" to ContentType.Application.Json.withCharset(Charsets.UTF_8),
    "png" to ContentType.Image.PNG
)
