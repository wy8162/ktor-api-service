package com.wy8162.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.wy8162.config.AppConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey

fun Application.registerSecurityModule() {
    install(Authentication) {
        val secret = AppConfig.CFG().getString("jwt.secret")
        val issuer = AppConfig.CFG().getString("jwt.issuer")
        val audience = AppConfig.CFG().getString("jwt.audience")
        val realmValue = AppConfig.CFG().getString("jwt.realm")

        jwt("auth-jwt") {
            realm = realmValue
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").isNull ||
                    credential.payload.getClaim("role").isNull
                ) {
                    null
                }
                attributes.put(
                    AttributeKey("username"),
                    credential.payload.getClaim("username").asString()
                )
                attributes.put(AttributeKey("role"), credential.payload.getClaim("role").asString())
                JWTPrincipal(credential.payload)
            }
            challenge { defaultScheme, realm ->
                // val expiresAt = credential.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
