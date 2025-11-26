/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:58 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.resources.Resources
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import krepo.BackendVersions
import krepo.entity.BackendVersion

@HttpRouting("api/version")
fun backendVersionRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
    val patch = Resources.readText("patch_version.txt").toInt()
    return request.respondJson(BackendVersion(BackendVersions.Azure().v, patch))
}