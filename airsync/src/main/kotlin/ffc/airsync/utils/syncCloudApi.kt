package ffc.airsync.utils

import ffc.airsync.api.autosync.RetofitSyncCloud
import ffc.airsync.api.autosync.SyncCloud

val syncCloud: SyncCloud by lazy { RetofitSyncCloud() }
