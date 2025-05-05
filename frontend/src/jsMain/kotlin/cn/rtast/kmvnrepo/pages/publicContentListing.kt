/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 15:28
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages

import cn.rtast.kmvnrepo.backend
import cn.rtast.kmvnrepo.components.errorToast
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.currentPath
import cn.rtast.kmvnrepo.entity.Contents
import cn.rtast.kmvnrepo.util.formatSize
import cn.rtast.kmvnrepo.util.fromJson
import cn.rtast.kmvnrepo.util.getDate
import cn.rtast.kmvnrepo.util.httpRequest
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.remote.http
import kotlinx.coroutines.launch

//fun RenderContext.publicContentListingPage() {
//    coroutineScope.launch {
//        val api = http("$backend/@/api/contents/$currentPath")
//        val response = api.get()
//        if (response.status == 404) {
//            div("text-red-500 text-xl font-semibold mt-10") { +"404 - Not Found" }
//            return@launch
//        }
//        require(response.ok)
//        val responseJson = response.body().fromJson<Contents>()
//        div("p-6 max-w-4xl mx-auto") {
//            h2("text-2xl font-bold mb-4 text-gray-800") { +"Index of $currentPath" }
//            hr { }
//            if (currentPath.isNotEmpty()) {
//                div("mb-2") {
//                    a {
//                        val parentPath = currentPath.trimEnd('/').substringBeforeLast('/', "")
//                        href("/#$parentPath")
//                        className("text-blue-600 hover:underline")
//                        +"../"
//                    }
//                }
//            }
//            div("grid grid-cols-[1fr_160px_100px] gap-2 text-sm font-mono") {
//                responseJson.data.sortedWith(compareBy({ !it.isDirectory }, { it.name })).forEach { entry ->
//                    a {
//                        if (entry.isDirectory) {
//                            href("/#$currentPath/${entry.name}")
//                            className("text-blue-500 hover:underline truncate")
//                            +"${entry.name}/"
//                        } else {
//                            href("$backend$currentPath/${entry.name}")
//                            className("text-gray-800 hover:underline truncate")
//                            +entry.name
//                        }
//                    }
//                    span("text-gray-500 text-right") {
//                        +getDate(entry.timestamp)
//                    }
//                    span("text-gray-400 text-right") {
//                        if (!entry.isDirectory) +formatSize(entry.size) else +"—"
//                    }
//                }
//            }
//            hr { className("mt-6") }
//        }
//    }
//}

fun RenderContext.publicContentListingPage() {
    coroutineScope.launch {
        val api = httpRequest("/@/api/contents/$currentPath")
        val response = api.get()
        if (response.status == 404) {
            div("notification is-danger") { +"404 - Not Found" }
            return@launch
        }
        require(response.ok) { errorToast("获取文件列表失败!") }
        val responseJson = response.body().fromJson<Contents>()
        div("container") {
            br {}
            h4("title is-4 has-text-centered") { i { +"Index of $currentPath" } }
            hr { }
            if (currentPath.isNotEmpty()) {
                div("mb-4") {
                    a {
                        val parentPath = currentPath.trimEnd('/').substringBeforeLast('/', "")
                        href("/#$parentPath")
                        className("button is-link")
                        +"上一级 / Go up"
                    }
                }
            }
            div("columns is-multiline") {
                responseJson.data.sortedWith(compareBy({ !it.isDirectory }, { it.name })).forEach { entry ->
                    div("column is-one-third") {
                        div("box") {
                            a {
                                if (entry.isDirectory) {
                                    href("/#$currentPath/${entry.name}")
                                    className("has-text-dark")
                                    i { +"${entry.name}/" }
                                } else {
                                    href("$backend$currentPath/${entry.name}")
                                    className("has-text-dark")
                                    b { +entry.name }
                                }
                            }
                            div("level is-mobile") {
                                div("level-left") {
                                    i { span("has-text-grey") { +getDate(entry.timestamp) } }
                                }
                                div("level-right") {
                                    span("has-text-grey") {
                                        b { if (!entry.isDirectory) +formatSize(entry.size) else +"—" }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            hr { className("mt-6") }
        }
    }
}
