package com.wy8162.model.response

import com.wy8162.error.ApiError

@Suppress("UNCHECKED_CAST")
data class ApiResponse(val response: MutableMap<String, Any?> = mutableMapOf()) {
    operator fun <T> get(name: String): T = response[name] as T
    operator fun set(name: String, any: Any?) {
        response[name] = any
    }

    operator fun plusAssign(pair: Pair<String, Any?>) {
        response[pair.first] = pair.second
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
