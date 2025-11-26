/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:42 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity.kmp


import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import krepo.enums.KotlinMultiplatformProjectType

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