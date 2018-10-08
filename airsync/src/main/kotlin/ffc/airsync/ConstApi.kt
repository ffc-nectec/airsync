package ffc.airsync

import ffc.airsync.api.healthcare.HealthCareApi
import ffc.airsync.api.healthcare.RetofitHealthCareApi
import ffc.airsync.api.house.HouseApi
import ffc.airsync.api.house.RetofitHouseApi
import ffc.airsync.api.notification.NotificationApi
import ffc.airsync.api.notification.RetofitNotificationApi
import ffc.airsync.api.organization.OrganizationApi
import ffc.airsync.api.organization.RetofitOrganizationApi
import ffc.airsync.api.person.PersonApi
import ffc.airsync.api.person.RetofitPersonApi
import ffc.airsync.api.user.RetofitUserApi
import ffc.airsync.api.user.UserApi

val orgApi: OrganizationApi by lazy { RetofitOrganizationApi() }
val healthCareApi: HealthCareApi by lazy { RetofitHealthCareApi() }
val houseApi: HouseApi by lazy { RetofitHouseApi() }
val notificationApi: NotificationApi by lazy { RetofitNotificationApi() }
val personApi: PersonApi by lazy { RetofitPersonApi() }
val userApi: UserApi by lazy { RetofitUserApi() }
