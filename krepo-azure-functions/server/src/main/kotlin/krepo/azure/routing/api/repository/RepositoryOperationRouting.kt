/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:57 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api.repository

import cn.rtast.kazure.*
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.response.respondText
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.entity.req.DeleteFileReq
import krepo.azure.entity.req.DeleteGAVReq
import krepo.azure.entity.req.UploadFileReq
import krepo.azure.entity.res.CommonResponse
import krepo.azure.util.*
import krepo.azure.util.string.decodeToByteArray
import krepo.azure.util.string.strip
import krepo.util.fromJson

@HttpRouting("/api/azure/repository/operation/file", [HttpMethod.POST, HttpMethod.DELETE])
fun uploadFileRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    when (request.httpMethod) {
        HttpMethod.POST -> {
            val payload = request.body.fromJson<UploadFileReq>()
            val fileContent = payload.file.decodeToByteArray()
            val result = uploadFile(
                "${payload.path.removePrefix("./repositories/").removeSuffix("/")}/${payload.filename}",
                fileContent,
                false
            )
            return if (result) request.respondJson(CommonResponse(200, null)) else request.respondJson(
                CommonResponse(409, null),
                status = HttpStatus.CONFLICT
            )
        }

        HttpMethod.DELETE -> {
            val payload = request.body.fromJson<DeleteFileReq>()
            deleteFile(payload.name.replace("./repositories/", ""))
            return request.respondText("OK")
        }

        else -> {}
    }
    return request.respondText("Internal server error", status = HttpStatus.INTERNAL_SERVER_ERROR)
}

@HttpRouting("/api/azure/repository/operation/gav", [HttpMethod.DELETE])
fun deleteGAVRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    val payload = request.body.fromJson<DeleteGAVReq>()
    if (payload.isDirectory) {
        deleteDirectoryIndividually(payload.path.strip())
    } else {
        deleteFile(payload.path.strip())
    }
    return request.respondText("OK")
}

@HttpRouting("/api/azure/repository/operation/directory/create")
fun createRepositoryDirectory(
    request: HttpRequest<Any>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}

@HttpRouting("/api/azure/repository/create", methods = [HttpMethod.POST])
fun createRepository(
    request: HttpRequest<Any>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}

@HttpRouting("/api/azure/repository/modify", methods = [HttpMethod.PUT])
fun modifyRepository(
    request: HttpRequest<Any>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}

@HttpRouting("/api/azure/repository/delete", methods = [HttpMethod.DELETE])
fun deleteRepository(
    request: HttpRequest<Any>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}