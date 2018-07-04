package ffc.airsync.provider

import ffc.airsync.client.webservice.module.FirebaseMessage
import ffc.airsync.localweb.FFCApiClient
import ffc.airsync.notification.Notification
import ffc.airsync.ui.AirSyncUi

fun airSyncUiModule(): AirSyncUi = FFCApiClient("127.0.0.1", 8081)

fun notifactionModule(): Notification = FirebaseMessage.getInstance()
