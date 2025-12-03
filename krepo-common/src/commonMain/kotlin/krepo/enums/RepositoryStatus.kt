/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/7 19:47
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.enums

import kotlinx.serialization.Serializable

@Serializable
public enum class RepositoryStatus(public val desc: String) {
    Deleted("Deleted"), Created("Created"),
    Available("Available"), Modified("Modified")
}