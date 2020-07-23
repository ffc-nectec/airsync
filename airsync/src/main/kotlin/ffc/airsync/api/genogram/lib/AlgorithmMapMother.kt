package ffc.airsync.api.genogram.lib

internal class AlgorithmMapMother<P> {

    interface AddMotherBaseFunc<P> {
        val idCard: String
        val sex: GENOSEX?

        /**
         * พ่อที่ถูกกำหนดมาใน Entity
         */
        val motherInRelation: P?
        fun setMother(fatherIdCard: String)
    }

    private fun P.addMother(
        mother: Person<P>?,
        func: (person: P) -> AddMotherBaseFunc<P>
    ) {
        if (mother != null && func(mother.person).sex != GENOSEX.MALE) {
            if (func(mother.person).idCard == func(this).idCard) return
            func(this).setMother(func(mother.person).idCard)
        }
    }

    interface MapMotherByIdGetData<P> : AddMotherBaseFunc<P> {
        override val idCard: String
        val motherInformationIdCard: String?
    }

    /**
     * @param persons คนที่แมพ pcucode, houseNumber, รายการคน
     */
    fun mapMotherById(persons: List<Person<P>>, func: (person: P) -> MapMotherByIdGetData<P>) {
        persons.forEach { person ->
            val focusPerson = person.person
            // check have father
            if (func(focusPerson).motherInRelation != null) return@forEach

            val motherInformationIdCard = func(focusPerson).motherInformationIdCard
            if (!motherInformationIdCard.isNullOrBlank()) {
                val mother = persons.find { func(it.person).idCard == motherInformationIdCard }
                focusPerson.addMother(mother, func)
            }
        }
    }

    interface MapMotherByName<P> : AddMotherBaseFunc<P> {
        val name: String
        val age: Int
        val motherName: String?
    }

    /**
     * @param persons คนที่แมพ pcucode, houseNumber, รายการคน
     */
    fun mapMotherByName(persons: List<Person<P>>, func: (person: P) -> MapMotherByName<P>) {
        persons.forEach { person ->
            val focusPerson = person.person
            // check have father
            if (func(focusPerson).motherInRelation != null) return@forEach

            val motherName = func(focusPerson).motherName
            if (!motherName.isNullOrBlank()) {
                val mother = persons.find { func(it.person).name == motherName }
                mother?.let {
                    if (func(it.person).age > 13) focusPerson.addMother(mother, func)
                }
            }
        }
    }

    interface MapMotherByFirstName<P> : AddMotherBaseFunc<P> {
        val firstName: String
        val lastName: String
        val age: Int
        val motherFirstName: String?
    }

    /**
     * @param persons คนที่แมพ pcucode, houseNumber, รายการคน
     */
    fun mapMotherByFirstName(persons: List<Person<P>>, func: (person: P) -> MapMotherByFirstName<P>) {
        persons.forEach { person ->
            val focusPerson = person.person
            // check have father
            if (func(focusPerson).motherInRelation != null) return@forEach

            val motherFirstName = func(focusPerson).motherFirstName
            if (!motherFirstName.isNullOrBlank()) {
                val mother = persons.find { func(it.person).firstName == motherFirstName }
                mother?.let {
                    if (func(it.person).lastName == func(focusPerson).lastName && func(it.person).age > 13)
                        focusPerson.addMother(mother, func)
                }
            }
        }
    }
}
