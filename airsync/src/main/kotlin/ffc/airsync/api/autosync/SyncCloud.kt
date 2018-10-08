package ffc.airsync.api.autosync

import ffc.entity.Token

interface SyncCloud {
    fun sync(orgId: String, token: Token)
}
