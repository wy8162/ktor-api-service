package com.wy8162.error

enum class ErrorCode {
    ERR_DB_FAILURE,
    ERR_USER_EXISTS,
    ERR_API_FAILURE,
    ERR_VALIDATION_ERROR,
    ERR_INVALID_REQUEST
}

data class ApiError(
    val errorCode: ErrorCode,
    val errorMessage: String? = null,
    val errors: MutableList<String> = mutableListOf()
) {
    fun addErrors(errors: List<String>) {
        this.errors.addAll(errors)
    }
}
