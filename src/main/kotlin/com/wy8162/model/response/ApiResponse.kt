package com.wy8162.model.response

import com.wy8162.error.ApiError

data class ApiResponse(
    var data: Any? = null,
    var errors: MutableList<ApiError> = mutableListOf()
)
