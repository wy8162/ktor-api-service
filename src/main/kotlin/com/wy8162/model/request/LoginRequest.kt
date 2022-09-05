package com.wy8162.model.request

import jakarta.validation.constraints.NotNull

data class LoginRequest(
    @NotNull(message = "Username is needed")
    val username: String,
    @NotNull(message = "Password is needed")
    val password: String
)
