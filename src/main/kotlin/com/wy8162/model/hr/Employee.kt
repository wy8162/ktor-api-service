package com.wy8162.model.hr

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.math.BigDecimal
import java.time.Instant

data class Employee(
    val employeeId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val hireDate: Instant,
    val jobId: String,
    val salary: BigDecimal,
    val commissionPct: BigDecimal?,
    val managerId: Int?,
    val departmentId: Int
)

object EmployeeEntity : Table("employees") {
    val employeeId = integer("employee_id").uniqueIndex("employees_pkey")
    val firstName = varchar("first_name", 32)
    val lastName = varchar("last_name", 32)
    val email = varchar("email", 32)
    val phoneNumber = varchar("phone_number", 32)
    val hireDate = timestamp("hire_date")
    val jobId = varchar("job_id", 16) references JobEntity.jobId
    val salary = decimal("salary", 16, 2)
    val commissionPct = decimal("commission_pct", 4, 2).nullable()
    val managerId = integer("manager_id").nullable()
    val departmentId = integer("department_id") references DepartmentEntity.departmentId

    override val primaryKey = PrimaryKey(employeeId, name = "employees_pkey")
}

fun ResultRow.toEmployee(): Employee = Employee(
    employeeId = this[EmployeeEntity.employeeId],
    firstName = this[EmployeeEntity.firstName],
    lastName = this[EmployeeEntity.lastName],
    email = this[EmployeeEntity.email],
    phoneNumber = this[EmployeeEntity.phoneNumber],
    hireDate = this[EmployeeEntity.hireDate],
    jobId = this[EmployeeEntity.jobId],
    salary = this[EmployeeEntity.salary],
    commissionPct = this[EmployeeEntity.commissionPct],
    managerId = this[EmployeeEntity.managerId],
    departmentId = this[EmployeeEntity.departmentId]
)
