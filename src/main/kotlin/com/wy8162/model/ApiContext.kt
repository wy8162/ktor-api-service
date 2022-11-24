package com.wy8162.model

import com.wy8162.model.response.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall

enum class ApiStatus {
    SUCCESSFUL,
    FAILED
}

@Suppress("UNCHECKED_CAST")
data class ApiContext(val properties: MutableMap<String, Any?> = mutableMapOf()) {
    var call: ApplicationCall by properties
    var status: ApiStatus by properties
    var httpStatus: HttpStatusCode by properties
    var apiResponse: ApiResponse by properties

    operator fun <T> get(name: String): T = properties[name] as T
    operator fun set(name: String, any: Any?) {
        properties[name] = any
    }

    operator fun plusAssign(pair: Pair<String, Any?>) {
        properties[pair.first] = pair.second
    }

    init {
        initializeApiContext()
    }

    companion object {
        fun configure(block: ApiContext.() -> Unit): ApiContext {
            val ctx = ApiContext()

            ctx.apply {
                initializeApiContext()
            }

            ctx.apply(block)
            return ctx
        }
    }

    private fun initializeApiContext() {
        val r = ApiResponse()
        status = ApiStatus.SUCCESSFUL
        httpStatus = HttpStatusCode.OK
        apiResponse = r
    }
}
