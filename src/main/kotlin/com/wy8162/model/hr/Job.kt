package com.wy8162.model.hr

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.math.BigDecimal

data class Job(
    val jobId: String,
    val jobTitle: String,
    val minSalary: BigDecimal,
    val maxSalary: BigDecimal
)

object JobEntity : Table(name = "jobs") {
    val jobId = varchar("job_id", 16).uniqueIndex("jobs_pkey")
    val jobTitle = varchar("job_title", 64)
    val minSalary = decimal("min_salary", 16, 2)
    val maxSalary = decimal("max_salary", 16, 2)

    override val primaryKey = PrimaryKey(jobId, name = "jobs_pkey")
}

fun ResultRow.toJob(): Job = Job(
    jobId = this[JobEntity.jobId],
    jobTitle = this[JobEntity.jobTitle],
    minSalary = this[JobEntity.minSalary],
    maxSalary = this[JobEntity.maxSalary]
)
