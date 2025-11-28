/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:34 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import krepo.routing.api.configureAPIArtifactsRouting
import krepo.routing.api.configureAPIFrontendConfigRouting
import krepo.routing.api.configureAPIUserRouting
import krepo.routing.api.configureRepositoriesRouting
import krepo.routing.api.oauth.configureAzureSignInRouting
import krepo.routing.api.v2.configureV2ArtifactsRouting
import krepo.routing.cfg.installAuthenticateRouting
import krepo.routing.cfg.installAutoHeadResponseRouting
import krepo.routing.cfg.installContentNegotiationRouting
import krepo.routing.cfg.installStatusPage
import krepo.routing.configureDownloadRouting
import krepo.routing.configureHomePageRouting
import krepo.routing.configurePublicRepositoriesListing
import krepo.routing.configureUploadArtifactRouting
import krepo.util.compareResVersion
import krepo.util.file.exists
import krepo.util.initialResources
import krepo.util.manager.ConfigManager
import krepo.util.manager.TokenManager
import krepo.util.manager.UserManager
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
    configureAzureSignInRouting()
}