/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 15:28
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages

import cn.rtast.kmvnrepo.backend
import cn.rtast.kmvnrepo.components.errorToast
import cn.rtast.kmvnrepo.components.infoToast
import cn.rtast.kmvnrepo.components.showDialog
import cn.rtast.kmvnrepo.components.warningToast
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.currentPath
import cn.rtast.kmvnrepo.entity.Contents
import cn.rtast.kmvnrepo.entity.CreateDirectoryRequest
import cn.rtast.kmvnrepo.entity.CreateDirectoryResponse
import cn.rtast.kmvnrepo.entity.DeleteGavRequest
import cn.rtast.kmvnrepo.pages.other.notFoundPage
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.LocalStorage
import cn.rtast.kmvnrepo.util.file.formatSize
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.setBody
import cn.rtast.kmvnrepo.util.string.*
import dev.fritz2.core.*
import dev.fritz2.remote.http
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

fun RenderContext.publicContentListingPage() {
    val showDeleteFileEntryDialog = storeOf(false)
    val selectedFileEntry = storeOf("")
    val showLocalConfigDialog = storeOf(false)
    val hiddenHashFilesToggle = storeOf(LocalStorage.HIDDEN_HASH_FILES)
    val showCreateFolderDialog = storeOf(false)
    val folderNameStore = storeOf<String?>(null)
    coroutineScope.launch {
        try {
            if (LocalStorage.TOKEN != null) {
                if (!http("$backend/@/api/user").auth().get().ok) {
                    LocalStorage.clearAll()
                    infoToast("Session was expired~")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val api = httpRequest("/@/api/contents/$currentPath")
            .auth().acceptJson().jsonContentType()
        val response = api.get()
        if (response.status == 404) {
            div("has-text-centered") { notFoundPage() }
            return@launch
        }
        require(response.ok) { errorToast("Failed to fetch repository contents!") }
        val responseJson = response.body().fromJson<Contents>()
        val artifacts = responseJson.data.toMutableList()
        div("container") {
            br {}
            inlineStyle("max-width: 65%")
            div("level-left") {
                h4("title is-4") {
                    +"Index of /"
                    val segments = currentPath.trim('/').split("/")
                    val basePath = StringBuilder("/#")
                    segments.forEachIndexed { index, segment ->
                        basePath.append("/").append(segment)
                        a("path-hover") {
                            href(basePath.toString())
                            +segment
                        }
                        if (index != segments.lastIndex) +"/"
                    }
                }
            }
            hr {}
            if (currentPath.isNotEmpty()) {
                div("mb-4") {
                    div("level") {
                        div("level-left") {
                            a("button") {
                                val parentPath = currentPath.trimEnd('/').substringBeforeLast('/', "")
                                href("/#$parentPath")
                                img { src("/assets/img/back.svg") }
                            }
                        }
                        div("level-right") {
                            if (artifacts.any {
                                    it.name != "maven-metadata.xml" &&
                                            it.name != "maven-metadata.xml.sha1" &&
                                            it.name != "maven-metadata.xml.sha256" &&
                                            it.name != "maven-metadata.xml.sha512" &&
                                            it.name.endsWith("sha1")
                                }) {
                                val simplePath = currentPath
                                    .removeSuffix("/").split("/").drop(2)
                                    .joinToString("/")
                                val repo = currentPath.removePrefix("/").split("/").first()
                                val (group, name, version) = parseGAV(simplePath, repo)
                                div("dropdown has-dropdown is-hoverable has-background is-centered") {
                                    button("button") {
                                        i("fa-solid fa-copy") {}
                                        clicks handledBy {
                                            window.navigator.clipboard.writeText(
                                                getGradleKotlinDslDependenciesTemplate(group, name, version)
                                            )
                                            infoToast("Gradle Kotlin DSL Dependencies Coordinate Copied")
                                        }
                                    }
                                    div("dropdown-menu") {
                                        id("dropdown-menu")
                                        attr("role", "menu")
                                        div("dropdown-content") {
                                            a("dropdown-item") {
                                                img("mr-2") {
                                                    width(14)
                                                    src("/assets/img/gradle_kotlin.svg")
                                                    alt("Gradle Kotlin DSL")
                                                }
                                                +"Gradle Kotlin DSL"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getGradleKotlinDslDependenciesTemplate(group, name, version)
                                                    )
                                                    infoToast("Gradle Kotlin DSL Dependencies Coordinate Copied")
                                                }
                                            }
                                            a("dropdown-item") {
                                                img("mr-2") {
                                                    width(14)
                                                    src("/assets/img/gradle.svg")
                                                    alt("Gradle Groovy DSL")
                                                }
                                                +"Gradle Groovy DSL"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getGradleGroovyDslDependenciesTemplate(group, name, version)
                                                    )
                                                    infoToast("Gradle Groovy DSL Dependencies Coordinate Copied")
                                                }
                                            }
                                            a("dropdown-item") {
                                                img("mr-2") {
                                                    width(14)
                                                    src("/assets/img/maven.svg")
                                                    alt("Maven")
                                                }
                                                +"Maven"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getMavenDependenciesTemplate(group, name, version)
                                                    )
                                                    infoToast("Maven Dependencies Coordinate Copied")
                                                }
                                            }
                                            a("dropdown-item") {
                                                img("mr-2") {
                                                    width(14)
                                                    src("/assets/img/scala.svg")
                                                    alt("SBT")
                                                }
                                                +"SBT"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getSBTDependenciesTemplate(group, name, version)
                                                    )
                                                    infoToast("SBT Dependencies Coordinate Copied")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (LocalStorage.TOKEN != null) {
//                                label("button") {
//                                    attr("for", "fileInput")
//                                    i("fa-solid fa-upload") {}
//                                }
//                                input("file-input") {
//                                    id("fileInput")
//                                    type("file")
//                                    className("is-hidden")
//                                    changes.values() handledBy {
//                                        val filename = it.split("/").last().split("\\").last()
//                                        val reader = FileReader()
//                                        reader.readAsArrayBuffer(File())
//                                        reader.onload = { event: Event ->
//                                            val fileContent = reader.result as ArrayBuffer
//                                            val form = FormData()
//                                            form.append("file", File(arrayOf(fileContent), filename))
//                                            form.append("filename", filename)
//                                            form.append("repoPath", currentPath)
//                                        }
//                                    }
//                                }
                                a("button") {
                                    img { src("/assets/img/create-folder.svg") }
                                    clicks handledBy { showCreateFolderDialog.update(true) }
                                }
                            }
                            a("button mr-2") {
                                img { src("assets/img/settings.svg") }
                                clicks handledBy { showLocalConfigDialog.update(true) }
                            }
                        }
                    }
                }
            }
            if (artifacts.isNotEmpty()) {
                table("table is-fullwidth is-striped is-hoverable") {
                    thead {
                        tr {
                            th { +"Name" }
                            th {
                                inlineStyle("text-align: center;")
                                +"Last modified date"
                            }
                            th {
                                inlineStyle("text-align: center;")
                                +"Size"
                            }
                            if (LocalStorage.TOKEN != null) {
                                th {
                                    inlineStyle("text-align: center;")
                                    +"Action"
                                }
                            }
                        }
                    }
                    tbody {
                        if (LocalStorage.HIDDEN_HASH_FILES) artifacts.removeAll {
                            !it.isDirectory &&
                                    (it.name.endsWith("md5")
                                            || it.name.endsWith("sha256")
                                            || it.name.endsWith("sha512")
                                            || it.name.endsWith("sha1")
                                            || it.name.endsWith("asc"))
                        }
                        artifacts.sortedWith(compareBy({ !it.isDirectory }, { it.name })).forEach { entry ->
                            tr {
                                td {
                                    if (entry.isDirectory) {
                                        i("fa-regular fa-folder-open mr-2") {}
                                        a {
                                            className("has-text-link")
                                            +("${entry.name}/")
                                            clicks handledBy {
                                                window.location.href = "/#$currentPath/${entry.name}"
                                            }
                                        }
                                    } else {
                                        i("fa-regular fa-file mr-2") {}
                                        a {
                                            className("has-text-dark")
                                            +entry.name
                                            clicks handledBy {
                                                window.location.href = "$backend$currentPath/${entry.name}"
                                            }
                                        }
                                    }
                                }
                                td {
                                    inlineStyle("text-align: center;")
                                    +getDate(entry.timestamp)
                                }
                                td {
                                    inlineStyle("text-align: center;")
                                    if (!entry.isDirectory) +formatSize(entry.size) else +"-"
                                }
                                if (LocalStorage.TOKEN != null) {
                                    td {
                                        inlineStyle("text-align: center;")
                                        button("button is-danger is-small") {
                                            img {
                                                src("assets/trash.svg")
                                                attr("alt", "Delete icon")
                                                inlineStyle("width: 1.2rem; height: 1.2rem;")
                                            }
                                            clicks handledBy {
                                                showDeleteFileEntryDialog.update(true)
                                                selectedFileEntry.update("$currentPath/${entry.name}".removePrefix("/"))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                h3("title is-3 has-text-centered") {
                    img { src("assets/img/thinking.svg") }
                    h4("title is-4 has-text-centered") {
                        +"it seems like nothing here."
                    }
                }
            }
            hr { className("mt-6") }
        }
    }
    showDialog(showCreateFolderDialog, "Create a new folder", null, {
        input("input") {
            type("text")
            placeholder("Type a name for the new folder")
            changes.values() handledBy folderNameStore.update
        }
    }) {
        if (folderNameStore.current == null) {
            warningToast("Folder must not be null or empty!")
        } else {
            coroutineScope.launch {
                val result = httpRequest("/@/api/repositories/create-directory")
                    .auth().acceptJson().jsonContentType()
                    .setBody(
                        CreateDirectoryRequest(
                            "${
                                currentPath.removeSuffix("/").removePrefix("/")
                            }/${folderNameStore.current}"
                        )
                    ).post().body().fromJson<CreateDirectoryResponse>()
                if (result.code == 200) {
                    infoToast("New folder successfully created")
                    window.location.reload()
                } else {
                    errorToast("Failed to create the new folder")
                }
            }
        }
    }
    showDialog(showDeleteFileEntryDialog, "Delete the artifacts/packages", "Delete the following contents?", {
        i { +selectedFileEntry.current }
    }) {
        coroutineScope.launch {
            httpRequest("/@/api/gav")
                .acceptJson().jsonContentType().auth()
                .setBody(DeleteGavRequest(selectedFileEntry.current))
                .delete()
            window.location.reload()
        }
        infoToast("Deleting...")
    }
    showDialog(showLocalConfigDialog, "Change Settings", null, {
        div("mb-4") {
            label("switch") {
                input {
                    type("checkbox")
                    id("hiddenHashFiles")
                    inlineStyle("display: none;")
                    className("switch-checkbox")
                    checked(hiddenHashFilesToggle.current)
                    changes.values() handledBy { hiddenHashFilesToggle.update(it.toBoolean()) }
                }
                span("slider round") {}
            }
            span("ml-2") { +"Hidden hash files" }
        }
    }) {
        LocalStorage.HIDDEN_HASH_FILES = hiddenHashFilesToggle.current.toString().toBoolean()
        infoToast("Saved")
        window.setTimeout({ window.location.reload() }, 0.5.seconds.inWholeMilliseconds.toInt())
    }
}