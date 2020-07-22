package ffc.airsync.api.genogram.lib

import ffc.airsync.utils.getLogger

class GenogramProcessWatcarakorn<P>(
    private val dataFunction: PersonDetailInterface<P>
) : GenogramProcess<P> {
    private val logger = getLogger(this)
    private val util = GenogramUtil<P>()

    override fun process(persons: List<P>): List<P> {
        val preData = util.prepareInformation(persons) {
            object : GenogramUtil.PreFunctionGetData {
                override val pcuCode: String? = dataFunction.getPcuCode(it)
                override val houseNumber: String? = dataFunction.getHouseNumber(it)
                override val name: String? = dataFunction.getName(it)
            }
        }

        val personGroupHouse = util.personGroupHouse(preData)

        TODO("Not yet implemented")
    }
}
