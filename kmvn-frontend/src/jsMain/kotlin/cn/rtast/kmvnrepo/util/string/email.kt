/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 21:16
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.util.string

import cn.rtast.kmvnrepo.components.warningToast
import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store

private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

fun RenderContext.validateEmail(store: Store<String>): Boolean {
    return if (store.current.isBlank()) {
        warningToast("邮箱不能为空")
        false
    } else if (!emailRegex.matches(store.current)) {
        warningToast("邮箱格式不正确")
        false
    } else true
}
