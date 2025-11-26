/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:49 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.cfg

import cn.rtast.kazure.resources.Resources
import cn.rtast.kazure.util.fromArrayJson
import krepo.azure.entity.User

class UserManager {
    private val users = mutableListOf<User>()

    init {
        users.addAll(Resources.readText("users.json").fromArrayJson<List<User>>())
    }

    fun validate(username: String, password: String): Boolean {
        val user = users.find { it.name == username } ?: return false
        return user.password == password
    }

    fun getUser(username: String): User? = users.find { it.name == username }
}