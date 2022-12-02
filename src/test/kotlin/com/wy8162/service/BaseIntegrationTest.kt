package com.wy8162.service

import com.wy8162.config.defaultJacksonObjectMapper
import com.wy8162.plugins.koinModule
import com.wy8162.plugins.registerApiModule
import com.wy8162.plugins.registerApiV1Routes
import com.wy8162.plugins.registerErrorHandlingModule
import com.wy8162.plugins.registerHrApiV1Routes
import com.wy8162.plugins.registerMonitoringModule
import com.wy8162.plugins.registerSecurityModule
import com.wy8162.plugins.registerSwaggerRoutes
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.mockkClass
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.junit5.mock.MockProviderExtension
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class BaseIntegrationTest : KoinTest {
//    @JvmField
//    @RegisterExtension
//    val dbServer = PostgresqlTestServerExtension.create()
    @Container
    val dbServer = PostgreSQLContainer("postgres:latest")

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

    // Make sure the PostgresqlTestServerExtension beforeAll and afterAll methods
    // are called to start and stop Postgresql.
//    @BeforeAll
//    fun setup() {
//    }
//
//    @AfterAll
//    fun tearDown() {
//    }

    open fun runTest(block: suspend ApplicationTestBuilder.(httpClient: HttpClient) -> Unit) =
        testApplication {
            application {
                registerApiModule()
                registerMonitoringModule()
                registerApiV1Routes()
                registerHrApiV1Routes()
                registerSwaggerRoutes()
                registerErrorHandlingModule()
                initializeTestDatabase()
                registerSecurityModule()
            }
            val client = createClient {
                install(ContentNegotiation) {
                    jackson {
                        defaultJacksonObjectMapper()
                    }
                }
            }
            environment {
                developmentMode = false
            } // to avoid error like "unnamed module of loader"
            block(client)
        }

    private fun Application.initializeTestDatabase() {
        val databaseResource = HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = String.format("jdbc:postgresql://${dbServer.host}:${dbServer.getMappedPort(5432)}/${dbServer.databaseName}")
                username = dbServer.username
                password = dbServer.password
                driverClassName = "org.postgresql.Driver"
                validate()
            }
        )

        Database.Companion.connect(databaseResource)

        Flyway(
            Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(
                    String.format("jdbc:postgresql://${dbServer.host}:${dbServer.getMappedPort(5432)}/${dbServer.databaseName}"),
                    dbServer.username,
                    dbServer.password
                )
        ).migrate()
    }
}
