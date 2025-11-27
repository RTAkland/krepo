/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 00:00
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.util.file

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

fun getLocalStorage(key: String): String? = localStorage[key]

fun setLocalStorage(key: String, value: String) {
    localStorage[key] = value
}

fun removeLocalStorage(key: String) = localStorage.removeItem(key)

object LocalStorage {
    var TOKEN
        get() = getLocalStorage("mvn_token")
        set(value) = if (value == null) removeLocalStorage("mvn_token") else setLocalStorage("mvn_token", value)
    var CURRENT_USERNAME
        get() = getLocalStorage("mvn_user")
        set(value) = if (value == null) removeLocalStorage("mvn_user") else setLocalStorage("mvn_user", value)
    var EMAIL_ADDRESS
        get() = getLocalStorage("mvn_email")
        set(value) = if (value == null) removeLocalStorage("mvn_email") else setLocalStorage("mvn_email", value)
    var AVATAR
        get() = getLocalStorage("mvn_avatar")
        set(value) = if (value == null) removeLocalStorage("mvn_avatar") else setLocalStorage("mvn_avatar", value)
    var HIDDEN_HASH_FILES
        get() = getLocalStorage("mvn_hidden_hash_files")?.toBoolean() ?: true
        set(value) = setLocalStorage("mvn_hidden_hash_files", value.toString())
    var EXPIRED_TIMESTAMP
        get(): Long? = getLocalStorage("token_expired_timestamp")?.toLong()
        set(value) = if (value == null) removeLocalStorage("token_expired_timestamp")
        else setLocalStorage("token_expired_timestamp", value.toString())

    fun clearAll() {
        TOKEN = null
        CURRENT_USERNAME = null
        EMAIL_ADDRESS = null
        AVATAR = null
        EXPIRED_TIMESTAMP = null
    }
}