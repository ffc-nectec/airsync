package ffc.airsync

import ffc.airsync.api.analyzer.AnalyzerSyncApi
import ffc.airsync.api.analyzer.RetofitAnalyzerSyncApi
import ffc.airsync.api.genogram.GeonogramApi
import ffc.airsync.api.genogram.RetofitGeonogramApi
import ffc.airsync.api.healthcare.HealthCareApi
import ffc.airsync.api.healthcare.RetofitHealthCareApi
import ffc.airsync.api.homehealthtype.HomeHealthTypeApi
import ffc.airsync.api.homehealthtype.RetofitHomeHealthTypeApi
import ffc.airsync.api.house.HouseApi
import ffc.airsync.api.house.RetofitHouseApi
import ffc.airsync.api.icd10.Icd10Api
import ffc.airsync.api.icd10.RetofitIcd10Api
import ffc.airsync.api.icd10.SpecialPpApi
import ffc.airsync.api.notification.NotificationApi
import ffc.airsync.api.notification.RetofitNotificationApi
import ffc.airsync.api.organization.OrganizationApi
import ffc.airsync.api.organization.RetofitOrganizationApi
import ffc.airsync.api.otp.OtpApi
import ffc.airsync.api.otp.RetofitOtpApi
import ffc.airsync.api.person.PersonApi
import ffc.airsync.api.person.RetofitPersonApi
import ffc.airsync.api.specialPP.RetofitSpecialPpApi
import ffc.airsync.api.template.RetofitTemplateApi
import ffc.airsync.api.template.TemplateApi
import ffc.airsync.api.user.RetofitUserApi
import ffc.airsync.api.user.UserApi
import ffc.airsync.api.village.RetofitVillageApi
import ffc.airsync.api.village.VillageApi
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.Village
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.analyze.HealthAnalyzer
import ffc.entity.place.House

val otpApi: OtpApi by lazy { RetofitOtpApi() }
val analyzerSyncApi: AnalyzerSyncApi by lazy { RetofitAnalyzerSyncApi() }
val geonogramApi: GeonogramApi by lazy { RetofitGeonogramApi() }
val healthCareApi: HealthCareApi by lazy { RetofitHealthCareApi() }
val homeHealthTypeApi: HomeHealthTypeApi by lazy { RetofitHomeHealthTypeApi() }
val houseApi: HouseApi by lazy { RetofitHouseApi() }
val icd10Api: Icd10Api by lazy { RetofitIcd10Api() }
val notificationApi: NotificationApi by lazy { RetofitNotificationApi() }
val orgApi: OrganizationApi by lazy { RetofitOrganizationApi() }
val personApi: PersonApi by lazy { RetofitPersonApi() }
val specialPpApi: SpecialPpApi by lazy { RetofitSpecialPpApi() }
val templateApi: TemplateApi by lazy { RetofitTemplateApi() }
val userApi: UserApi by lazy { RetofitUserApi() }
val villageApi: VillageApi by lazy { RetofitVillageApi() }
val relation = arrayListOf<Person>()

val analyzer = hashMapOf<String, HealthAnalyzer>()
val healthCare = arrayListOf<HealthCareService>()
val houses = arrayListOf<House>()
val persons = arrayListOf<Person>()
val users = arrayListOf<User>()
val villages = arrayListOf<Village>()
