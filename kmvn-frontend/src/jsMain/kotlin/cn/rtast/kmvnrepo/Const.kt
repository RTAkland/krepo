/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 21:26
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo

import io.ktor.client.*
import io.ktor.client.engine.cio.*

val client = HttpClient(CIO)

fun getRepositoryTemplate(repo: String): String = """
    |maven("${backend}/$repo")
""".trimMargin()

fun getDependenciesTemplate(group: String, name: String, version: String) ="""
    |implementation("$group:$name:$version")
""".trimMargin()