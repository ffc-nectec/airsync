package ffc.airsync.api.healthcare

import ffc.airsync.db.DatabaseDao
import ffc.airsync.gui
import ffc.airsync.healthCare
import ffc.airsync.persons
import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.ui.AirSyncGUI.MESSAGE_TYPE.ERROR
import ffc.airsync.ui.AirSyncGUI.MESSAGE_TYPE.INFO
import ffc.airsync.userManage
import ffc.airsync.utils.ApiLoopException
import ffc.airsync.utils.UploadSpliter
import ffc.airsync.utils.callApi
import ffc.airsync.utils.callApiNoReturn
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.isTempId
import ffc.airsync.utils.save
import ffc.entity.Link
import ffc.entity.System
import ffc.entity.gson.toJson
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.HomeVisit
import org.jdbi.v3.core.statement.UnableToExecuteStatementException

class HealthCareServiceApi : RetofitApi<HealthCareServiceUrl>(HealthCareServiceUrl::class.java), HealthCareApi {

    private val logger by lazy { getLogger(this) }
    override fun clearAndCreateHealthCare(
        healthCare: List<HealthCareService>,
        progressCallback: (Int) -> Unit,
        clearCloud: Boolean
    ): List<HealthCareService> {
        if (clearCloud)
            callApiNoReturn {
                val respond = restService.cleanHealthCare(orgId = organization.id, authkey = tokenBarer).execute()
                val code = respond.code()
                if (code == 500) {
                    throw ApiLoopException("500 Loop ${respond.errorBody()}")
                }

                check(code == 200) { "เกิดข้อผิดพลาดการเยี่ยมบ้าน $code" }
            }
        return _createHealthCare(healthCare, progressCallback)
    }

    override fun createHealthCare(
        healthCare: List<HealthCareService>,
        progressCallback: (Int) -> Unit
    ): List<HealthCareService> {
        val providerId = healthCare.firstOrNull()?.providerId
        val provider = userManage.cloudUser.find { it.id == providerId } ?: {
            gui.createMessageDelay("พบผู้ใช้ใหม่กำลัง Sync...", delay = 30000)
            userManage.sync()
            userManage.cloudUser.find { it.id == providerId }
        }.invoke()
        logger.info("${provider?.name} กำลังออกเยี่ยม")
        return _createHealthCare(healthCare, progressCallback)
    }

    private fun _createHealthCare(
        healthCare: List<HealthCareService>,
        progressCallback: (Int) -> Unit
    ): ArrayList<HealthCareService> {
        val healthCareLastUpdate = arrayListOf<HealthCareService>()
        val fixSizeCake = 100
        val healthCareSize = healthCare.size / fixSizeCake
        logger.info("Create health care total ${healthCare.size}")
        UploadSpliter.upload(fixSizeCake, healthCare) { it, index ->

            val result = callApi {
                restService.unConfirmHealthCareBlock(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    block = index
                ).execute()

                val temp = restService.insertHealthCareBlock(
                    orgId = organization.id,
                    authkey = tokenBarer,
                    healthCare = it,
                    block = index
                )
                val respond = temp.execute()

                if (respond.code() == 201 || respond.code() == 200) {
                    restService.confirmHealthCareBlock(
                        orgId = organization.id,
                        authkey = tokenBarer,
                        block = index
                    ).execute()
                    respond.body() ?: arrayListOf()
                } else {
                    val message =
                        "Error index:$index Exam:${it.first().toJson()} Loop ${respond.code()} ${respond.errorBody()
                            ?.charStream()?.readText()}"
                    throw ApiLoopException(message)
                }
            }
            healthCareLastUpdate.addAll(result)
            if (healthCareSize != 0) // fix bug หากยังไม่มี visit จะสร้างครั้งแรกจะถูกหารด้วย 0 จะ error
                progressCallback(((index * 50) / healthCareSize) + 50)
        }
        return healthCareLastUpdate
    }

    override fun syncHealthCareFromCloud(id: String, dao: DatabaseDao) {
        logger.info { "Sync from object id:$id" }
        val data = restService.getHomeVisit(orgId = organization.id, authkey = tokenBarer, id = id).execute()

        if (data.code() != 200) {
            logger.error("Not success get healthcare code=${data.code()}")
            return
        }
        val healthCareService = data.body()!!
        if (healthCareService.link?.isSynced == true) return
        val pcucode = pcucode

        val patientId = healthCareService.patientId
        val providerId = healthCareService.providerId

        if (patientId.isTempId() || providerId.isTempId()) {
            val ex = IllegalAccessException("Health Care Service provider or patient isTempId ")
            logger.error(ex) { ex.message }
            throw ex
        }

        val provider = userManage.cloudUser.find { it.id == providerId } ?: {
            gui.createMessageDelay("พบผู้ใช้ใหม่กำลัง Sync...", delay = 30000)
            userManage.sync()
            userManage.cloudUser.find { it.id == providerId }
        }.invoke()

        val patient = persons.find { it.id == patientId }

        if (patient == null || provider == null) {
            val message = "ข้อมูลที่ใช้ประกอบการ visit ไม่ครบ " +
                    "ผู้ให้บริการ $providerId:${provider?.name} " +
                    "ผู้ถูกเยี่ยม $patientId:${patient?.name}"
            logger.error {
                message
            }
            gui.createMessageDelay(message, ERROR, 5000)
            return
        }

        logger.debug("partian id ${(patient.link!!.keys["pid"] as String).toLong()}")

        if (healthCareService.link == null) healthCareService.link = Link(System.JHICS)
        try {
            if (healthCareService.link!!.keys.isEmpty()) {
                val message = "เจ้าหน้าที่ ${provider.name} กำลังเยี่ยม\r\n${patient.name}"
                logger.info(message.replace(Regex("""[\r\n]"""), " "))
                gui.createMessageDelay(message, INFO, 60000 * 1L)

                healthCareService.communityServices.forEach {
                    if (it is HomeVisit) {
                        dao.createHomeVisit(
                            it,
                            healthCareService,
                            pcucode,
                            pcucode,
                            patient,
                            provider.name
                        )
                    }
                }
            } else {
                val message = "เจ้าหน้าที่ ${provider.name} อัพเดทข้อมูลการเยี่ยม\r\n${patient.name}"
                logger.info(message.replace(Regex("""[\r\n]"""), " "))
                gui.createMessageDelay(message, INFO, 60000 * 1L)
                healthCareService.communityServices.forEach {
                    if (it is HomeVisit) {
                        dao.updateHomeVisit(
                            it,
                            healthCareService,
                            pcucode,
                            pcucode,
                            patient,
                            provider.name
                        )
                    }
                }
            }

            val result = updateHealthCare(healthCareService)

            healthCare.lock {
                healthCare.removeIf { it.id == result.id }
                healthCare.add(result)
                healthCare.save()
            }

            logger.info { "Result healthcare from cloud ${result.toJson()}" }
        } catch (ex: UnableToExecuteStatementException) {
            val messageErr = ex.message ?: ""
            if (messageErr.startsWith("com.mysql.jdbc.MysqlDataTruncation: Data truncation: Data too long")) {
                gui.createMessageDelay(
                    "ปฏิเสธข้อมูลจากเจ้าหน้าที่ ${provider.name} " +
                            "ที่ลงเยื่ยม ${patient.name} เนื่องจากข้อมูลยาวเกินขีดจำกัด JHCISDB " +
                            "หากแก้ไขข้อมูลจะเข้าได้ตามปกติ"
                )
            }
            throw ex
        }
    }

    override fun updateHealthCare(healthCareService: HealthCareService): HealthCareService {
        val result = callApi {
            restService.updateHomeVisit(
                organization.id, tokenBarer,
                healthCareService.id, healthCareService
            ).execute()
        }
        check(result.code() == 200) { "ไม่สามารถ update visit ได้" }
        return result.body()!!
    }
}
