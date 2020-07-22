package ffc.airsync.api.genogram.lib

internal class Algorithm<P> {

    interface MapFatherByIdGetData<P> {
        val idCard: String?
        val sex: GENOSEX?

        /**
         * idCard พ่อที่กำหนดมาใน information
         * หรือ เข้าใจอีกอย่างว่า มาจากฐานดิบๆ
         */
        val fatherInformationIdCard: String?

        /**
         * พ่อที่ถูกกำหนดมาใน Entity
         */
        val fatherInRelation: P?
        fun setFather(fatherIdCard: String)
    }

    /**
     * @param persons คนที่แมพ pcucode, houseNumber, รายการคน
     */
    fun mapFatherById(persons: List<Person<P>>, func: (person: P) -> MapFatherByIdGetData<P>) {
        persons.forEach { person ->
            val rawPerson = person.person
            if (func(rawPerson).fatherInRelation != null) return@forEach
            val fatherInformationIdCard = func(rawPerson).fatherInformationIdCard
            if (!fatherInformationIdCard.isNullOrBlank()) {
                val father = persons.find { func(it.person).idCard == fatherInformationIdCard }
                if (father != null && func(father.person).sex != GENOSEX.FEMALE) {
                    if (func(father.person).idCard == func(rawPerson).idCard) return@forEach
                    func(rawPerson).setFather(func(father.person).idCard!!)
                }
            }
        }
    }
}
