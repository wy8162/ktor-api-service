package com.wy8162.plugins

import com.wy8162.config.getLogger
import com.wy8162.error.ApiError
import com.wy8162.error.ApiRequestValidationException
import com.wy8162.error.EndpointNotFoundException
import com.wy8162.error.ErrorCode
import com.wy8162.model.response.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

fun Application.registerErrorHandlingModule() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            getLogger().error(cause.stackTraceToString())
            when (cause) {
                is EndpointNotFoundException -> call.respond(HttpStatusCode.NotFound)
                is ApiRequestValidationException -> call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse().addError(
                        ApiError(
                            errorCode = ErrorCode.ERR_VALIDATION_ERROR,
                            errorMessage = cause.message
                        ).apply { addErrors(cause.violations) }
                    )
                )

                else -> call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse().addError(
                        ApiError(
                            errorCode = ErrorCode.ERR_INVALID_REQUEST,
                            errorMessage = cause.message
                        )
                    )
                )
            }
        }
    }
}
