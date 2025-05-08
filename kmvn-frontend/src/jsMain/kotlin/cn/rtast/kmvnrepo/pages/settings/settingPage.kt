/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/6 14:41
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package cn.rtast.kmvnrepo.pages.settings

import cn.rtast.kmvnrepo.components.infoToast
import cn.rtast.kmvnrepo.components.navbar
import cn.rtast.kmvnrepo.components.pageFooter
import cn.rtast.kmvnrepo.components.showDialog
import cn.rtast.kmvnrepo.coroutineScope
import cn.rtast.kmvnrepo.entity.FrontendConfig
import cn.rtast.kmvnrepo.frontendConfig
import cn.rtast.kmvnrepo.util.auth
import cn.rtast.kmvnrepo.util.file.checkSession
import cn.rtast.kmvnrepo.util.httpRequest
import cn.rtast.kmvnrepo.util.jsonContentType
import cn.rtast.kmvnrepo.util.setBody
import dev.fritz2.core.*
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun RenderContext.settingPage() {
    checkSession {
        val pageTitleStore = storeOf(frontendConfig.pageTitle)
        val icpLicenseStore = storeOf(frontendConfig.icpLicense)
        val descriptionStore = storeOf(frontendConfig.description)
        val copyrightStore = storeOf(frontendConfig.copyright)
        val showSubmitSettingDialog = storeOf(false)
        val showResetFrontConfigDialog = storeOf(false)
        coroutineScope.launch {
            section("section") {
                div("container") {
                    div("box mx-auto") {
                        h1("title is-4") { +"前端配置" }
                        div("columns is-multiline") {
                            div("column is-full") {
                                div("field") {
                                    label("label") { +"页面标题" }
                                    div("control") {
                                        input("input") {
                                            type("text")
                                            value(pageTitleStore.data)
                                            changes.values() handledBy pageTitleStore.update
                                        }
                                    }
                                }
                            }
                            div("column is-full") {
                                div("field") {
                                    label("label") {
                                        +"ICP备案"
                                        div("control") {
                                            input("input") {
                                                type("text")
                                                value(icpLicenseStore.current ?: "")
                                                changes.values() handledBy icpLicenseStore.update
                                            }
                                        }
                                    }
                                }
                            }
                            div("column is-full") {
                                div("field") {
                                    label("label") {
                                        +"网站描述"
                                    }
                                    div("control") {
                                        input("input") {
                                            type("text")
                                            value(descriptionStore.current ?: "")
                                            changes.values() handledBy descriptionStore.update
                                        }
                                    }
                                }
                            }
                            div("column is-full") {
                                div("field") {
                                    label("label") { +"版权信息" }
                                    div("control") {
                                        input("input") {
                                            type("text")
                                            value(copyrightStore.current)
                                            changes.values() handledBy copyrightStore.update
                                        }
                                    }
                                }
                            }
                            div("field is-grouped mt-4") {
                                div("control") {
                                    button("button is-primary") {
                                        +"保存设置"
                                        clicks handledBy { showSubmitSettingDialog.update(true) }
                                    }
                                }
                                div("control") {
                                    button("button is-danger") {
                                        +"重置为默认"
                                        clicks handledBy { showResetFrontConfigDialog.update(true) }
                                    }
                                }
                            }
                            hr {}
                        }
                    }
                }
            }
        }
        showDialog(showSubmitSettingDialog, "保存更改", "是否要保存更改?", {}) {
            val pageTitle = pageTitleStore.current
            val icpLicense = icpLicenseStore.current
            val description = descriptionStore.current
            val copyright = copyrightStore.current
            coroutineScope.launch {
                httpRequest("/@/api/config/frontend")
                    .auth().acceptJson().jsonContentType()
                    .setBody(FrontendConfig(pageTitle, icpLicense, description, copyright))
                    .put()
                infoToast("更改成功!")
                window.location.reload()
            }
        }
        showDialog(showResetFrontConfigDialog, "重置设置", "是否要重置前端设置?", {}) {
            coroutineScope.launch {
                httpRequest("/@/api/config/frontend/reset")
                    .auth().acceptJson().jsonContentType()
                    .put()
                infoToast("重置成功!")
                window.location.reload()
            }
        }
    }
}