/*
 * Copyright (c) 2018 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.provider

import ffc.airsync.JdbiDao
import ffc.airsync.client.webservice.module.FirebaseMessage
import ffc.airsync.db.DatabaseDao
import ffc.airsync.db.DatabaseWatcherDao
import ffc.airsync.gui.drawable.AirSyncGUIController
import ffc.airsync.localweb.FFCApiClient
import ffc.airsync.notification.Notification
import ffc.airsync.ui.AirSyncGUI
import ffc.airsync.ui.AirSyncUi
import th.`in`.ffc.airsync.logreader.LogReader

fun airSyncUiModule(): AirSyncUi = FFCApiClient("127.0.0.1", 8081)

fun notificationModule(): Notification = FirebaseMessage.instant

fun databaseDaoModule(
    dbHost: String,
    dbPort: String,
    dbName: String,
    dbUsername: String,
    dbPassword: String
): DatabaseDao = JdbiDao(dbHost, dbPort, dbName, dbUsername, dbPassword)

fun databaseWatcher(
    filepath: String,
    tableMaps: Map<String, List<String>>,
    onLogInput: (tableName: String, keyWhere: List<String>) -> Unit
): DatabaseWatcherDao = LogReader(filepath, tableMaps = tableMaps, onLogInput = onLogInput)

fun createArisyncGui(): AirSyncGUI = AirSyncGUIController()
