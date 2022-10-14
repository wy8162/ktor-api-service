package com.wy8162.service

import arrow.core.Either
import arrow.core.rightIfNotNull
import com.wy8162.error.ApiError
import com.wy8162.error.ErrorCode
import com.wy8162.model.hr.Employee
import com.wy8162.model.hr.EmployeeEntity
import com.wy8162.model.hr.toEmployee
import org.jetbrains.exposed.sql.select

interface HrService {
    fun dummy()
    suspend fun getEmployee(employeeId: Int): Either<ApiError, Employee>
}

abstract class AbstractHrService : HrService {
    override fun dummy() {
        println("this is a dummy function")
    }
    // abstract override suspend fun getEmployee(employeeId: Int): Either<ApiError, Employee>
}

class HrServiceImpl1(
    private val dbService: DatabaseService
) : AbstractHrService() {
    override suspend fun getEmployee(employeeId: Int): Either<ApiError, Employee> = dbService.databaseQuery {
        dummy()
        EmployeeEntity.select {
            EmployeeEntity.employeeId eq employeeId
        }.singleOrNull().rightIfNotNull {
            ApiError(ErrorCode.ERR_DB_FAILURE, "Employee not found")
        }.map {
            it.toEmployee()
        }
    }
}

class HrServiceImpl2(
    private val dbService: DatabaseService
) : AbstractHrService() {
    override suspend fun getEmployee(employeeId: Int): Either<ApiError, Employee> = dbService.databaseQuery {
        dummy()
        EmployeeEntity.select {
            EmployeeEntity.employeeId eq employeeId
        }.singleOrNull().rightIfNotNull {
            ApiError(ErrorCode.ERR_DB_FAILURE, "Employee not found")
        }.map {
            it.toEmployee()
        }
    }
}

class HrServiceImpl3(
    private val dbService: DatabaseService
) : AbstractHrService() {
    override suspend fun getEmployee(employeeId: Int): Either<ApiError, Employee> = dbService.databaseQuery {
        dummy()
        EmployeeEntity.select {
            EmployeeEntity.employeeId eq employeeId
        }.singleOrNull().rightIfNotNull {
            ApiError(ErrorCode.ERR_DB_FAILURE, "Employee not found")
        }.map {
            it.toEmployee()
        }
    }
}
