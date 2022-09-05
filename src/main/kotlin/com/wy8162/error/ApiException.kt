package com.wy8162.error

open class ApiException(message: String) : Exception(message)

class EndpointNotFoundException : ApiException("Endpoint not found")
class InvalidUserIdException : ApiException("User ID is invalid")
class UnauthorizedAccessException : ApiException("Unauthorized access")
class JwtTokenExpiredException : ApiException("Token expired")
class ApiRequestValidationException(
    val violations: MutableList<String>
) : ApiException("Invalid request")
