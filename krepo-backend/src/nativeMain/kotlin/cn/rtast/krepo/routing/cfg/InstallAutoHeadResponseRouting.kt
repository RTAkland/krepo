/*
 * Copyright © 2025 RTAkland
 * Date: 9/23/25, 1:48 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.routing.cfg

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse

fun Application.installAutoHeadResponseRouting() {
    install(AutoHeadResponse)
}