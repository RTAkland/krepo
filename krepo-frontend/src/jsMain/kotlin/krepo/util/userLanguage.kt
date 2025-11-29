/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/29 19:36
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.util


enum class Language(val lc: String) {
    ZH_CN("zh-CN"), EN_US("en-US");

    override fun toString(): String = lc.lowercase()

    companion object {
        fun f(s: String): Language {
            return when (s) {
                "en-US" -> EN_US
                "zh-CN" -> ZH_CN
                else -> EN_US
            }
        }
    }
}

val userLanguage = Language.f(js("navigator.language") as String)