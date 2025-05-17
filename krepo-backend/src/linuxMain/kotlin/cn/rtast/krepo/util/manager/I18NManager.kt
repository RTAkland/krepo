/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:50
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.util.manager

import cn.rtast.krepo.i18nManager
import cn.rtast.krepo.resources.getResource
import cn.rtast.krepo.util.string.fromJson
import cn.rtast.krepo.util.string.getLanguage
import io.ktor.server.application.ApplicationCall

class I18NManager {
    private val zhCN = getResource("lang/zh-cn.json").asString().fromJson<Map<String, String>>()
    private val enUS = getResource("lang/en-us.json").asString().fromJson<Map<String, String>>()

    private val languages = mapOf("zh-cn" to zhCN, "en-us" to enUS)

    fun translate(language: String, key: String): String {
        return languages[language]?.get(key) ?: enUS[key]!!
    }
}

fun ApplicationCall.i18n(key: String) = i18nManager.translate(this.getLanguage(), key)