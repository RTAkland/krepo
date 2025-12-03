/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:49 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.manager

import krepo.azure.resources.resources
import krepo.azure.util.string.fromJson
import krepo.entity.user.User

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

    fun getNamespaceOwner(path: String): User? {
        val dotNamespace = path.replace("/", ".")
        return users.firstOrNull { user ->
            val namespaces = user.ex?.namespaces ?: emptySet()
            namespaces.any { ns -> dotNamespace.startsWith(ns) }
        }
    }
}