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
            val focusPerson = person.person
            if (func(focusPerson).fatherInRelation != null) return@forEach
            val fatherInformationIdCard = func(focusPerson).fatherInformationIdCard
            if (!fatherInformationIdCard.isNullOrBlank()) {
                val father = persons.find { func(it.person).idCard == fatherInformationIdCard }
                addFather(father, func, focusPerson)
            }
        }
    }

    private fun addFather(
        father: Person<P>?,
        func: (person: P) -> MapFatherByIdGetData<P>,
        focusPerson: P
    ) {
        if (father != null && func(father.person).sex != GENOSEX.FEMALE) {
            if (func(father.person).idCard == func(focusPerson).idCard) return
            func(focusPerson).setFather(func(father.person).idCard!!)
        }
    }
}
