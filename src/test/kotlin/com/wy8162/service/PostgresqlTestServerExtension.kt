package com.wy8162.service

import com.wy8162.config.getLogger
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.PostgreSQLContainer
import java.lang.String

class PostgresqlTestServerExtension private constructor() : BeforeAllCallback, AfterAllCallback {
    private val logger = getLogger()

    private val postgresqlContainer = PostgreSQLContainer("postgres:latest")

    override fun beforeAll(context: ExtensionContext?) {
        System.getenv("TESTCONTAINERS_RYUK_DISABLED") ?: throw Exception("Must define env var: TESTCONTAINERS_RYUK_DISABLED=true")
        postgresqlContainer.start()
    }

    override fun afterAll(context: ExtensionContext?) {
        postgresqlContainer.stop()
    }

    fun getDatabase() = postgresqlContainer.databaseName
    fun getHost() = postgresqlContainer.host
    fun getPassword() = postgresqlContainer.password
    fun getPort() = postgresqlContainer.getMappedPort(5432)
    fun getUserName() = postgresqlContainer.getUsername()
    fun getDatabaseJdbcUrl() = String.format("jdbc:postgresql://${postgresqlContainer.host}:${getPort()}/${postgresqlContainer.databaseName}")
    fun getDatabaseR2dbcUrl() = String.format("r2dbc:pool:postgresql://${postgresqlContainer.username}:${postgresqlContainer.password}@${postgresqlContainer.host}:${getPort()}/${postgresqlContainer.databaseName}")

    companion object {
        fun create(): PostgresqlTestServerExtension {
            return PostgresqlTestServerExtension()
        }
    }
}
