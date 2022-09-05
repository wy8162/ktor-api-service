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
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

interface UserService {
    suspend fun registerUser(user: UserRequest): Either<ApiError, User>
    suspend fun getUserById(id: String): Either<ApiError, User>
    suspend fun getUserByUsernameAndPassword(login: LoginRequest): Either<ApiError, User>
    suspend fun getAllUsers(): Either<ApiError, List<User>>
}

class UserServiceImpl(
    private val dbService: DatabaseService
) : UserService {
    override suspend fun registerUser(user: UserRequest): Either<ApiError, User> = Either.catch {
        dbService.databaseQuery {
            UserEntity.insert {
                it[email] = user.email
                it[userName] = user.userName ?: user.email
                it[password] = user.password
                it[phone] = user.phone
                it[cell] = user.cell
            }
        }
    }.mapLeft {
        dbService.resolveException(it)
    }.map { insertStatement ->
        insertStatement.resultedValues?.single()!!.toUser()
    }

    override suspend fun getUserById(id: String): Either<ApiError, User> = dbService.databaseQuery {
        UserEntity.select {
            UserEntity.id eq UUID.fromString(id)
        }.singleOrNull().rightIfNotNull {
            ApiError(ErrorCode.ERR_DB_FAILURE, "User not found")
        }.map {
            it.toUser()
        }
    }

    override suspend fun getUserByUsernameAndPassword(login: LoginRequest) = dbService.databaseQuery {
        UserEntity.select {
            (UserEntity.userName eq login.username) and (UserEntity.password eq login.password)
        }.singleOrNull().rightIfNotNull {
            ApiError(ErrorCode.ERR_DB_FAILURE, "User not found")
        }.map {
            it.toUser()
        }
    }

    override suspend fun getAllUsers(): Either<ApiError, List<User>> = dbService.databaseQuery {
        UserEntity.selectAll()
            .toList().rightIfNotNull {
                ApiError(ErrorCode.ERR_DB_FAILURE, "No user found")
            }.map { row ->
                row.map { it.toUser() }
            }
    }
}
