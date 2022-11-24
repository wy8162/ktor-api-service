package com.wy8162.controller

import arrow.core.Either
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.wy8162.config.AppConfig
import com.wy8162.config.ROLE_USER
import com.wy8162.config.getLogger
import com.wy8162.error.InvalidUserIdException
import com.wy8162.error.UnauthorizedAccessException
import com.wy8162.model.ApiContext
import com.wy8162.model.ApiStatus
import com.wy8162.model.request.LoginRequest
import com.wy8162.model.request.UserRequest
import com.wy8162.model.response.LoginResponse
import com.wy8162.service.UserService
import com.wy8162.utils.validate
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import org.slf4j.MDC
import java.util.Date

class UserController(
    private val userService: UserService
) {
    suspend fun processUserRegistration(context: ApiContext) {
        val extraData: String = context["extraData"]

        context {
            set("someOtherData1", "someOtherData2")
            set("someOtherData2", "someOtherData1")
        }

        context.apiResponse {
            set("controller", "UserController")
            set("app", "User Management")
        }

        getLogger().info("Extra data from context: $extraData")
        val userRequest = context.call.receive<UserRequest>()
        userRequest.validate()

        when (val result = userService.registerUser(userRequest)) {
            is Either.Left -> {
                context.status = ApiStatus.FAILED
                context.httpStatus = HttpStatusCode.BadRequest
                context.apiResponse.addError(result.value)
            }

            is Either.Right -> {
                context.httpStatus = HttpStatusCode.OK
                context.apiResponse["data"] = result.value
            }
        }
    }

    suspend fun processLogin(context: ApiContext) {
        val loginRequest = context.call.receive<LoginRequest>()

        loginRequest.validate()

        val user = userService.getUserByUsernameAndPassword(loginRequest)
        if (user is Either.Left) {
            throw UnauthorizedAccessException()
        }

        val token = JWT.create()
            .withAudience(AppConfig.CFG().getString("jwt.audience"))
            .withIssuer(AppConfig.CFG().getString("jwt.issuer"))
            .withClaim("username", loginRequest.username)
            .withClaim("role", ROLE_USER)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(AppConfig.CFG().getString("jwt.secret")))

        context {
            set("someOtherData1", "someOtherData2")
            set("someOtherData2", "someOtherData1")
        }

        context.apiResponse {
            this["controller"] = "UserController"
            this["app"] = "User Login"
            this["data"] = LoginResponse(token = token, refreshToken = token)
        }

        // Another way to add values to API response
        context.apiResponse.assign(
            "error" to "None",
            "status" to HttpStatusCode.OK
        )
    }

    suspend fun getUser(context: ApiContext) {
        val id = context.call.parameters["userId"] ?: throw InvalidUserIdException()
        when (val user = userService.getUserByIdSql(id)) {
            is Either.Left -> throw InvalidUserIdException()
            is Either.Right -> {
                context.httpStatus = HttpStatusCode.OK
                context.apiResponse["data"] = user.value
            }
        }
        MDC.put("user-id", id)
    }
}
