/*
 * Copyright © 2025 RTAkland
 * Date: 2025/12/6 11:30
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.entity.maven

import kotlinx.serialization.Serializable


@Serializable
public data class GetArtifactLatestVersionResponse(
    val version: String?
)

@Serializable
public data class GetArtifactLatestVersionRequest(
    val repo: String,
    val group: String,
    val artifact: String
)