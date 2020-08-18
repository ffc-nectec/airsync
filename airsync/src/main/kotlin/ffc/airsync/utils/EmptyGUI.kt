/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package ffc.airsync.utils

import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.KEY
import ffc.airsync.ui.LookPcuCode

class EmptyGUI : AirSyncGUI {
    override fun cretaeItemList(data: Pair<KEY, Any>) {
    }

    override fun createMessageDelay(message: String, type: AirSyncGUI.MESSAGE_TYPE, delay: Long) {
    }

    override fun remove(key: KEY) {
    }

    override fun hideWindows() {
    }

    override fun showWIndows() {
    }

    override fun switchhHideShow() {
    }

    override fun setHeader(string: String) {
    }

    override fun setLocation(x: Int, y: Int) {
    }

    override fun createRightClick(x: Int, y: Int) {
    }

    override fun hideRightClick() {
    }

    override var enableSyncButton: Boolean
        get() = false
        set(value) {}

    override var enableOtp: Boolean
        get() = false
        set(value) {}

    override var callGetOtp: () -> String
        get() = { "" }
        set(value) {}

    override fun setLookPcuCode(pcuCode: LookPcuCode) {
    }

    override fun setCallConfirmUninstall(callback: () -> Unit) {
    }
}
