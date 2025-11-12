/*
 * Copyright © 2025 RTAkland
 * Date: 11/12/25, 9:17 AM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package cn.rtast.krepo.azure.entity.res

enum class RepositoryStatus(val desc: String) {
    Deleted("已删除"),
    Created("已创建"),
    Available("可用"),
    Modified("已修改")
}