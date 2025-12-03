/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:26 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages

import dev.fritz2.core.*
import kotlinx.browser.window
import krepo.*
import krepo.components.errorToast
import krepo.components.fa.autoSvg
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.components.warningToast
import krepo.entity.maven.*
import krepo.enums.CheckImplType
import krepo.pages.other.NotFoundPage
import krepo.util.*
import krepo.util.byte.fromProtoBuf
import krepo.util.byte.toByteArray
import krepo.util.file.LocalStorage
import krepo.util.file.formatSize
import krepo.util.string.*
import org.khronos.webgl.ArrayBuffer
import org.w3c.files.FileReader

fun RenderContext.ContentListingPage() {
    val showDeleteFileEntryDialog = storeOf(false)
    val selectedFileEntry = storeOf("")
    val selectedFileEntryIsDirectory = storeOf(true)
    val showCreateFolderDialog = storeOf(false)
    val folderNameStore = storeOf<String?>(null)
    val repositoryContentsStore = storeOf<List<FileEntry>>(emptyList())
    val isRepositoryEmptyStore = storeOf(false)
    coroutineScope.launchJob {
        val url = when (backendVersion) {
            is BackendVersions.Azure -> {
                val repo = currentPath.split("/")[1]
                val path = currentPath.split("/").drop(2).joinToString("/")
                if (repo != "private") "${backendVersion.GET_PUBLIC_REPOSITORY_CONTENTS}?repo=$repo&path=$path" else
                    "${backendVersion.GET_PRIVATE_REPOSITORY_CONTENTS}?repo=$repo&path=$path"
            }

            is BackendVersions.Legacy -> "${backendVersion.GET_PUBLIC_REPOSITORY_CONTENTS}${currentPath.removePrefix("/")}"
        }
        val api = httpRequest(url).auth().octetType()
        val response = api.get()
        if (response.status == 404) {
            div("has-text-centered") { NotFoundPage() }
            return@launchJob
        }
        require(response.ok) { errorToast("Failed to fetch repository contents!") }
        val repositoryContents = response.arrayBuffer().fromProtoBuf<RepositoryContents>().data
        if (repositoryContents.isEmpty()) isRepositoryEmptyStore.update(true)
        repositoryContentsStore.update(repositoryContents)
    }
    repositoryContentsStore.data.render { artifacts ->
        div("container") {
            br {}
            inlineStyle("max-width: 65%")
            div("level-left") {
                h4("title is-4") {
                    id("index-of-sep-id")
                    +"Index of /"
                    val segments = currentPath.trim('/').split("/")
                    val basePath = StringBuilder("/#")
                    segments.forEachIndexed { index, segment ->
                        basePath.append("/").append(segment)
                        a("path-hover") pathSegment@{
                            href(basePath.toString())
                            +segment
                            title("Back to $basePath")
                        }
                        if (index != segments.lastIndex) +"/"
                    }
                }
            }
            hr("index-sep") {}
            if (currentPath.isNotEmpty()) {
                div("mb-4") {
                    div("level") {
                        div("level-left") {
                            a("button") {
                                val parentPath = currentPath.trimEnd('/').substringBeforeLast('/', "")
                                href("/#$parentPath")
                                autoSvg("fa-arrow-left", "")
                                title("Back")
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
                                        autoSvg("fa-copy", "")
                                        title("Copy dependency URL")
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
                                                +"Gradle Kotlin DSL"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getGradleKotlinDslDependenciesTemplate(group, name, version)
                                                    )
                                                    infoToast("Gradle Kotlin DSL Dependencies Coordinate Copied")
                                                }
                                            }
                                            a("dropdown-item") {
                                                +"Maven"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getMavenDependenciesTemplate(group, name, version)
                                                    )
                                                    infoToast("Maven Dependencies Coordinate Copied")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (LocalStorage.TOKEN != null) {
                                label("button") {
                                    title("Upload file")
                                    attr("for", "fileInput")
                                    autoSvg("fa-upload", "")
                                }
                                input("file-input") {
                                    id("fileInput")
                                    type("file")
                                    className("is-hidden")
                                    changes.values() handledBy {
                                        val file = domNode.files!!.item(0)!!
                                        val reader = FileReader()
                                        coroutineScope.launchJob {
                                            infoToast("Reading file...")
                                            reader.readAsArrayBuffer(file)
                                        }
                                        reader.onload = {
                                            coroutineScope.launchJob {
                                                val fileContent = reader.result as ArrayBuffer
                                                val body =
                                                    UploadFilePayload(
                                                        fileContent.toByteArray().encodeToBase64(),
                                                        file.name,
                                                        "./repositories$currentPath"
                                                    )
                                                infoToast("Uploading...")
                                                val result = httpRequest(backendVersion.UPLOAD_FILE)
                                                    .auth().setOctetBody(body).post().arrayBuffer()
                                                    .fromProtoBuf<UploadFileResponse>()
                                                when (result.code) {
                                                    200 -> window.location.reload()
                                                    409 -> errorToast("Upload failed, Conflict!")
                                                    else -> errorToast("Upload file unknown error")
                                                }
                                            }
                                        }
                                    }
                                }
                                a("button") {
                                    title("Create folder")
                                    autoSvg("fa-folder-plus", "")
                                    clicks handledBy { showCreateFolderDialog.update(true) }
                                }
                            }
                            a("button mr-2") {
                                title("Preferences")
                                autoSvg("fa-sliders", "")
                                clicks handledBy { infoToast("Nothing to set.") }
                            }
                        }
                    }
                }
            }
            if (artifacts.isNotEmpty()) {
                table("table is-fullwidth is-striped is-hoverable") {
                    caption { +"Repository content" }
                    thead {
                        tr {
                            th { inlineStyle("max-width: 240px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;") }
                            th {}
                            th {}
                            th {}
                            if (LocalStorage.TOKEN != null) {
                                th { inlineStyle("text-align: center;") }
                            }
                        }
                    }
                    tbody {
                        artifacts.sortedWith(compareBy({ !it.isDirectory }, { it.name })).forEach { entry ->
                            tr {
                                td {
                                    inlineStyle("max-width: 240px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;")
                                    if (entry.isDirectory) {
                                        a("folder-entry") {
                                            className("")
                                            +"${entry.name}/"
                                            title("$currentPath/${entry.name}")
                                            clicks handledBy { window.location.href = "/#$currentPath/${entry.name}" }
                                        }
                                    } else {
                                        a("file-entry") {
                                            +entry.name
                                            title("$currentPath/${entry.name}")
                                            clicks handledBy {
                                                window.location.href = "$backend$currentPath/${entry.name}"
                                            }
                                        }
                                    }
                                }
                                td("has-text-centered") {
                                    span("ml-5") {
                                        +formatDate(entry.timestamp)
                                        title(entry.timestamp.toString())
                                    }
                                }
                                td("has-text-centered") { if (!entry.isDirectory) +formatSize(entry.size) else +"-" }
                                td("has-text-left") {
                                    span {
                                        if (entry.owner != null) {
                                            autoSvg("fa-circle-check", size = 15)
                                            +"by ${entry.owner}"
                                            title("Published by ${entry.owner}")
                                        } else {
                                            autoSvg("fa-circle-question", size = 15)
                                            +"by Unknown"
                                            title("Owned by Unknown")
                                        }
                                    }
                                }
                                if (LocalStorage.TOKEN != null) {
                                    td {
                                        inlineStyle("text-align: center;")
                                        button("button is-small") {
                                            val name = if (entry.isDirectory) "directory" else "file"
                                            title("Delete $name")
                                            autoSvg("fa-trash-alt", "", size = 12)
                                            clicks handledBy {
                                                showDeleteFileEntryDialog.update(true)
                                                selectedFileEntry.update("$currentPath/${entry.name}".removePrefix("/"))
                                                selectedFileEntryIsDirectory.update(entry.isDirectory)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                isRepositoryEmptyStore.data.render {
                    h3("has-text-centered") {
                        if (!it) {
                            span("loader") {};span { +"Loading" }
                        } else +"No content in $currentPath"
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
            coroutineScope.launchJob {
                val result = httpRequest(backendVersion.CREATE_DIRECTORY)
                    .auth().setOctetBody(
                        CreateDirectoryRequest(
                            "${
                                currentPath.removeSuffix("/").removePrefix("/")
                            }/${folderNameStore.current}"
                        )
                    ).post().checkImpl(CheckImplType.Toast)
                    .arrayBuffer().fromProtoBuf<CreateDirectoryResponse>()
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
        coroutineScope.launchJob {
            httpRequest(backendVersion.DELETE_GAV)
                .auth().setOctetBody(
                    DeleteGAVRequest(
                        selectedFileEntry.current,
                        selectedFileEntryIsDirectory.current
                    )
                ).delete()
            window.location.reload()
        }
        infoToast("Deleting...")
    }
}