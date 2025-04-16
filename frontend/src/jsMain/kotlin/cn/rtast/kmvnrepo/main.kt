/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 22:06
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo

import cn.rtast.kmvnrepo.entity.Contents
import cn.rtast.kmvnrepo.util.fromJson
import dev.fritz2.core.href
import dev.fritz2.core.render
import dev.fritz2.remote.http
import dev.fritz2.routing.routerOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val router = routerOf("contents")
var currentPath = ""

fun main() {
    render("#target") {
        router.data.render { site ->
            currentPath = site
            CoroutineScope(Dispatchers.Default).launch {
                val api = http("https://repo.maven.rtast.cn/@/api/contents/$site")
                val response = api.get()
                if (response.status == 404) {
                    div { +"Not Found" }
                }
                require(response.ok)
                val responseJson = response.body().fromJson<Contents>()
                h3 { +"Index of $currentPath" }
                ul {
                    responseJson.data.sortedBy { !it.isDirectory }.forEach { file ->
                        li {
                            if (file.isDirectory) {
                                a(id = "regularFolder") {
                                    href("/#$currentPath/${file.name}")
                                    +file.name
                                }
                            } else {
                                a(id = "regularFile") {
                                    href("https://repo.maven.rtast.cn$currentPath/${file.name}")
                                    +file.name
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}