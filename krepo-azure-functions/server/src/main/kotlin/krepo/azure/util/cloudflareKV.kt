/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:19 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.util

import cn.rtast.kazure.util.toJson
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import krepo.azure.CLOUDFLARE_API_URL
import krepo.azure.cfg.ConfigManger

object CFKV {
    fun buildURL(name: String, ttl: Int = -1): String {
        return CLOUDFLARE_API_URL + "/${ConfigManger.CF_ACCOUNT_ID}/storage/kv/namespaces/${ConfigManger.CF_KV_NAMESPACE_ID}/values/$name" + if (ttl != -1) "?expiration_ttl=$ttl" else ""
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

    fun setValue(name: String, value: String, ttl: Int = -1) = runBlocking {
        client.put(buildURL(name, ttl)) {
            headers {
                append("Authorization", "Bearer " + ConfigManger.CF_KV_TOKEN)
            }
            setBody(mapOf("value" to value).toJson())
        }.bodyAsText()
    }
}