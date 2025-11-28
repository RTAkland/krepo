/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:39 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalTime::class)

package krepo.routing

import krepo.RESOURCE_PATH
import krepo.fr.resources.getResource
import krepo.fr.resources.resourceIndex
import krepo.util.contentTypeMapper
import krepo.util.file.exists
import krepo.util.file.rawSource
import krepo.util.initialResources
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