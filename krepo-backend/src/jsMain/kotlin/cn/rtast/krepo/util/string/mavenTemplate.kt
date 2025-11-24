/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/8 17:52
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.util.string

import cn.rtast.krepo.backend

fun getGradleKotlinDslRepositoryTemplate(repo: String): String = """
    |maven("${backend}/$repo")
""".trimMargin()

fun getGradleGroovyDslRepositoryTemplate(repo: String): String = """
    |maven {
    |   url "$backend/$repo"
    |}
""".trimMargin()

fun getMavenRepositoryTemplate(repo: String): String = """
    |<repository>
    |  <url>$backend/$repo<repository></url>
    |</repository>
""".trimMargin()

fun getSBTRepositoryTemplate(repo: String): String = """
    |resolvers +=
    |  "repository-$repo"
    |    at "$backend/$repo"
""".trimMargin()

fun getGradleKotlinDslDependenciesTemplate(group: String, name: String, version: String) = """
    |implementation("$group:$name:$version")
""".trimMargin()

fun getGradleGroovyDslDependenciesTemplate(group: String, name: String, version: String) = """
    |implementation "$group:$name:$version"
""".trimMargin()

fun getMavenDependenciesTemplate(group: String, name: String, version: String) = """
    |<dependency>
    |  <groupId>$group</groupId>
    |  <artifactId>$name</artifactId>
    |  <version>$version</version>
    |</dependency>
""".trimMargin()

fun getSBTDependenciesTemplate(group: String, name: String, version: String) = """
    |"$group" %% "$name" %% "$version"
""".trimMargin()