package com.wy8162.utils // ktlint-disable filename

import com.wy8162.error.ApiRequestValidationException
import jakarta.validation.Validation

private val validator = Validation.buildDefaultValidatorFactory().validator

fun <T : Any> T.validate() {
    val violations = validator.validate(this)
        .map {
            "${it.invalidValue} : ${it.message}"
        }
        .toMutableList()

    if (violations.isNotEmpty()) {
        throw ApiRequestValidationException(violations)
    }
}
