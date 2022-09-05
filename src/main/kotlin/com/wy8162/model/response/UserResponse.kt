package com.wy8162.model.response

import java.util.UUID

class UserResponse(
    val id: UUID,
    val email: String,
    val userName: String?,
    val password: String,
    val phone: String?,
    val cell: String?
)
