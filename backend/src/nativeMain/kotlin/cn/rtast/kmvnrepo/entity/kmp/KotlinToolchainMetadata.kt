/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/15 19:34
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.entity.kmp


import cn.rtast.kmvnrepo.enums.KotlinMultiplatformProjectType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class KotlinToolchainMetadata(
    val projectTargets: List<ProjectTarget>
) {
    @Serializable
    data class ProjectTarget(
        val platformType: KotlinMultiplatformProjectType,
        val extras: Map<String, JsonElement>?
    )
}