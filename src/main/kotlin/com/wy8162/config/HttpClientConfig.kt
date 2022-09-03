package com.wy8162.config // ktlint-disable filename

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache.Apache
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("HttpClient")

fun httpClientInstance(): HttpClient {
    val client = HttpClient(Apache, httpClientCommonSettings())

    httpClientPlugging(client)
    return client
}

private fun httpClientPlugging(client: HttpClient) {
    client.plugin(HttpSend).intercept { request ->
        val call = execute(request)
        logger.info("${call.request.url} [${call.response.status.value}] (${call.response.responseTime.timestamp - call.response.requestTime.timestamp})")
        call
    }
}

private fun httpClientCommonSettings(): HttpClientConfig<*>.() -> Unit = {
    expectSuccess = true
    install(Logging) { level = LogLevel.INFO }
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = AppConfig.CFG().getInt("ktor.app.http.maxRetries"))
        exponentialDelay()
    }

    install(HttpTimeout) {
        requestTimeoutMillis = AppConfig.CFG().getLong("ktor.app.http.requestTimeout")
        connectTimeoutMillis = AppConfig.CFG().getLong("ktor.app.http.connectTimeout")
        socketTimeoutMillis = AppConfig.CFG().getLong("ktor.app.http.socketTimeout")
    }
}
