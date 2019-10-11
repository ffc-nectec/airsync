package ffc.airsync

import ffc.airsync.api.village.VILLAGELOOKUP
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.callApi
import ffc.airsync.utils.getLogger
import ffc.airsync.utils.jobFFC
import ffc.airsync.utils.save
import ffc.entity.copy
import ffc.entity.healthcare.HealthCareService
import ffc.entity.place.House
import ffc.entity.update

class SetupDatabaseWatcher(val dao: DatabaseDao) {

    init {
        databaseWatcher()
    }

    private val logger by lazy { getLogger(this) }

    private fun databaseWatcher() {
        /**
         * รูปแบบคือ ชื่อ , list รูปแบบ table string ที่จะให้ตรวจจับ
         * แต่ยังไม่ได้แยกว่า เป็น select, update, delete
         */
        val tablesPattern = hashMapOf<String, List<String>>().apply {
            put("house", listOf("house", "`house`", "`jhcisdb`.`house`"))
            put("visit", listOf("visit", "`visit`", " visit ", "visitdrug", "visithomehealthindividual"))
        }

        ffc.airsync.provider.databaseWatcher(
            Config.logfilepath, tablesPattern, { isShutdown }
        ) { tableName, keyWhere ->
            logger.info("Database watcher $tableName $keyWhere")
            when (tableName) {
                "house" -> houseEvent(keyWhere)
                "visit" -> visitEvent(keyWhere)
            }
        }.start()
    }

    private fun houseEvent(keyWhere: List<String>) {
        jobFFC {
            if (keyWhere.size == 1) {
                val house = dao.getHouse(VILLAGELOOKUP, keyWhere.first())
                house.forEach {
                    try {
                        val houseSync = findHouseWithKey(it)
                        houseSync.update(it.timestamp) {
                            road = it.road
                            no = it.no
                            location = it.location
                            link!!.isSynced = true
                        }

                        houseApi.syncHouseToCloud(houseSync)
                    } catch (ignore: NullPointerException) {
                    }
                }
            }
        }
    }

    /**
     * เมื่อเกิดเหตุการที่ตาราง visit ให้ทำ
     * @param keyWhere ค่า where จาก sql
     */
    private fun visitEvent(keyWhere: List<String>) {
        jobFFC {
            when (keyWhere.size) {
                1 -> { // พฤติกรรมของ update where
                    val sqlWhere = keyWhere.first()
                    val pattern = listOf(
                        Regex("""^.*pcucode[` ]?='?(\d+)'?.*visitno[` ]?='?(\d+)'?.*$""")
                    )
                    val visitMatchValue: (List<Regex>, String) -> List<String> =
                        { reg, where ->
                            var output = listOf<String>()
                            reg.forEach {
                                val match = it.matchEntire(where)?.groupValues
                                if (match?.size == 3 && output.isEmpty()) {
                                    output = match
                                }
                            }
                            output
                        }
                    val aggregate = visitMatchValue(pattern, sqlWhere)
                    if (aggregate.size == 3) {
                        val pcucode = aggregate[1]
                        val visitno = aggregate[2].toLongOrNull()
                        visitToCloud(pcucode, visitno, sqlWhere)
                    }
                }
                2 -> { // พฤติกรรม Insert
                    val map = keyWhereIsInsertQueryToMapKeyValue(keyWhere)
                    val pcucode = map["pcucode"]
                    val visitno = map["visitno"]?.toLongOrNull()

                    if (pcucode != null) {
                        val sqlWhere = "pcucode ='$pcucode' AND visitno ='$visitno'"
                        visitToCloud(pcucode, visitno, sqlWhere)
                    }
                }
                else -> {
                    logger.warn("Insert where size ${keyWhere.size}")
                }
            }
            logger.debug("visit k:$keyWhere")
        }
    }

    private fun keyWhereIsInsertQueryToMapKeyValue(keyWhere: List<String>): HashMap<String, String> {
        val sqlSetKey = keyWhere.first().replace(Regex("""['`]"""), "").split(',')
        val sqlValueKey = keyWhere.last().replace(Regex("""['`]"""), "").split(',')
        val map = hashMapOf<String, String>()
        if (sqlSetKey.size == sqlValueKey.size) {
            sqlSetKey.forEachIndexed { index, key ->
                map[key] = sqlValueKey[index]
            }
        }
        return map
    }

    private fun visitToCloud(pcucode: String, visitno: Long?, updateWhere: String) {
        logger.info("Create new visit to cloud")
        jobFFC {
            visitno?.let { visitNo ->
                val healthcareDb = getHealthCareFromDb(updateWhere)

                val oldHealthcare = healthCare.find {
                    val checkPcuCode = it.link?.keys?.get("pcucode").toString() == pcucode
                    val checkVisitNumber =
                        it.link?.keys?.get("visitno").toString() == visitNo.toString()
                    checkPcuCode && checkVisitNumber
                }

                if (oldHealthcare == null) {
                    val healcareCloud = callApi { healthCareApi.createHealthCare(healthcareDb) {} }
                    healthCare.addAll(healcareCloud)
                } else {
                    healthcareDb.forEach { it ->
                        it.link = oldHealthcare.link
                        it.link?.isSynced = true
                        healthCareApi.updateHealthCare(it.copy(oldHealthcare.id))
                    }
                }

                healthCare.save()
            }
        }
    }

    private fun getHealthCareFromDb(updateWhere: String): List<HealthCareService> {
        return dao.getHealthCareService(
            lookupPatientId = { pid ->
                persons.find { it.link!!.keys["pid"] == pid }?.id ?: ""
            },
            lookupProviderId = { name ->
                (users.find { it.name == name } ?: users.last()).id
            },
            lookupDisease = { icd10 -> icd10Api.lookup(icd10) },
            lookupServiceType = { serviceId -> homeHealthTypeApi.lookup(serviceId) },
            lookupSpecialPP = { ppCode -> specialPpApi.lookup(ppCode.trim()) },
            whereString = updateWhere
        )
    }

    private fun findHouseWithKey(house: House): House {
        val houseFind = houses.find {
            house.link!!.keys["pcucode"] == it.link!!.keys["pcucode"] &&
                    house.link!!.keys["hcode"] == it.link!!.keys["hcode"]
        }

        return houseFind ?: throw NullPointerException("ค้นหาไม่พบบ้าน")
    }
}
