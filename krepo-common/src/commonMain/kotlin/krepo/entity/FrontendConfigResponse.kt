/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/30 14:32
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.entity

import kotlinx.serialization.Serializable

@Serializable
public data class FrontendConfigResponse(
    val data: FrontendConfig,
    /**
     * exit when backend is Azure
     */
    val repositories: List<ConfigRepositoryWithSize>?
)