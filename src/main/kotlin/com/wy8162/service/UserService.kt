package com.wy8162.service

import arrow.core.Either
import arrow.core.rightIfNotNull
import com.wy8162.error.ApiError
import com.wy8162.error.ErrorCode
import com.wy8162.model.User
import com.wy8162.model.UserEntity
import com.wy8162.model.request.LoginRequest
import com.wy8162.model.request.UserRequest
import com.wy8162.model.toUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PSQLState
import java.util.UUID

interface UserService {
    suspend fun registerUser(user: UserRequest): Either<ApiError, User>
    suspend fun getUserById(id: String): Either<ApiError, User>
    suspend fun getUserByUsernameAndPassword(login: LoginRequest): Either<ApiError, User>
    suspend fun getAllUsers(): Either<ApiError, List<User>>
}

class UserServiceImpl : UserService {
    override suspend fun registerUser(user: UserRequest): Either<ApiError, User> = Either.catch {
        databaseQuery {
            UserEntity.insert {
                it[email] = user.email
                it[userName] = user.userName ?: user.email
                it[password] = user.password
                it[phone] = user.phone
                it[cell] = user.cell
            }
        }
    }.mapLeft {
        resolveException(it)
    }.map { insertStatement ->
        insertStatement.resultedValues?.single()!!.toUser()
    }

    override suspend fun getUserById(id: String): Either<ApiError, User> = databaseQuery {
        UserEntity.select {
            UserEntity.id eq UUID.fromString(id)
        }.singleOrNull().rightIfNotNull {
            ApiError(ErrorCode.ERR_DB_FAILURE, "User not found")
        }.map {
            it.toUser()
        }
    }

    override suspend fun getUserByUsernameAndPassword(login: LoginRequest) = databaseQuery {
        UserEntity.select {
            (UserEntity.userName eq login.username) and (UserEntity.password eq login.password)
        }.singleOrNull().rightIfNotNull {
            ApiError(ErrorCode.ERR_DB_FAILURE, "User not found")
        }.map {
            it.toUser()
        }
    }

    override suspend fun getAllUsers(): Either<ApiError, List<User>> = databaseQuery {
        UserEntity.selectAll()
            .toList().rightIfNotNull {
                ApiError(ErrorCode.ERR_DB_FAILURE, "No user found")
            }.map { row ->
                row.map { it.toUser() }
            }
    }

    private suspend fun <T> databaseQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction {
                addLogger()
                block()
            }
        }

    private fun resolveException(cause: Throwable): ApiError = when (cause) {
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
