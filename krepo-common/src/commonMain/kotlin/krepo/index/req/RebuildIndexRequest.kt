/*
 * Copyright © 2025 RTAkland
 * Date: 11/27/25, 9:11 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.index.req

import kotlinx.serialization.Serializable

@Serializable
public data class RebuildIndexRequest(
    val repository: String
)

public typealias DeleteIndexRequest = RebuildIndexRequest