/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 6:00 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.cfg

import cn.rtast.kazure.util.Resources
import cn.rtast.kazure.util.fromArrayJson
import cn.rtast.kazure.util.fromJson
import cn.rtast.krepo.azure.entity.User

class UserManager {
    private val users = mutableListOf<User>()

    init {
        users.addAll(Resources.readText("users.json").fromArrayJson<List<User>>())
    }

    fun validate(username: String, password: String): Boolean {
        val user = users.find { it.name == username } ?: return false
        return user.password == password
    }
}