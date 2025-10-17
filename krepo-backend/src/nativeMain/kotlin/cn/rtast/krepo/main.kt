/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:49
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo

import cn.rtast.krepo.routing.api.configureAPIArtifactsRouting
import cn.rtast.krepo.routing.api.configureAPIFrontendConfigRouting
import cn.rtast.krepo.routing.api.configureAPIUserRouting
import cn.rtast.krepo.routing.api.configureRepositoriesRouting
import cn.rtast.krepo.routing.api.v2.configureV2ArtifactsRouting
import cn.rtast.krepo.routing.cfg.installAuthenticateRouting
import cn.rtast.krepo.routing.cfg.installAutoHeadResponseRouting
import cn.rtast.krepo.routing.cfg.installContentNegotiationRouting
import cn.rtast.krepo.routing.cfg.installStatusPage
import cn.rtast.krepo.routing.configureDownloadRouting
import cn.rtast.krepo.routing.configureHomePageRouting
import cn.rtast.krepo.routing.configurePublicRepositoriesListing
import cn.rtast.krepo.routing.configureUploadArtifactRouting
import cn.rtast.krepo.util.compareResVersion
import cn.rtast.krepo.util.file.exists
import cn.rtast.krepo.util.initialResources
import cn.rtast.krepo.util.manager.ConfigManager
import cn.rtast.krepo.util.manager.TokenManager
import cn.rtast.krepo.util.manager.UserManager
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import platform.posix.exit

val userManager = UserManager()
val configManager = ConfigManager().apply { initRepositories() }
val tokenManager = TokenManager()

fun main(args: Array<String>) {
    if (args.firstOrNull() == "generateStatic" || !RESOURCE_PATH.exists()) {
        initialResources()
        exit(0)
    }
    compareResVersion()
    embeddedServer(factory = CIO, configure = {
        connector {
            port = configManager.getConfig().port
            host = "0.0.0.0"
        }
        reuseAddress = true
    }, module = Application::module).start(wait = true)
}

fun Application.module() {
    // cfg
    installAutoHeadResponseRouting()
    installContentNegotiationRouting()
    installStatusPage()
    installAuthenticateRouting()
    // routing
    configurePublicRepositoriesListing()
    configureUploadArtifactRouting()
    configureDownloadRouting()
    configureAPIUserRouting()
    configureAPIArtifactsRouting()
    configureRepositoriesRouting()
    configureAPIFrontendConfigRouting()
    configureHomePageRouting()
    configureV2ArtifactsRouting()
}