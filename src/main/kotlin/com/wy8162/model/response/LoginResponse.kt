package com.wy8162.model.response

data class LoginResponse(
    val token: String,
    val refreshToken: String
)
