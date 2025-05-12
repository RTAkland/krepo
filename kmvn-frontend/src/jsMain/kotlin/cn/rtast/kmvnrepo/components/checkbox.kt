/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/12 16:21
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.components

import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.checked
import dev.fritz2.core.type
import dev.fritz2.core.values

fun RenderContext.prettyCheckbox(labelText: String, store: Store<Boolean>) {
    div("field mt-3") {
        div("control") {
            label("checkbox is-flex is-align-items-center") {
                input("mr-2") {
                    type("checkbox")
                    checked(store.data)
                    changes.values() handledBy { store.update(it.toBoolean()) }
                }
                +labelText
            }
        }
    }
}