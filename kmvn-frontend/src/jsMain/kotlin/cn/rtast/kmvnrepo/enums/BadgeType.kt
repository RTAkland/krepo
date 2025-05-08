/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/9 00:47
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.enums

enum class BadgeType(val classes: String, val chinese: String) {
    Created("is-info", "已创建"),
    Deleted("is-danger", "已删除"),
    Modified("is-warning", "已修改"),
    Available("is-success", "可用")

}