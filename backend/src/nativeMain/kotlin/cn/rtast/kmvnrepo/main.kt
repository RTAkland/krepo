/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 13:58
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.routing.api.configureAPIArtifactsRouting
import cn.rtast.kmvnrepo.routing.api.configureAPIRepositoryRouting
import cn.rtast.kmvnrepo.routing.api.configureAPIUserRouting
import cn.rtast.kmvnrepo.routing.configureDownloadRouting
import cn.rtast.kmvnrepo.routing.configurePublicRepositoriesListing
import cn.rtast.kmvnrepo.routing.configureUploadArtifactRouting
import cn.rtast.kmvnrepo.util.ConfigManager
import cn.rtast.kmvnrepo.util.UserManager
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

val userManager = UserManager()
val configManager = ConfigManager().apply { initRepositories() }

fun main() {
    embeddedServer(
        CIO, port = configManager.getConfig().port, host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configurePublicRepositoriesListing()
    configureUploadArtifactRouting()
    configureDownloadRouting()
    configureAPIUserRouting()
    configureAPIRepositoryRouting()
    configureAPIArtifactsRouting()
}