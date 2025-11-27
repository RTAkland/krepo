/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:49 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.cfg

import cn.rtast.kazure.resources.resources
import krepo.azure.entity.User
import krepo.util.fromJson

class UserManager {
    private val users = mutableListOf<User>()

    init {
        val resUsers: String by resources("users.json")
        users.addAll(resUsers.fromJson<List<User>>())
    }

    fun validate(username: String, password: String): Boolean {
        val user = users.find { it.name == username } ?: return false
        return user.password == password
    }

    fun validateAzureUser(email: String): Boolean {
        return users.any { it.email.equals(email, ignoreCase = true) }
    }

    fun getUser(username: String): User? = users.find { it.name == username }

    fun getUserByUID(id: String): User? = users.find { it.uid == id }

    fun allUsers(): List<User> = users
}