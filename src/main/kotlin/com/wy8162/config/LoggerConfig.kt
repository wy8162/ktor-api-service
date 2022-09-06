package com.wy8162.config // ktlint-disable filename

import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal fun Any.getLogger(): Logger = LoggerFactory.getLogger(this::class.java)
