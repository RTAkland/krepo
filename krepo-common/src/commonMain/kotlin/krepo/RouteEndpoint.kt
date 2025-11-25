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

    public operator fun invoke(): String = toString()

    public override fun equals(other: Any?): Boolean {
        other as RouteEndpoint
        return this._route == other._route
    }

    public override fun hashCode(): Int {
        var result = route.hashCode()
        result = 31 * result + _route.hashCode()
        return result
    }
}


internal fun createRouteEndpoint(route: String): RouteEndpoint = RouteEndpoint(route)
