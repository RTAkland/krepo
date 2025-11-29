/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/29 17:12
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing

import cn.rtast.kazure.HttpContext
import cn.rtast.kazure.HttpRequest
import cn.rtast.kazure.HttpResponse
import cn.rtast.kazure.response.respondRedirect
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.cfg.ConfigManger

@HttpRouting("/releases")
fun redirectReleasesRouting(
    request: HttpRequest<*>,
    context: HttpContext
): HttpResponse = request.respondRedirect("${ConfigManger.FRONTEND_URL}/#/releases")


@HttpRouting("/snapshots")
fun redirectSnapshotsRouting(
    request: HttpRequest<*>,
    context: HttpContext
): HttpResponse = request.respondRedirect("${ConfigManger.FRONTEND_URL}/#/snpshots")


@HttpRouting("/private")
fun redirectPrivateRouting(
    request: HttpRequest<*>,
    context: HttpContext
): HttpResponse = request.respondRedirect("${ConfigManger.FRONTEND_URL}/#/private")