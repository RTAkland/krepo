/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:50
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.krepo.util.manager

import cn.rtast.krepo.DATA_PATH
import cn.rtast.krepo.entity.User
import cn.rtast.krepo.util.file.exists
import cn.rtast.krepo.util.file.readText
import cn.rtast.krepo.util.file.writeText
import cn.rtast.krepo.util.string.fromJson
import cn.rtast.krepo.util.string.toJson
import kotlinx.io.files.Path
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
        users.add(User(username, password, email))
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

    fun getUser(name: String): User? = users.find { it.name == name }

    fun getUserByEmail(email: String): User? = users.find { it.email == email }

    fun getAllUsers(): List<User> = users
}