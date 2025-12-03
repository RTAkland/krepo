/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 7:57 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package krepo.azure.routing.api.repository

import cn.rtast.kazure.*
import cn.rtast.kazure.response.respondText
import cn.rtast.kazure.trigger.HttpRouting
import krepo.azure.util.*
import krepo.azure.util.string.decodeToByteArray
import krepo.azure.util.string.strip
import krepo.entity.maven.DeleteGAVRequest
import krepo.entity.maven.UploadFilePayload
import krepo.entity.maven.UploadFileResponse
import krepo.util.fromProtobuf

@HttpRouting("/api/azure/repository/operation/file", [HttpMethod.POST])
fun uploadOrDeleteFileRouting(
    request: HttpRequest<ByteArray>,
    context: HttpContext,
): HttpResponse {
    val payload = request.body.fromProtobuf<UploadFilePayload>()
    val fileContent = payload.file.decodeToByteArray()
    val result = uploadFile(
        "${payload.path.removePrefix("./repositories/").removeSuffix("/")}/${payload.filename}",
        fileContent,
        false
    )
    return if (result) request.respondProtobuf(UploadFileResponse(200)) else request.respondProtobuf(
        UploadFileResponse(409),
        status = HttpStatus.CONFLICT
    )
}

@HttpRouting("/api/azure/repository/operation/gav", [HttpMethod.DELETE])
fun deleteGAVRouting(
    request: HttpRequest<ByteArray>,
    context: HttpContext,
): HttpResponse {
    val payload = request.body.fromProtobuf<DeleteGAVRequest>()
    if (payload.isDirectory) {
        deleteDirectoryIndividually(payload.gav.strip())
    } else {
        deleteFile(payload.gav.strip())
    }
    return request.respondText("OK")
}

@HttpRouting("/api/azure/repository/operation/directory/create", [HttpMethod.POST])
fun createRepositoryDirectory(
    request: HttpRequest<ByteArray>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}

@HttpRouting("/api/azure/repository/create", methods = [HttpMethod.POST])
fun createRepository(
    request: HttpRequest<ByteArray>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}

@HttpRouting("/api/azure/repository/modify", methods = [HttpMethod.PUT])
fun modifyRepository(
    request: HttpRequest<ByteArray>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}

@HttpRouting("/api/azure/repository/delete", methods = [HttpMethod.DELETE])
fun deleteRepository(
    request: HttpRequest<ByteArray>,
    context: HttpContext,
): HttpResponse {
    return request.notImplemented()
}