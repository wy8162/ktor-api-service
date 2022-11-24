package com.wy8162.model

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import java.sql.ResultSet
import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val userName: String?,
    val password: String,
    val phone: String?,
    val cell: String?
)

object UserEntity : UUIDTable(name = "users") {
    val email = varchar("email", 255).uniqueIndex()
    val userName = varchar("username", 255).uniqueIndex().nullable()
    val password = varchar("password", 255)
    val phone = varchar("phone", 255).nullable()
    val cell = varchar("cell", 255).nullable()
}

fun ResultRow.toUser(): User = User(
    id = this[UserEntity.id].value,
    email = this[UserEntity.email],
    userName = this[UserEntity.userName],
    password = this[UserEntity.password],
    phone = this[UserEntity.phone],
    cell = this[UserEntity.cell]
)
