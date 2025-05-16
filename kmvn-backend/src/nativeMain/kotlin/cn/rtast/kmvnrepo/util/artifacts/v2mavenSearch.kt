/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/16 20:24
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalForeignApi::class)

package cn.rtast.kmvnrepo.util.artifacts

import kotlinx.cinterop.*

expect fun searchMavenRepo(keyword: String): List<String>
