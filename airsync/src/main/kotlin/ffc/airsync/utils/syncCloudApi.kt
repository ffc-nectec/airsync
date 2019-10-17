package ffc.airsync.utils

import ffc.airsync.api.autosync.RetofitSyncCloud
import ffc.airsync.api.autosync.SyncCloud
import ffc.airsync.gui
import ffc.airsync.ui.AirSyncGUI.MESSAGE_TYPE.INFO
import ffc.airsync.ui.createMessage

val syncCloud: SyncCloud by lazy {
    RetofitSyncCloud {
        gui.createMessage("SYNC_CLOUD", it, INFO)
    }
}
