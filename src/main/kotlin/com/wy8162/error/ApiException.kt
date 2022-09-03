package com.wy8162.error

open class ApiException(message: String) : Exception(message)

class EndpointNotFoundException : ApiException("Endpoint not found")
