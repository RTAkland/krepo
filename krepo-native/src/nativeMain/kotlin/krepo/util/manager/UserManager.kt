/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:38 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class)

package krepo.util.manager

import krepo.DATA_PATH
import krepo.entity.user.User
import krepo.util.file.exists
import krepo.util.file.readText
import krepo.util.file.writeText
import kotlinx.io.files.Path
import krepo.util.string.fromJson
import krepo.util.string.toJson
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserManager {
    private val file = Path(DATA_PATH, "users.json").apply {
        if (!exists()) writeText(listOf(User("admin", Uuid.random().toString(), "admin@rtast.cn")).toJson())
    }

    private var users = file.readText().fromJson<MutableList<User>>()

    fun readUsers() {
        users = file.readText().fromJson<MutableList<User>>()
    }

    private fun sync() {
        file.writeText(users.toJson())
        readUsers()
    }

    fun addUser(username: String, password: String, email: String): Boolean {
        if (users.any { it.name == username }) return false
        users.add(User(username, email, password))
        sync()
        return true
    }

    fun addUser(user: User): Boolean {
        return this.addUser(user.name, user.password!!, user.email)
    }

    fun validateAzureUser(email: String): Boolean {
        return users.any { it.email.equals(email, ignoreCase = true) }
    }

    fun validateUser(identifier: String, password: String? = null): Boolean {
        return users.any { user ->
            (user.name == identifier || user.email == identifier) &&
                    (user.password == null || user.password == password)
        }
    }

    fun removeUser(username: String): Boolean {
        return users.removeAll { it.name == username }.apply { sync() }
    }

    fun setUserUID(email: String, uid: String) {
        users.find { it.email == email }?.uid = uid
        sync()
    }

    fun getUser(name: String): User? = users.find { it.name == name }

    fun getUserByEmail(email: String): User? = users.find { it.email == email }

    fun getUserByUID(uid: String): User? = users.find { it.uid == uid }

    fun getAllUsers(): List<User> = users
}