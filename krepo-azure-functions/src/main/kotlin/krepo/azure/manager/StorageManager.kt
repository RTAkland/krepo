/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 2:00 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.manager

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import krepo.azure.CLOUDFLARE_API_URL
import krepo.azure.util.client

class StorageManager {

    @Suppress("classname")
    object CF_KV {
        fun buildURL(name: String, ttl: Int = -1): String {
            return CLOUDFLARE_API_URL + "/${ConfigManger.CF_ACCOUNT_ID}/storage/kv/namespaces/${ConfigManger.CF_KV_NAMESPACE_ID}/values/$name" + if (ttl != -1) "?expiration_ttl=$ttl" else ""
        }

        fun getValue(name: String): String? = runBlocking {
            val result = client.get(buildURL(name)) {
                headers { append("Authorization", "Bearer " + ConfigManger.CF_KV_TOKEN) }
            }
            return@runBlocking if (result.status == HttpStatusCode.OK) {
                result.bodyAsText()
            } else null
        }

        fun setValue(name: String, value: String, ttl: Int = -1) = runBlocking {
            client.put(buildURL(name, ttl)) {
                headers { append("Authorization", "Bearer " + ConfigManger.CF_KV_TOKEN) }
                setBody(value)
            }.bodyAsText()
        }

        fun removeKey(name: String) = runBlocking {
            client.delete(buildURL(name)) {
                headers { append("Authorization", "Bearer " + ConfigManger.CF_KV_TOKEN) }
            }
        }
    }

    enum class AuthStorageType {
        CloudflareKV;

        companion object {
            fun fromString(str: String): AuthStorageType = when (str) {
                "CF_KV" -> CloudflareKV
                else -> throw IllegalStateException("No storage type of $str")
            }
        }
    }

    private val type = AuthStorageType.fromString(ConfigManger.AUTH_STORAGE_TYPE)

    fun setKV(name: String, value: String, ttl: Int = -1) {
        when (type) {
            AuthStorageType.CloudflareKV -> CF_KV.setValue(name, value, ttl)
        }
    }

    fun getKV(name: String): String? {
        return when (type) {
            AuthStorageType.CloudflareKV -> CF_KV.getValue(name)
        }
    }

    fun removeKV(name: String) {
        when (type) {
            AuthStorageType.CloudflareKV -> CF_KV.removeKey(name)
        }
    }
}