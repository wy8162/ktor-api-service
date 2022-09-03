package com.wy8162.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

fun Application.initializeDatabase() {
    val databaseResource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = AppConfig.CFG().getString("database.url")
            username = AppConfig.CFG().getString("database.username")
            password = AppConfig.CFG().getString("database.password")
            driverClassName = AppConfig.CFG().getString("database.driver")
            validate()
        }
    )

    Database.Companion.connect(databaseResource)

//        transaction {
//            SchemaUtils.createMissingTablesAndColumns(UserEntity)
//        }

    Flyway(
        Flyway.configure()
            .baselineOnMigrate(true)
            .dataSource(
                AppConfig.CFG().getString("database.url"),
                AppConfig.CFG().getString("database.username"),
                AppConfig.CFG().getString("database.password")
            )
    ).migrate()
}
