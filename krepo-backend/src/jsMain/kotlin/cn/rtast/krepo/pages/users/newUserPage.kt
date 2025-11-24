/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/5 21:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.krepo.pages.users

import cn.rtast.krepo.backendVersion
import cn.rtast.krepo.components.errorToast
import cn.rtast.krepo.components.infoToast
import cn.rtast.krepo.components.showDialog
import cn.rtast.krepo.components.warningToast
import cn.rtast.krepo.coroutineScope
import cn.rtast.krepo.util.auth
import cn.rtast.krepo.util.file.checkSession
import cn.rtast.krepo.util.httpRequest
import cn.rtast.krepo.util.jsonContentType
import cn.rtast.krepo.util.setBody
import cn.rtast.krepo.util.string.fromJson
import cn.rtast.krepo.util.string.validateEmail
import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.launch
import krepo.entity.User

fun RenderContext.newUserPage() {
    checkSession {
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
                        span("icon is-small is-left mr-2") { i("fa-solid fa-user") {} }
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
                        span("icon is-small is-left mr-2") { i("fa-solid fa-envelope") {} }
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
                        span("icon is-small is-key mr-2") { i("fa-solid fa-user") {} }
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
                        button("button is-info") {
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
                    coroutineScope.launch {
                        val result = httpRequest(backendVersion.CREATE_USER)
                            .auth().acceptJson().jsonContentType()
                            .setBody(requestBody).post().body().fromJson<Map<String, String>>()
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
