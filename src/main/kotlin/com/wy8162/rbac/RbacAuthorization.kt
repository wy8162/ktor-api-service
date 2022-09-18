package com.wy8162.rbac

import com.wy8162.error.UnauthorizedAccessException
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.application.plugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.util.AttributeKey

typealias RbacAuthorizationFunction =
    suspend ApplicationCall.(rbac: RbacCredential) -> RbacCredential

data class RbacRole(val role: String, val requesterId: String)

data class RbacCredential(
    val authorized: Boolean = false,
    val roles: List<RbacRole>? = null
)

class RbacAuthorization(var config: RbacAuthorizationConfig) {

    fun configure(block: RbacAuthorizationConfig.() -> Unit) {
        block(config)
    }

    class RbacAuthorizationConfig(providers: Map<String?, RbacAuthorizationProvider> = emptyMap()) {
        val providers = providers.toMutableMap()

        fun register(provider: RbacAuthorizationProvider) {
            providers[provider.name] = provider
        }
    }

    companion object :
        BaseApplicationPlugin<Application, RbacAuthorizationConfig, RbacAuthorization> {
        override val key: AttributeKey<RbacAuthorization>
            get() = AttributeKey(name = "RbacAuthorization")

        override fun install(
            pipeline: Application,
            configure: RbacAuthorizationConfig.() -> Unit
        ): RbacAuthorization {
            val config = RbacAuthorizationConfig().apply(configure)
            return RbacAuthorization(config)
        }
    }
}

fun RbacAuthorization.RbacAuthorizationConfig.rbac(
    name: String? = null,
    configure: RbacAuthorizationProvider.Config.() -> Unit
) {
    val provider = RbacAuthorizationProvider.Config(name).apply(configure).build()
    register(provider)
}

class RbacAuthorizationProvider(config: Config) {
    val name: String? = config.name
    val authorizationFunction = config.authFunction
    class Config(val name: String? = null) {
        internal var authFunction: RbacAuthorizationFunction = { rbacCredential ->
            throw NotImplementedError("RBAC provided not implemented.")
        }

        public fun validate(validate: RbacAuthorizationFunction) {
            authFunction = validate
        }
        fun build() = RbacAuthorizationProvider(this)
    }
}

fun Route.authorize(name: String, vararg roles: RbacRole, build: Route.() -> Unit): Route {
    val route = createChild(RbacRouteSelector())
    val plugin = createRouteScopedPlugin("RbacAuthorization") {
        on(AuthenticationChecked) { call ->

            val authConfig = call.application.plugin(RbacAuthorization).config
            val validate = authConfig.providers["rbac"]?.authorizationFunction!!
            val rbacCredential = validate(call, RbacCredential(roles = roles.toList()))

            if (!rbacCredential.authorized) {
                throw UnauthorizedAccessException()
            }
        }
    }
    route.install(plugin)
    route.build()
    return route
}

class RbacRouteSelector : RouteSelector() {
    override fun evaluate(
        context: RoutingResolveContext,
        segmentIndex: Int
    ) = RouteSelectorEvaluation.Transparent
}
