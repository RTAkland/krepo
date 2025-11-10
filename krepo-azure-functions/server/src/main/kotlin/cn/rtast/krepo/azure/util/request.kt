/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 6:24 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.util

import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.response.respondBytes
import cn.rtast.kazure.response.respondText
import cn.rtast.krepo.azure.entity.res.DeployStatus
import cn.rtast.krepo.azure.userManager
import com.microsoft.azure.functions.HttpStatus

fun <T> HttpRequest<T>.ok() = this.respondText("Ok", status = HttpStatus.OK)

fun <T> HttpRequest<T>.conflict() = this.respondText("Conflict", status = HttpStatus.CONFLICT)

fun <T> HttpRequest<T>.error() = this.respondText("Error", status = HttpStatus.INTERNAL_SERVER_ERROR)

fun <T> HttpRequest<T>.unAuth() = this.respondText("UnAuthorized", status = HttpStatus.UNAUTHORIZED)

fun <T> HttpRequest<T>.notFound() = this.respondText("Not Found", status = HttpStatus.NOT_FOUND)

fun <T> HttpRequest<T>.auto(status: DeployStatus) = when (status) {
    DeployStatus.Ok -> ok()
    DeployStatus.Conflict -> conflict()
    DeployStatus.Error -> error()
    DeployStatus.UnAuth -> unAuth()
}

fun <T> HttpRequest<T>.bytes(bytes: ByteArray) = this.respondBytes(bytes)

fun <T> HttpRequest<T>.basicAuth(): Pair<String, String>? {
    val auth = this.headers["authorization"]
        ?.split(" ")?.last()?.decodeToString()?.split(":")
    return if (auth == null) null else auth.first() to auth.last()
}

fun HttpRequest<*>.basicAuthCheck(): Pair<Boolean, Pair<String, String>?> {
    val auth = this.basicAuth() ?: return false to null
    return if (userManager.validate(auth.first, auth.second)) {
        true to auth
    } else {
        false to null
    }
}