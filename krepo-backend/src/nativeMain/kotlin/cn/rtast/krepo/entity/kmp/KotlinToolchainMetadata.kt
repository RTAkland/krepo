/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/17 05:55
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.entity.kmp


import cn.rtast.krepo.enums.KotlinMultiplatformProjectType
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