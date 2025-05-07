/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/7 19:47
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.enums

import kotlinx.serialization.Serializable

@Serializable
enum class RepositoryStatus(val desc: String) {
    /**
     * 被标记为删除
     */
    Deleted("已删除"),

    /**
     * 已添加但是无法使用
     * 下次启动可用
     */
    Created("已创建"),

    /**
     * 已经可以正常使用
     */
    Available("可用"),

    /**
     * 标记为已被修改但是无法正常使用
     */
    Modified("已修改")
}