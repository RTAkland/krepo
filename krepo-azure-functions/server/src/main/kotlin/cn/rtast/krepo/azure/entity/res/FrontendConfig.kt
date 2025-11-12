/*
 * Copyright © 2025 RTAkland
 * Date: 11/12/25, 9:08 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.entity.res

data class FrontendConfig(
    val pageTitle: String,
    val icpLicense: String?,
    val description: String?,
    val copyright: String,
    val enableAzureSignIn: Boolean
)