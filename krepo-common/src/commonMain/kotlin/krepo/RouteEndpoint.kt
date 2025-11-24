/*
 * Copyright © 2025 RTAkland
 * Date: 11/25/25, 1:13 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo

public data class RouteEndpoint(val route: String) {
    private var _route = route

    public operator fun plus(other: Any?): RouteEndpoint {
        _route += other.toString()
        return this
    }

    override fun toString(): String = _route
}


internal fun createRouteEndpoint(route: String): RouteEndpoint = RouteEndpoint(route)
