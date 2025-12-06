/*
 * Copyright © 2025 RTAkland
 * Date: 11/26/25, 1:29 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

@file:Suppress("FunctionName")

package krepo.pages.settings

import dev.fritz2.core.*
import kotlinx.browser.window
import krepo.backendVersion
import krepo.components.infoToast
import krepo.components.showDialog
import krepo.coroutineScope
import krepo.entity.FrontendConfig
import krepo.frontendConfig
import krepo.util.*
import krepo.util.file.checkPermission
import krepo.util.img.autoSvg

fun RenderContext.CommonSettingPage() {
    checkPermission {
        val pageTitleStore = storeOf(frontendConfig.pageTitle)
        val icpLicenseStore = storeOf(frontendConfig.icpLicense)
        val descriptionStore = storeOf(frontendConfig.description)
        val copyrightStore = storeOf(frontendConfig.copyright)
        val showSubmitSettingDialog = storeOf(false)
        val showResetFrontConfigDialog = storeOf(false)
        coroutineScope.launchJob {
            div("container") {
                inlineStyle("max-width: 60%;")
                h3("title is-3") { +"Update frontend settings" }
                div("box") {
                    div("field") {
                        label("label") { +"Title" }
                        div("control") {
                            input("input") {
                                type("text")
                                placeholder("Fill in the frontend title here")
                                value(pageTitleStore.current)
                                changes.values() handledBy pageTitleStore.update
                            }
                        }
                    }
                    div("field") {
                        label("label") { +"Description" }
                        div("control") {
                            input("input") {
                                type("text")
                                placeholder("Fill in the frontend description here")
                                value(descriptionStore.current ?: "")
                                changes.values() handledBy descriptionStore.update
                            }
                        }
                    }
                    div("field") {
                        label("label") { +"Copyright" }
                        div("control") {
                            input("input") {
                                type("text")
                                placeholder("Fill in the frontend copyright here")
                                value(copyrightStore.current)
                                changes.values() handledBy copyrightStore.update
                            }
                        }
                    }
                    div("field") {
                        label("label") { +"ICP License" }
                        div("control") {
                            input("input") {
                                type("text")
                                placeholder("Fill in the frontend ICP License here")
                                value(icpLicenseStore.current ?: "")
                                changes.values() handledBy icpLicenseStore.update
                            }
                        }
                    }
                    div("is-grouped is-right buttons") {
                        div("control") {
                            button("button") {
                                autoSvg("fa-rotate-left")
                                +"Reset"
                                disabled(true)
                                clicks handledBy { showResetFrontConfigDialog.update(true) }
                            }
                        }
                        div("control") {
                            button("button") {
                                autoSvg("fa-floppy-disk")
                                +"Save"
                                disabled(true)
                                clicks handledBy { showSubmitSettingDialog.update(true) }
                            }
                        }
                    }
                }
            }
        }
        showDialog(showSubmitSettingDialog, "Saving the changes", "Do you want to save these changes?", {}) {
            val pageTitle = pageTitleStore.current
            val icpLicense = icpLicenseStore.current
            val description = descriptionStore.current
            val copyright = copyrightStore.current
            coroutineScope.launchJob {
                httpRequest(backendVersion.MODIFY_FRONTEND_CONFIG).auth()
                    .setOctetBody(FrontendConfig(pageTitle, icpLicense, description, copyright, false, null))
                    .put()
                infoToast("Saved!")
                window.location.reload()
            }
        }
        showDialog(showResetFrontConfigDialog, "Reset", "Do you want to reset the frontend settings?", {}) {
            coroutineScope.launchJob {
                httpRequest(backendVersion.RESET_FRONTEND_CONFIG)
                    .auth().put()
                infoToast("Rested!")
                window.location.reload()
            }
        }
    }
}