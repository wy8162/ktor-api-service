package com.wy8162.model.request

import jakarta.validation.constraints.NotNull

data class UserRequest(
    @NotNull(message = "Email cannot be null")
    val email: String,
    val userName: String?,
    val password: String,
    val phone: String?,
    val cell: String?
)
