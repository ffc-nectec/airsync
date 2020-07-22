package ffc.airsync.api.genogram.lib

internal class GenogramUtil<P> {

    /**
     * สำหรับขั้นตอนการเตรียมข้อมูล
     * ทำให้แยก unit test ได้สะดวกขึ้น
     */
    interface PreFunctionGetData {
        val pcuCode: String?
        val houseNumber: String?
        val name: String?
    }

    /**
     * ใช้สำหรับเตรียมข้อมูลที่จำเป็นก่อนการประมวลผล
     * สาเหตุเพราะ ข้อมูลชุดเต็ม มีโครงสร้างที่ซับซ้อนที่ไม่จำเป็น
     * เกินไปสำหรับการสร้าง Genogram ในที่นี้หลักๆ
     * จะใช้เลขบ้าน กับรหัส pcucode เพื่อใช้ในการจัดกลุ่มต่าง ๆ
     * @return ข้อมูลที่ผ่านการจัดเตรียมแล้ว
     */
    fun prepareInformation(
        persons: List<P>,
        preFunctionGetData: (person: P) -> PreFunctionGetData
    ): List<Person<P>> {
        return persons.mapNotNull {
            // it.link?.keys?.get("pcucodeperson")?.toString()
            val pcuCode = preFunctionGetData(it).pcuCode
            // it.link?.keys?.get("hcode")?.toString()
            val houseNumber = preFunctionGetData(it).houseNumber
            val name = preFunctionGetData(it).name
            when {
                pcuCode == null -> null
                houseNumber == null -> null
                else -> {
                    Person(pcuCode, houseNumber, it, name)
                }
            }
        }
    }

    /**
     * ทำการค้นหา คนที่อยู่ในบ้าน
     * @return แมพ pcucode, houseNumber, รายการคน
     */
    fun personGroupHouse(persons: List<Person<P>>): Map<Pair<String, String>, List<Person<P>>> {
        val setData = HashSet<Pair<String, String>>()
        persons.forEach { setData.add(it.pcucode to it.houseNumber) }

        return setData.mapNotNull { key ->
            key to persons.filter { it.pcucode == key.first && it.houseNumber == key.second }
        }.toMap()
    }
}
