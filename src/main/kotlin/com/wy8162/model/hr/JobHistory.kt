package com.wy8162.model.hr

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

data class JobHistory(
    val employeeId: Int,
    val startDate: Instant,
    val endDate: Instant,
    val jobId: String,
    val departmentId: Int
)

object JobHistoryEntity : Table("job_history") {
    val employeeId = integer("employee_id") references EmployeeEntity.employeeId
    val startDate = timestamp("start_date")
    val endDate = timestamp("end_date")
    val jobId = varchar("job_id", 16) references JobEntity.jobId
    val departmentId = integer("department_id") references DepartmentEntity.departmentId

    override val primaryKey = PrimaryKey(arrayOf(employeeId, startDate), name = "job_history_pkey")
}

fun ResultRow.toJobHistory(): JobHistory = JobHistory(
    employeeId = this[JobHistoryEntity.employeeId],
    startDate = this[JobHistoryEntity.startDate],
    endDate = this[JobHistoryEntity.endDate],
    jobId = this[JobHistoryEntity.jobId],
    departmentId = this[JobHistoryEntity.departmentId]
)
