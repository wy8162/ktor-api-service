package com.wy8162.controller

import arrow.core.Either
import com.wy8162.error.InvalidEmployeeIdException
import com.wy8162.model.ApiContext
import com.wy8162.service.HrService
import io.ktor.http.HttpStatusCode

class HrController(
    private val hrService: HrService
) {
    suspend fun getEmployee(ctx: ApiContext) {
        val id = ctx.call.parameters["employeeId"] ?: throw InvalidEmployeeIdException()

        when (val emp = hrService.getEmployee(id.toInt())) {
            is Either.Left -> throw InvalidEmployeeIdException()
            is Either.Right -> {
                ctx.httpStatus = HttpStatusCode.OK
                ctx.apiResponse["data"] = emp.value
            }
        }
    }
}
