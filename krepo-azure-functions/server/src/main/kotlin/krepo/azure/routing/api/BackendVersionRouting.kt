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
import cn.rtast.kazure.resources.resources
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.trigger.HttpRouting
import krepo.BackendVersions
import krepo.entity.BackendVersion

@HttpRouting("api/version")
fun backendVersionRouting(
    request: HttpRequest<*>,
    context: HttpContext,
): HttpResponse {
//    val patch: String by resources("patch_version.txt")
    return request.respondJson(BackendVersion(BackendVersions.Azure().v, -1))
}