/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 15:21
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

fun getLocalStorage(key: String): String = localStorage[key]!!

fun setLocalStorage(key: String, value: String) {
    localStorage[key] = value
}

object LocalStorage {
    var TOKEN
        get() = getLocalStorage("mvn_token")
        set(value) = setLocalStorage("mvn_token", value)
    var CURRENT_USERNAME
        get() = getLocalStorage("mvn_user")
        set(value) = setLocalStorage("mvn_user", value)
}