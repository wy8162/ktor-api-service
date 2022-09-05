package com.wy8162.model.hr

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

data class Department(
    val departmentId: Int,
    val departmentName: String,
    val managerId: Int,
    val locationId: Int
)

object DepartmentEntity : Table(name = "departments") {
    val departmentId = integer("department_id").uniqueIndex("departments_pkey")
    val departmentName = varchar("department_name", 32)
    val managerId = integer("manager_id")
    val locationId = integer("location_id") references LocationEntity.locationId

    override val primaryKey = PrimaryKey(departmentId, name = "departments_pkey")
}

fun ResultRow.toDepartment(): Department = Department(
    departmentId = this[DepartmentEntity.departmentId],
    departmentName = this[DepartmentEntity.departmentName],
    managerId = this[DepartmentEntity.managerId],
    locationId = this[DepartmentEntity.locationId]
)
