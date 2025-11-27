/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:24 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package krepo.util.string

import dev.fritz2.core.Store
import krepo.components.warningToast

private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")

fun validateEmail(store: Store<String>): Boolean {
    return if (store.current.isBlank()) {
        warningToast("Email must not be empty!")
        false
    } else if (!emailRegex.matches(store.current)) {
        warningToast("The email format is incorrect!")
        false
    } else true
}