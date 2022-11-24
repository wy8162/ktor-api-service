package com.wy8162.service

import com.wy8162.config.getLogger
import com.wy8162.error.ApiError
import com.wy8162.error.ErrorCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLState
import java.sql.ResultSet

interface DatabaseService {
    suspend fun <T> databaseQuery(block: () -> T): T
    suspend fun <T> execute(sql: String, transform: (ResultSet) -> T): List<T>
    fun resolveException(cause: Throwable): ApiError
}

class DatabaseServiceImpl : DatabaseService {
    override suspend fun <T> databaseQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction {
                addLogger()
                block()
            }
        }

    override suspend fun <T> execute(sql: String, transform: (ResultSet) -> T): List<T> = newSuspendedTransaction {
        try {
            execAndMap(sql, transform)
        } catch (exception: Exception) {
            getLogger().error("Failed to execute SQL", exception)
            listOf()
        }
    }
    private fun <T> execAndMap(sql: String, transform: (ResultSet) -> T): List<T> {
        val result = arrayListOf<T>()
        TransactionManager.current().exec(sql) { rs ->
            while (rs.next()) {
                result += transform(rs)
            }
        }
        return result
    }

    override fun resolveException(cause: Throwable): ApiError = when (cause) {
        is ExposedSQLException -> {
            when (cause.sqlState) {
                PSQLState.UNIQUE_VIOLATION.state -> ApiError(
                    errorCode = ErrorCode.ERR_DB_FAILURE,
                    errorMessage = "User already exists."
                )

                else -> ApiError(errorCode = ErrorCode.ERR_DB_FAILURE, errorMessage = cause.message)
            }
        }

        else -> ApiError(errorCode = ErrorCode.ERR_DB_FAILURE, errorMessage = cause.message)
    }
}
