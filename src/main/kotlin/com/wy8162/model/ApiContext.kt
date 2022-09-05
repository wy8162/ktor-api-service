package com.wy8162.model

import com.wy8162.error.ApiError
import com.wy8162.model.response.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall

enum class ApiStatus {
    SUCCESSFUL,
    FAILED
}

data class ApiContext(
    val call: ApplicationCall,
    var status: ApiStatus = ApiStatus.SUCCESSFUL,
    var httpStatus: HttpStatusCode = HttpStatusCode.OK,
    val apiResponse: ApiResponse = ApiResponse()
) {
    fun addError(error: ApiError) = apiResponse.errors.add(error)
    fun addErrors(errors: MutableList<ApiError>) = apiResponse.errors.addAll(errors)
}
