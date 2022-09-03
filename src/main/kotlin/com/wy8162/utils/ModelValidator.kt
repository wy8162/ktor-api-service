package com.wy8162.utils // ktlint-disable filename

import jakarta.validation.Validation

private val validator = Validation.buildDefaultValidatorFactory().validator

fun <T : Any> T.validate(): MutableList<String> {
    return validator.validate(this)
        .map {
            "${it.invalidValue} : ${it.message}"
        }
        .toMutableList()
}
