/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/14 03:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.kmvnrepo.routing

import cn.rtast.kmvnrepo.publicRepositories
import cn.rtast.kmvnrepo.util.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.configurePublicRepositoriesListing() {
    publicRepositories.forEach { repo ->
        route("/listing/${repo.name}") {
            get("{path...}") {
                val pathParts = call.parameters.getAll("path") ?: emptyList()
                val relativePath = pathParts.joinToString("/")
                val fullPath = rootPathOf("${repo.name}/$relativePath")
                if (!fullPath.exists()) {
                    call.respond(HttpStatusCode.NotFound, "Path not found.")
                    return@get
                }
                if (fullPath.isFile()) {
                    call.respondBytes(fullPath.readBytes())
                    return@get
                }
                val fileList = fullPath.listFiles().sortedWith(compareBy({ !it.isDirectory() }, { it.name }))
                val nameList = fileList.map { it.name + if (it.isDirectory()) "/" else "" }
                val maxNameLength = nameList.maxOfOrNull { it.length } ?: 0
                val html = buildString {
                    appendLine("<!DOCTYPE html>")
                    appendLine("<html><head><meta charset='utf-8'><title>Index of /${repo.name}/$relativePath</title></head><body>")
                    appendLine("<h1>Index of /${repo.name}/$relativePath</h1>")
                    appendLine("<pre style=\"font-family: monospace; line-height: 1.6;\">")
                    if (pathParts.isNotEmpty()) {
                        val parent = pathParts.dropLast(1).joinToString("/")
                        appendLine("""<a href="/listing/${repo.name}/$parent">../</a>""")
                    }
                    fileList.forEachIndexed { index, file ->
                        val name = nameList[index]
                        val href = (pathParts + file.name).joinToString("/")
                        val padding = " ".repeat(maxNameLength - name.length + 2)
                        val sizeText = if (file.isFile()) {
                            val sizeInKB = file.size()
                            "(${sizeInKB} B)"
                        } else "( - )"
                        appendLine("""<a href="/listing/${repo.name}/$href">$name</a>$padding$sizeText""")
                    }
                    appendLine("</pre></body></html>")
                }
                call.respondText(html, ContentType.Text.Html)
            }
        }
    }
}
