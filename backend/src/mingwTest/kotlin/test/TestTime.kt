/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/18 21:59
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:OptIn(ExperimentalForeignApi::class)

package test

import cn.rtast.kmvnrepo.time.get_file_modified_time
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.test.Test

class TestTime {

    @Test
    fun `test get file modified time`() {
        println(get_file_modified_time("E:\\projects\\KMVNRepo\\backend\\src\\mingwMain\\kotlin\\cn\\rtast\\kmvnrepo\\util\\Path.mingw.kt"))
    }
}