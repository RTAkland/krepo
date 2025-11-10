/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 2:38 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.util

import cn.rtast.kazure.util.toJson
import cn.rtast.krepo.azure.CLOUDFLARE_API_URL
import cn.rtast.krepo.azure.cfg.ConfigManger
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

object CFKV {
    fun buildURL(name: String): String {
        return CLOUDFLARE_API_URL + "/accounts/${ConfigManger.CF_ACCOUNT_ID}/storage/kv/namespaces/${ConfigManger.CF_KV_NAMESPACE_ID}/values/$name?expiration_ttl=3600"
    }

    fun getValue(name: String): String? = runBlocking {
        val result = client.get(buildURL(name)) {
            headers {
                append("Authorization", "Bearer " + ConfigManger.CF_KV_TOKEN)
            }
        }
        return@runBlocking if (result.status == HttpStatusCode.OK) {
            result.bodyAsText()
        } else null
    }

    fun setValue(name: String, value: String) = runBlocking {
        client.put(buildURL(name)) {
            headers {
                append("Authorization", "Bearer " + ConfigManger.CF_KV_TOKEN)
            }
            setBody(mapOf("value" to value).toJson())
        }.bodyAsText()
    }
}