/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/12 20:58
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util.http

import io.ktor.client.engine.HttpClientEngineFactory

expect val httpClientEngine: HttpClientEngineFactory<*>