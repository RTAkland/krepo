/*
 * Copyright © 2025 RTAkland
 * Date: 11/24/25, 10:26 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.routing.api.v2

import cn.rtast.kazure.*
import cn.rtast.kazure.response.respondText
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.kazure.util.fromJson
import cn.rtast.krepo.azure.entity.req.DeleteGAVReq
import cn.rtast.krepo.azure.entity.req.DeleteFileReq
import cn.rtast.krepo.azure.entity.req.UploadFileReq
import cn.rtast.krepo.azure.util.decodeToByteArray
import cn.rtast.krepo.azure.util.deleteDirectory
import cn.rtast.krepo.azure.util.deleteFile
import cn.rtast.krepo.azure.util.uploadFile

@HttpRouting("api/v2/repository/operation/file", [HttpMethod.POST, HttpMethod.DELETE])
fun uploadFileRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    when (request.httpMethod) {
        HttpMethod.POST -> {
            val payload = request.body.fromJson<UploadFileReq>()
            val fileContent = payload.file.decodeToByteArray()
            val result = uploadFile(payload.path.replace("./repositories/", "") + payload.filename, fileContent, false)
            return if (result) request.respondText("OK") else request.respondText(
                "Conflict",
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

@HttpRouting("api/v2/repository/operation/directory", [HttpMethod.DELETE])
fun deleteDirectoryRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    val path = request.body.fromJson<DeleteGAVReq>().path
    deleteDirectory(path)
    return request.respondText("OK")
}