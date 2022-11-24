package com.wy8162.model

import com.wy8162.model.response.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall

enum class ApiStatus {
    SUCCESSFUL,
    FAILED
}

@Suppress("UNCHECKED_CAST")
class ApiContext(block: ApiContext.() -> Unit) {
    private val _attributes: MutableMap<String, Any?> = mutableMapOf()

    var call: ApplicationCall by _attributes
    var status: ApiStatus by _attributes
    var httpStatus: HttpStatusCode by _attributes
    var apiResponse: ApiResponse by _attributes

    fun response(): Map<String, Any?> = apiResponse.response

    operator fun invoke(block: ApiContext.() -> Unit): ApiContext {
        this.apply(block)
        return this
    }

    fun assign(vararg values: Pair<String, Any?>): ApiContext {
        for ((k, v) in values) {
            _attributes[k] = v
        }
        return this
    }

    operator fun <T> get(name: String): T = _attributes[name] as T
    operator fun set(name: String, any: Any?) {
        _attributes[name] = any
    }

    operator fun plusAssign(pair: Pair<String, Any?>) {
        _attributes[pair.first] = pair.second
    }

    init {
        initializeApiContext()
    }

    private fun initializeApiContext() {
        val r = ApiResponse()
        status = ApiStatus.SUCCESSFUL
        httpStatus = HttpStatusCode.OK
        apiResponse = r
    }

    init {
        this.apply(block)
    }
}
