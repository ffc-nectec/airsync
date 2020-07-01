package ffc.airsync

import ffc.airsync.api.analyzer.AnalyzerSyncApi
import ffc.airsync.api.analyzer.AnalyzerSyncServiceApi
import ffc.airsync.api.genogram.GeonogramApi
import ffc.airsync.api.genogram.GeonogramServiceApi
import ffc.airsync.api.healthcare.HealthCareApi
import ffc.airsync.api.healthcare.HealthCareServiceApi
import ffc.airsync.api.homehealthtype.HomeHealthTypeApi
import ffc.airsync.api.homehealthtype.HomeHealthTypeServiceApi
import ffc.airsync.api.house.HouseApi
import ffc.airsync.api.house.HouseServiceApi
import ffc.airsync.api.icd10.Icd10Api
import ffc.airsync.api.icd10.Icd10ServiceApi
import ffc.airsync.api.icd10.SpecialPpApi
import ffc.airsync.api.notification.NotificationApi
import ffc.airsync.api.notification.NotificationServiceApi
import ffc.airsync.api.organization.OrganizationApi
import ffc.airsync.api.organization.OrganizationServiceApi
import ffc.airsync.api.otp.OtpApi
import ffc.airsync.api.otp.OtpServiceApi
import ffc.airsync.api.person.PersonApi
import ffc.airsync.api.person.PersonServiceApi
import ffc.airsync.api.specialPP.SpecialPpServiceApi
import ffc.airsync.api.template.TemplateApi
import ffc.airsync.api.template.TemplateServiceApi
import ffc.airsync.api.user.UserInterface
import ffc.airsync.api.user.UserManage
import ffc.airsync.api.village.VillageApi
import ffc.airsync.api.village.VillageServiceApi
import ffc.entity.Person
import ffc.entity.Village
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.analyze.HealthAnalyzer
import ffc.entity.place.House

val userManage: UserInterface by lazy { UserManage() }
val otpApi: OtpApi by lazy { OtpServiceApi() }
val analyzerSyncApi: AnalyzerSyncApi by lazy { AnalyzerSyncServiceApi() }
val geonogramApi: GeonogramApi by lazy { GeonogramServiceApi() }
val healthCareApi: HealthCareApi by lazy { HealthCareServiceApi() }
val homeHealthTypeApi: HomeHealthTypeApi by lazy { HomeHealthTypeServiceApi() }
val houseApi: HouseApi by lazy { HouseServiceApi() }
val icd10Api: Icd10Api by lazy { Icd10ServiceApi() }
val notificationApi: NotificationApi by lazy { NotificationServiceApi() }
val orgApi: OrganizationApi by lazy { OrganizationServiceApi() }
val personApi: PersonApi by lazy { PersonServiceApi() }
val specialPpApi: SpecialPpApi by lazy { SpecialPpServiceApi() }
val templateApi: TemplateApi by lazy { TemplateServiceApi() }
val villageApi: VillageApi by lazy { VillageServiceApi() }

val relation = arrayListOf<Person>()
val analyzer = hashMapOf<String, HealthAnalyzer>()
val healthCare = arrayListOf<HealthCareService>()
val houses = arrayListOf<House>()
val persons = arrayListOf<Person>()
val villages = arrayListOf<Village>()
