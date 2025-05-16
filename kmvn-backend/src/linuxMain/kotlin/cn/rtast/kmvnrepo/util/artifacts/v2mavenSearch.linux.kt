/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/16 21:05
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalForeignApi::class)

package cn.rtast.kmvnrepo.util.artifacts

import kotlinx.cinterop.*
import platform.posix.*

actual fun searchMavenRepo(keyword: String): List<String> {
    val results = mutableListOf<String>()
    fun searchRecursive(dirPath: String) {
        memScoped {
            val dir = opendir(dirPath) ?: return
            val entry = alloc<CPointerVar<dirent>>()
            val entryBuf = alloc<dirent>()
            while (readdir_r(dir, entryBuf.ptr, entry.ptr) == 0 && entry.value != null) {
                val name = entry.value!!.pointed.d_name.toKString()
                if (name == "." || name == "..") continue
                val path = "$dirPath/$name"
                if (path.contains(keyword)) {
                    results.add(path)
                }
                val st = alloc<stat>()
                if (stat(path, st.ptr) == 0 && (st.st_mode.toInt() and S_IFMT) == S_IFDIR) {
                    searchRecursive(path)
                }
            }
            closedir(dir)
        }
    }
    searchRecursive("repositories")
    return results
}
