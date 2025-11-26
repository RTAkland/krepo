/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/9 00:47
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.enums

enum class BadgeType(val classes: String, val chinese: String) {
    Created("is-info", "Created"),
    Deleted("is-danger", "Deleted"),
    Modified("is-warning", "Modified"),
    Available("is-success", "Available")

}