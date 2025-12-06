/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:30 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages.users

import dev.fritz2.core.*
import kotlinx.browser.window
import krepo.backendVersion
import krepo.components.errorToast
import krepo.util.img.autoSvg
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.components.warningToast
import krepo.coroutineScope
import krepo.entity.user.User
import krepo.util.auth
import krepo.util.byte.fromProtoBuf
import krepo.util.file.checkPermission
import krepo.util.httpRequest
import krepo.util.launchJob
import krepo.util.setOctetBody
import krepo.util.string.validateEmail

fun RenderContext.CreateUserPage() {
    checkPermission {
        val username = storeOf("")
        val email = storeOf("")
        val password = storeOf("")
        val showCreateUserDialog = storeOf(false)
        div("container") {
            h3("title is-3") { +"Create a new user" }
            inlineStyle("max-width: 50%;")
            div("box") {
                div("field") {
                    label("label") { +"Username" }
                    div("control has-icons-left") {
                        span("icon is-small is-left mr-2") { autoSvg("fa-user", "") }
                        input("input") {
                            type("text")
                            placeholder("Fill in the username here")
                            value(username.current)
                            changes.values() handledBy username.update
                        }
                    }
                }
                div("field") {
                    label("label") { +"Email" }
                    div("control has-icons-left") {
                        span("icon is-small is-left mr-2") { autoSvg("fa-envelope", "") }
                        input("input") {
                            type("email")
                            placeholder("Fill in the email here")
                            value(email.current)
                            changes.values() handledBy email.update
                        }
                    }
                }
                div("field") {
                    label("label") { +"Password" }
                    div("control has-icons-left") {
                        span("icon is-small is-key mr-2") { autoSvg("fa-key", "") }
                        input("input") {
                            type("password")
                            placeholder("Fill in the password here")
                            value(password.current)
                            changes.values() handledBy password.update
                        }
                    }
                }
                div("is-grouped is-right buttons") {
                    div("control") {
                        button("button") {
                            autoSvg("fa-check")
                            +"Confirm"
                            clicks handledBy { showCreateUserDialog.update(true) }
                        }
                    }
                }
            }
        }
        showDialog(showCreateUserDialog, "Create User", "Do you want to create the user?", {}) {
            if (username.current.isBlank() || password.current.isBlank() || email.current.isBlank()) {
                warningToast("Please fill in all fields")
            } else {
                if (validateEmail(email)) {
                    val requestBody = User(username.current, email.current, password.current)
                    coroutineScope.launchJob {
                        val result = httpRequest(backendVersion.CREATE_USER)
                            .auth().setOctetBody(requestBody).post()
                            .arrayBuffer().fromProtoBuf<Map<String, String>>()
                        val code = result["code"]!!.toInt()
                        if (code == 200) {
                            infoToast("User created")
                            window.location.href = "/#/user/manage"
                        } else {
                            errorToast("Failed to create user! ${result["message"]}")
                        }
                    }
                    infoToast("Creating...")
                }
            }
        }
    }
}
