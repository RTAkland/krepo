/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/16 01:27
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kmvnrepo.util.manager

import cn.rtast.kmvnrepo.ROOT_PATH
import cn.rtast.kmvnrepo.entity.User
import cn.rtast.kmvnrepo.util.*
import kotlinx.io.files.Path
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserManager {
    private val file = Path(ROOT_PATH, "users.json").apply {
        if (!exists()) writeText(listOf(User("admin", Uuid.random().toString(), "admin@rtast.cn")).toJson())
    }

    private var users = file.readText().fromJson<MutableList<User>>()

    private fun readUsers() {
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

    fun validateUser(username: String, password: String): Boolean {
        return users.any { it.name == username && it.password == password }
    }

    fun removeUser(username: String): Boolean {
        return users.removeAll { it.name == username }.apply { sync() }
    }

    fun getUser(name: String): User? = users.find { it.name == name }

    fun getAllUsers(): List<User> = users
}