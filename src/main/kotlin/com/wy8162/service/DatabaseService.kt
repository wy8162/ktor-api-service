package com.wy8162.service

import com.wy8162.error.ApiError
import com.wy8162.error.ErrorCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLState

interface DatabaseService {
    suspend fun <T> databaseQuery(block: () -> T): T
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
