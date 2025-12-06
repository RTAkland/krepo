/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:24 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.util.string

import krepo.util.customBacked

fun getGradleKotlinDslRepositoryTemplate(repo: String): String = """
    |maven("${customBacked}/$repo")
""".trimMargin()

fun getGradleKotlinDslDependenciesTemplate(group: String, name: String, version: String) = """
    |implementation("$group:$name:$version")
""".trimMargin()

fun getMavenDependenciesTemplate(group: String, name: String, version: String) = """
    |<dependency>
    |  <groupId>$group</groupId>
    |  <artifactId>$name</artifactId>
    |  <version>$version</version>
    |</dependency>
""".trimMargin()

fun getGradleKotlinDslPluginTemplate(group: String, version: String) = """
    |id("$group") version "$version"
""".trimMargin()