/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 5:27 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("unused")

package cn.rtast.krepo.azure

import cn.rtast.kazure.resources.StaticAssets
import cn.rtast.kazure.resources.staticAssets
import cn.rtast.krepo.azure.cfg.TokenManager
import cn.rtast.krepo.azure.cfg.UserManager


val userManager = UserManager()
val tokenManager = TokenManager()

@StaticAssets("/", "frontend/index.html")
val indexHtml: ByteArray by staticAssets

