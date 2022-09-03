package com.wy8162.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

const val ENVIRONMENT_LOCAL = "local"
const val ENVIRONMENT_DEV = "dev"
const val ENVIRONMENT_STAGING = "staging"
const val ENVIRONMENT_PROD = "prod"

class ApplicationConfigurations(
    private val appConfig: Config = ConfigFactory.load()
) : Config by appConfig

private val applicationConfig = ApplicationConfigurations()

object AppConfig {
    fun applicationEnvironment(): String = applicationConfig.getString("ktor.environment")
    fun appServerPort(): Int = applicationConfig.getInt("ktor.serverPort")
    fun appMetricServerPort(): Int = applicationConfig.getInt("ktor.metricsServer")

    fun CFG() = applicationConfig
}
