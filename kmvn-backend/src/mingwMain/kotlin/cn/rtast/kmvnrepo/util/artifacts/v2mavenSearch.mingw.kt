/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/16 21:08
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalForeignApi::class)

package cn.rtast.kmvnrepo.util.artifacts

import kotlinx.cinterop.*
import platform.windows.*

actual fun searchMavenRepo(keyword: String): List<String> {
    val results = mutableListOf<String>()
    fun searchRecursive(dirPath: String) {
        memScoped {
            val pattern = "$dirPath\\*"
            val findData = alloc<WIN32_FIND_DATAA>()
            val hFind = FindFirstFileA(pattern, findData.ptr)
            if (hFind == INVALID_HANDLE_VALUE) return
            try {
                do {
                    val name = findData.cFileName.toKString()
                    if (name == "." || name == "..") continue
                    val fullPath = "$dirPath/$name"
                    if (fullPath.contains(keyword)) {
                        results.add(fullPath)
                    }
                    if ((findData.dwFileAttributes.toInt() and FILE_ATTRIBUTE_DIRECTORY.toInt()) != 0) {
                        searchRecursive(fullPath)
                    }
                } while (FindNextFileA(hFind, findData.ptr) != 0)
            } finally {
                FindClose(hFind)
            }
        }
    }
    searchRecursive("repositories")
    return results
}
