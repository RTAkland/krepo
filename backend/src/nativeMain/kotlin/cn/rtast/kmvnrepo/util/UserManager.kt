/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:51
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


@file:OptIn(ExperimentalUuidApi::class)

package cn.rtast.kmvnrepo.util

import cn.rtast.kmvnrepo.ROOT_PATH
import cn.rtast.kmvnrepo.entity.User
import kotlinx.io.files.Path
import kotlin.uuid.ExperimentalUuidApi


class UserManager {
    private val file = Path(ROOT_PATH, "data.json").apply {
        if (!exists()) writeText("[]")
    }

    private var users = file.readText().fromJson<MutableList<User>>()

    private fun readUsers() {
        users = file.readText().fromJson<MutableList<User>>()
    }

    private fun sync() {
        file.writeText(users.toJson())
        readUsers()
    }

    fun addUser(username: String, password: String) {
        if (users.any { it.name == username }) return
        users.add(User(username, password))
        sync()
    }

    fun validateUser(username: String, password: String): Boolean {
        return users.any { it.name == username && it.password == password }
    }

    fun removeUser(username: String) {
        users.removeAll { it.name == username }
    }
}