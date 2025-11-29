/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:26 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.pages

import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import krepo.*
import krepo.components.errorToast
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.components.warningToast
import krepo.entity.*
import krepo.enums.CheckImplType
import krepo.pages.other.notFoundPage
import krepo.util.*
import krepo.util.byte.toByteArray
import krepo.util.file.LocalStorage
import krepo.util.file.formatSize
import krepo.util.string.*
import org.khronos.webgl.ArrayBuffer
import org.w3c.files.FileReader
import kotlin.time.Duration.Companion.seconds

fun RenderContext.publicContentListingPage() {
    val showDeleteFileEntryDialog = storeOf(false)
    val selectedFileEntry = storeOf("")
    val selectedFileEntryIsDirectory = storeOf(true)
    val showLocalConfigDialog = storeOf(false)
    val hiddenHashFilesToggle = storeOf(LocalStorage.HIDDEN_HASH_FILES)
    val showCreateFolderDialog = storeOf(false)
    val folderNameStore = storeOf<String?>(null)
    coroutineScope.launch {
        val url = when (backendVersion) {
            is BackendVersions.Azure -> {
                val repo = currentPath.split("/")[1]
                val path = currentPath.split("/").drop(2).joinToString("/")
                if (repo != "private") "${backendVersion.GET_PUBLIC_REPOSITORY_CONTENTS}?repo=$repo&path=$path" else
                    "${backendVersion.GET_PRIVATE_REPOSITORY_CONTENTS}?repo=$repo&path=$path"
            }

            is BackendVersions.STABLE -> "${backendVersion.GET_PUBLIC_REPOSITORY_CONTENTS}${currentPath.removePrefix("/")}"
        }
        val api = httpRequest(url)
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
                        a("path-hover") pathSegment@{
                            isDarkTheme.data.render {
                                if (!it) this@pathSegment.className("has-text-black")
                                else this@pathSegment.className("has-text-light")
                            }
                            href(basePath.toString())
                            +segment
                            title("Back to $basePath")
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
                                i("fa-solid fa-arrow-left") {}
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
                                        i("fa-solid fa-copy") {}
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
                                                img { src("assets/img/Kotlin Multiplatform icon.svg");width(12) }
                                                +"Gradle Kotlin DSL"
                                                clicks handledBy {
                                                    window.navigator.clipboard.writeText(
                                                        getGradleKotlinDslDependenciesTemplate(group, name, version)
                                                    )
                                                    infoToast("Gradle Kotlin DSL Dependencies Coordinate Copied")
                                                }
                                            }
                                            a("dropdown-item") {
                                                img { src("assets/img/maven.svg");width(12) }
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
                                    i("fa-solid fa-upload") {}
                                }
                                input("file-input") {
                                    id("fileInput")
                                    type("file")
                                    className("is-hidden")
                                    changes.values() handledBy {
                                        val file = domNode.files!!.item(0)!!
                                        val reader = FileReader()
                                        coroutineScope.launch {
                                            infoToast("Reading file...")
                                            reader.readAsArrayBuffer(file)
                                        }
                                        reader.onload = {
                                            coroutineScope.launch {
                                                val fileContent = reader.result as ArrayBuffer
                                                val body =
                                                    UploadFilePayload(
                                                        fileContent.toByteArray().encodeToBase64(),
                                                        file.name,
                                                        "./repositories$currentPath"
                                                    )
                                                infoToast("Uploading...")
                                                val result = httpRequest(backendVersion.UPLOAD_FILE)
                                                    .auth().acceptJson().jsonContentType()
                                                    .setBody(body).post().body()
                                                    .fromJson<UploadFileResponse>()
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
                                    i("fa-solid fa-folder-plus") {}
                                    clicks handledBy { showCreateFolderDialog.update(true) }
                                }
                            }
                            a("button mr-2") {
                                title("Preferences")
                                i("fa-solid fa-sliders") {}
                                clicks handledBy { showLocalConfigDialog.update(true) }
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
                            th { inlineStyle("text-align: left; width: 180px; white-space: nowrap;") }
                            th { inlineStyle("text-align: center;") }
                            if (LocalStorage.TOKEN != null) {
                                th { inlineStyle("text-align: center;") }
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
                                                val fileHref = if (developmentMode) "$backend$currentPath/${entry.name}"
                                                else "$currentPath/${entry.name}"
                                                window.location.href = fileHref
                                            }
                                        }
                                    }
                                }
                                td {
                                    inlineStyle("text-align: center;")
                                    +getDate(entry.timestamp)
                                    title(entry.timestamp.toString())
                                }
                                td {
                                    inlineStyle("text-align: center;")
                                    if (!entry.isDirectory) +formatSize(entry.size) else +"-"
                                }
                                if (LocalStorage.TOKEN != null) {
                                    td {
                                        inlineStyle("text-align: center;")
                                        button("button is-danger is-small") {
                                            val name = if (entry.isDirectory) "directory" else "file"
                                            title("Delete $name")
                                            img trashImg@{
                                                isDarkTheme.data.render {
                                                    if (it) this@trashImg.src("assets/img/trash-light.svg")
                                                    else this@trashImg.src("assets/img/trash.svg")
                                                }
                                                attr("alt", "Delete icon")
                                                inlineStyle("width: 1.1rem; height: 1.1rem;")
                                            }
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
                h3("title is-3 has-text-centered") {
                    h4("title is-4 has-text-centered") {
                        +"There's nothing here"
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
                val result = httpRequest(backendVersion.CREATE_DIRECTORY)
                    .auth().acceptJson().jsonContentType()
                    .setBody(
                        CreateDirectoryRequest(
                            "${
                                currentPath.removeSuffix("/").removePrefix("/")
                            }/${folderNameStore.current}"
                        )
                    ).post().checkImpl(CheckImplType.Toast).body().fromJson<CreateDirectoryResponse>()
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
            httpRequest(backendVersion.DELETE_GAV)
                .acceptJson().jsonContentType().auth()
                .setBody(
                    DeleteGavRequest(
                        selectedFileEntry.current,
                        selectedFileEntryIsDirectory.current
                    )
                )
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