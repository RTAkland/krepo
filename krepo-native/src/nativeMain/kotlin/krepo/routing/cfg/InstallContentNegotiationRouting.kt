/*
 * Copyright © 2025 RTAkland
 * Date: 9/23/25, 1:46 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.routing.cfg

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.installContentNegotiationRouting() {
    install(ContentNegotiation) {
        json()
    }
}