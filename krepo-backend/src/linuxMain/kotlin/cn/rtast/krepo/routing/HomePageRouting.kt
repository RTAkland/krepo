/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:25
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalTime::class)

package cn.rtast.krepo.routing

import cn.rtast.krepo.RESOURCE_PATH
import cn.rtast.krepo.fr.resources.getResource
import cn.rtast.krepo.fr.resources.resourceIndex
import cn.rtast.krepo.util.contentTypeMapper
import cn.rtast.krepo.util.file.exists
import cn.rtast.krepo.util.file.rawSource
import cn.rtast.krepo.util.initialResources
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.io.files.Path
import platform.posix.exit
import kotlin.time.ExperimentalTime

fun Application.configureHomePageRouting() {
    val indexBytes = getResource("index.html").asString()
    routing {
        get("/") {
            call.respondText(indexBytes, contentType = ContentType.Text.Html)
        }
        mapWebAssets()
    }
}

private fun Route.mapWebAssets() {
    if (!RESOURCE_PATH.exists()) {
        initialResources()
        exit(0)
    }
    resourceIndex.forEach {
        get(it.key) {
            val staticFile = Path(RESOURCE_PATH, it.key)
            call.respondSource(
                staticFile.rawSource(),
                contentType = staticFile.name.contentTypeMapper()
            )
        }
    }
}