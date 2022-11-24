package com.wy8162.model.response

import com.wy8162.error.ApiError

@Suppress("UNCHECKED_CAST")
class ApiResponse(vararg fields: String) {
    private val fieldSet = fields.toHashSet()
    val response: MutableMap<String, Any?> = mutableMapOf()

    operator fun invoke(block: ApiResponse.() -> Unit): ApiResponse {
        this.apply(block)
        return this
    }

    operator fun <T> get(name: String): T = response[name] as T
    operator fun set(name: String, any: Any?) {
        if (fieldSet.isEmpty()) {
            response[name] = any
            return
        }
        if (fieldSet.contains(name)) {
            response[name] = any
        }
    }

    operator fun plusAssign(pair: Pair<String, Any?>) {
        this[pair.first] = pair.second
    }

    fun assign(vararg values: Pair<String, Any?>): ApiResponse {
        for ((k, v) in values) {
            this[k] = v
        }
        return this
    }

    fun addError(error: ApiError): ApiResponse {
        if (response["errors"] == null) {
            response["errors"] = mutableListOf<ApiError>()
        }
        val r = response["errors"] as MutableList<ApiError>
        r.add(error)
        return this
    }
}

object HelloResponse {
    fun newInstance(): ApiResponse = ApiResponse(
        "local",
        "remote",
        "message",
        "hello"
    )
}
