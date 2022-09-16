package com.wy8162.rbac

import com.wy8162.error.UnauthorizedAccessException
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext

fun Route.authorize(name: String, vararg roles: String, build: Route.() -> Unit): Route {
    val route = createChild(RbacRouteSelector())
    val plugin = createRouteScopedPlugin("RbacAuthorization") {
        on(AuthenticationChecked) { call ->
            val role = call.request.headers["role"] ?: ""
            if (!roles.toHashSet().contains(role)) {
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
