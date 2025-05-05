/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 22:06
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalTime::class)

package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.entity.Config
import cn.rtast.kmvnrepo.pages.publicContentListingPage
import cn.rtast.kmvnrepo.pages.webuiPage
import cn.rtast.kmvnrepo.util.fromJson
import dev.fritz2.core.render
import dev.fritz2.headless.components.toastContainer
import dev.fritz2.headless.foundation.portalRoot
import dev.fritz2.remote.http
import dev.fritz2.routing.routerOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

val router = routerOf("contents")
var currentPath = ""
var backend = "http://127.0.0.1:9098"
val coroutineScope = MainScope()

fun main() {
    toastContainer("default", "toast-container")
    coroutineScope.launch {
        backend = http("/config.json").get().body().fromJson<Config>().backend
        render("#target") {
            router.data.render { site ->
                currentPath = site
                if (site.startsWith("webui")) webuiPage()
                else publicContentListingPage()
            }
            portalRoot()
        }
    }
}