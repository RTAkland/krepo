/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 14:23
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.routing.api

import cn.rtast.kmvnrepo.DEFAULT_CONFIG
import cn.rtast.kmvnrepo.configManager
import cn.rtast.kmvnrepo.entity.FrontendConfig
import cn.rtast.kmvnrepo.entity.res.CommonDataResponse
import cn.rtast.kmvnrepo.entity.res.CommonResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAPIFrontendConfigRouting() {
    routing {
        route("/@/api/config") {
            configureGetFrontendConfigRouting()
            authenticate("api") {
                configureUpdateFrontendConfigRouting()
                configureResetFrontendConfigRouting()
            }
        }
    }
}

private fun Route.configureResetFrontendConfigRouting() {
    put("/frontend/reset") {
        val newConfig = configManager.getConfig()
            .copy(
                pageTitle = DEFAULT_CONFIG.pageTitle,
                icpLicense = DEFAULT_CONFIG.icpLicense
            )
        configManager.write(newConfig)
        call.respond(HttpStatusCode.OK, CommonResponse(200, "重置成功"))
    }
}

private fun Route.configureUpdateFrontendConfigRouting() {
    put("/frontend") {
        val putConfig = call.receive<FrontendConfig>()
        val currentConfig = configManager.getConfig()
        val icpLicense = if (putConfig.icpLicense.isNullOrBlank()) null
        else putConfig.icpLicense
        val newConfig = currentConfig.copy(
            pageTitle = putConfig.pageTitle,
            icpLicense = icpLicense
        )
        configManager.write(newConfig)
        call.respond(HttpStatusCode.OK, CommonResponse(200, "更新成功"))
    }
}

private fun Route.configureGetFrontendConfigRouting() {
    get("/frontend") {
        val config = configManager.getConfig()
        call.respond(
            HttpStatusCode.OK, CommonDataResponse(
                200,
                FrontendConfig(config.pageTitle, config.icpLicense)
            )
        )
    }
}