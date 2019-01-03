package ffc.airsync

import ffc.airsync.api.healthcare.healthCare
import ffc.airsync.api.healthcare.healthCareApi
import ffc.airsync.api.homehealthtype.homeHealthTypeApi
import ffc.airsync.api.house.houseApi
import ffc.airsync.api.house.houses
import ffc.airsync.api.icd10.icd10Api
import ffc.airsync.api.icd10.specialPpApi
import ffc.airsync.api.person.persons
import ffc.airsync.api.user.users
import ffc.airsync.api.village.VILLAGELOOKUP
import ffc.airsync.db.DatabaseDao
import ffc.airsync.utils.callApi
import ffc.airsync.utils.printDebug
import ffc.airsync.utils.save
import ffc.entity.copy
import ffc.entity.healthcare.HealthCareService
import ffc.entity.place.House
import ffc.entity.update

class SetupDatabaseWatcher(val dao: DatabaseDao) {

    init {
        databaseWatcher()
    }

    private fun databaseWatcher() {
        val filter = hashMapOf<String, List<String>>().apply {
            put("house", listOf("house", "`house`", "`jhcisdb`.`house`"))
            put("visit", listOf("visit", "`visit`"))
        }

        ffc.airsync.provider.databaseWatcher(
            Config.logfilepath, filter
        ) { tableName, keyWhere ->
            printDebug("Database watcher $tableName $keyWhere")
            when (tableName) {
                "house" -> {
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
                "visit" -> {
                    when (keyWhere.size) {
                        1 -> {
                            val regexType =
                                Regex("""^.*pcucode[` ]+?=[' ]+?(\d+)[' ]+?.*visitno[` ]+?=[' ]+?(\d+)[' ]+?.*$""")
                            val updateWhere = keyWhere.first()
                            val groupValues = regexType.matchEntire(updateWhere)?.groupValues

                            if (groupValues?.size == 3) {
                                val pcucode = groupValues[1]
                                val visitno = groupValues[2].toLongOrNull()
                                visitno
                                    ?.let { visitNo ->
                                        val healthcareDb = getHealthCareFromDb(updateWhere)

                                        val oldmat = healthCare.find {
                                            val checkPcuCode = it.link?.keys?.get("pcucode").toString() == pcucode
                                            val checkVisitNumber =
                                                it.link?.keys?.get("visitno").toString() == visitNo.toString()
                                            checkPcuCode && checkVisitNumber
                                        }

                                        if (oldmat == null) {
                                            val healcareCloud = callApi { healthCareApi.createHealthCare(healthcareDb) }
                                            healthCare.addAll(healcareCloud)
                                        } else {
                                            healthcareDb.forEach { it ->
                                                it.link = oldmat.link
                                                healthCareApi.updateHealthCare(it.copy(oldmat.id))
                                            }
                                        }

                                        healthCare.save()
                                    }
                            }
                        }
                        2 -> {
                            printDebug("Insert where")
                        }
                    }

                    printDebug("visit t:$tableName k:$keyWhere")
                }
            }
        }.start()
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
