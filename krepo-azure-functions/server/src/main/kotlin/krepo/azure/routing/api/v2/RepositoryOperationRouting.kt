/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:50 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.routing.api.v2

import cn.rtast.kazure.*
import cn.rtast.kazure.response.respondJson
import cn.rtast.kazure.response.respondText
import cn.rtast.kazure.trigger.HttpRouting
import cn.rtast.kazure.util.fromJson
import krepo.azure.entity.req.DeleteFileReq
import krepo.azure.entity.req.DeleteGAVReq
import krepo.azure.entity.req.UploadFileReq
import krepo.azure.entity.res.CommonResponse
import krepo.azure.util.*

@HttpRouting("api/azure/repository/operation/file", [HttpMethod.POST, HttpMethod.DELETE])
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

@HttpRouting("api/azure/repository/operation/gav", [HttpMethod.DELETE])
fun deleteGAVRouting(
    request: HttpRequest<String>,
    context: HttpContext,
): HttpResponse {
    val payload = request.body.fromJson<DeleteGAVReq>()
    if (payload.isDirectory) {
        deleteDirectory(payload.path.strip())
    } else {
        deleteFile(payload.path.strip())
    }
    return request.respondText("OK")
}