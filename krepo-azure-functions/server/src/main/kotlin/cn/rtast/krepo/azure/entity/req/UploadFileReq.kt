/*
 * Copyright © 2025 RTAkland
 * Date: 11/24/25, 10:35 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.entity.req

data class UploadFileReq(
    val file: String,
    val filename: String,
    val path: String
)