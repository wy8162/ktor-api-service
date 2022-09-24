package com.wy8162.service

import com.wy8162.config.defaultJacksonObjectMapper
import com.wy8162.config.initializeDatabase
import com.wy8162.plugins.koinModule
import com.wy8162.plugins.registerApiModule
import com.wy8162.plugins.registerApiV1Routes
import com.wy8162.plugins.registerErrorHandlingModule
import com.wy8162.plugins.registerHrApiV1Routes
import com.wy8162.plugins.registerMonitoringModule
import com.wy8162.plugins.registerSecurityModule
import com.wy8162.plugins.registerSwaggerRoutes
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.mockkClass
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.junit5.mock.MockProviderExtension

open class BaseIntegrationTest : KoinTest {
    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(koinModule)
    }

    @JvmField
    @RegisterExtension
    val mockProvider = MockProviderExtension.create { clazz ->
        mockkClass(clazz)
    }

    open fun runTest(block: suspend ApplicationTestBuilder.(httpClient: HttpClient) -> Unit) = testApplication {
        application {
            registerApiModule()
            registerMonitoringModule()
            registerApiV1Routes()
            registerHrApiV1Routes()
            registerSwaggerRoutes()
            registerErrorHandlingModule()
            initializeDatabase()
            registerSecurityModule()
        }
        val client = createClient {
            install(ContentNegotiation) {
                jackson {
                    defaultJacksonObjectMapper()
                }
            }
        }
        environment { developmentMode = false } // to avoid error like "unnamed module of loader"
        block(client)
    }
}
