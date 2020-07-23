package ffc.airsync.api.genogram.lib

internal class AlgorithmMapFather<P> {

    interface AddFatherBaseFunc<P> {
        val idCard: String
        val sex: GENOSEX?

        /**
         * พ่อที่ถูกกำหนดมาใน Entity
         */
        val fatherInRelation: P?
        fun setFather(fatherIdCard: String)
    }

    private fun P.addFather(
        father: Person<P>?,
        func: (person: P) -> AddFatherBaseFunc<P>
    ) {
        if (father != null && func(father.person).sex != GENOSEX.FEMALE) {
            if (func(father.person).idCard == func(this).idCard) return
            func(this).setFather(func(father.person).idCard)
        }
    }

    interface MapFatherByIdGetData<P> : AddFatherBaseFunc<P> {
        override val idCard: String
        val fatherInformationIdCard: String?
    }

    /**
     * @param persons คนที่แมพ pcucode, houseNumber, รายการคน
     */
    fun mapFatherById(persons: List<Person<P>>, func: (person: P) -> MapFatherByIdGetData<P>) {
        persons.forEach { person ->
            val focusPerson = person.person
            // check have father
            if (func(focusPerson).fatherInRelation != null) return@forEach

            val fatherInformationIdCard = func(focusPerson).fatherInformationIdCard
            if (!fatherInformationIdCard.isNullOrBlank()) {
                val father = persons.find { func(it.person).idCard == fatherInformationIdCard }
                focusPerson.addFather(father, func)
            }
        }
    }

    interface MapFatherByName<P> : AddFatherBaseFunc<P> {
        val name: String
        val age: Int
        val fatherName: String?
    }

    /**
     * @param persons คนที่แมพ pcucode, houseNumber, รายการคน
     */
    fun mapFatherByName(persons: List<Person<P>>, func: (person: P) -> MapFatherByName<P>) {
        persons.forEach { person ->
            val focusPerson = person.person
            // check have father
            if (func(focusPerson).fatherInRelation != null) return@forEach

            val fatherName = func(focusPerson).fatherName
            if (!fatherName.isNullOrBlank()) {
                val father = persons.find { func(it.person).name == fatherName }
                father?.let {
                    if (func(it.person).age > 18) focusPerson.addFather(father, func)
                }
            }
        }
    }

    interface MapFatherByFirstName<P> : AddFatherBaseFunc<P> {
        val firstName: String
        val lastName: String
        val age: Int
        val fatherFirstName: String?
    }

    /**
     * @param persons คนที่แมพ pcucode, houseNumber, รายการคน
     */
    fun mapFatherByFirstName(persons: List<Person<P>>, func: (person: P) -> MapFatherByFirstName<P>) {
        persons.forEach { person ->
            val focusPerson = person.person
            // check have father
            if (func(focusPerson).fatherInRelation != null) return@forEach

            val fatherFirstName = func(focusPerson).fatherFirstName
            if (!fatherFirstName.isNullOrBlank()) {
                val father = persons.find { func(it.person).firstName == fatherFirstName }
                father?.let {
                    if (func(it.person).lastName == func(focusPerson).lastName && func(it.person).age > 18)
                        focusPerson.addFather(father, func)
                }
            }
        }
    }
}
