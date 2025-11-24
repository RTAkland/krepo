/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 19:20
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.entity

import kotlinx.serialization.Serializable

@Serializable
data class CreateRepositoryResponse(
    val code: Int,
    val message: String,
)

typealias DeleteRepositoryResponse = CreateRepositoryResponse

typealias ModifyRepositoryResponse = CreateRepositoryResponse