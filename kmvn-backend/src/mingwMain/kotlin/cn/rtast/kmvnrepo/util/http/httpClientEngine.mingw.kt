/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/12 16:10
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util.http

import io.ktor.client.engine.*
import io.ktor.client.engine.winhttp.*

actual val httpClientEngine: HttpClientEngineFactory<*> = WinHttp