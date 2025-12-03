/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:53
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.routing.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import krepo.DEFAULT_CONFIG
import krepo.configManager
import krepo.entity.BackendVersion
import krepo.entity.FrontendConfig
import krepo.entity.FrontendConfigResponse
import krepo.entity.maven.ConfigRepositoryWithSize
import krepo.entity.maven.RepositoryVisibility
import krepo.entity.res.CommonDataResponse
import krepo.entity.res.CommonResponse
import krepo.tokenManager
import krepo.util.respondJson
import krepo.util.toJson

fun Application.configureAPIFrontendConfigRouting() {
    routing {
        get("/api/version") {
            val token = call.principal<BearerTokenCredential>()
            val repos = if (token == null) {
                configManager.getConfig().repositories.toMutableList().apply {
                    removeAll {
                        it.visibility == RepositoryVisibility.Internal
                    }
                }
            } else {
                val isValidToken = tokenManager.validate(token.token)
                if (isValidToken) configManager.getConfig().repositories else {
                    configManager.getConfig().repositories.toMutableList().apply {
                        removeAll {
                            it.visibility == RepositoryVisibility.Internal
                        }
                    }
                }
            }
            val resp = FrontendConfigResponse(
                configManager.getConfig().frontendConfig,
                repos.map {
                    ConfigRepositoryWithSize(
                        it.name,
                        it.visibility,
                        it.acceptExtensions,
                        it.allowSnapshot,
                        it.status,
                        0L
                    )
                }
            )
            call.respondJson(resp.toJson())
        }
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
                frontendConfig = FrontendConfig(
                    DEFAULT_CONFIG.frontendConfig.pageTitle,
                    DEFAULT_CONFIG.frontendConfig.icpLicense,
                    DEFAULT_CONFIG.frontendConfig.description,
                    DEFAULT_CONFIG.frontendConfig.copyright,
                    DEFAULT_CONFIG.frontendConfig.enableAzureSignIn,
                    BackendVersion(0, -1)
                ),
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
            frontendConfig = FrontendConfig(
                putConfig.pageTitle,
                icpLicense,
                putConfig.description,
                putConfig.copyright,
                currentConfig.frontendConfig.enableAzureSignIn,
                BackendVersion(0, -1)
            ),
        )
        configManager.write(newConfig)
        call.respond(HttpStatusCode.OK, CommonResponse(200, "更新成功"))
    }
}

private fun Route.configureGetFrontendConfigRouting() {
    get("/frontend") {
        val config = configManager.getConfig().frontendConfig
        call.respond(
            HttpStatusCode.OK, CommonDataResponse(
                200,
                FrontendConfig(
                    config.pageTitle,
                    config.icpLicense,
                    config.description,
                    config.copyright,
                    config.enableAzureSignIn,
                    BackendVersion(0, -1)
                )
            )
        )
    }
}