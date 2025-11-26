/*
 * Copyright © 2025 RTAkland
 * Date: 11/9/25, 6:00 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.azure.entity

data class User(
    val name: String,
    val email: String,
    val password: String? = null,
    var uid: String = ""
)