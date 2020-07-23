package ffc.airsync.api.genogram.lib

/**
 * สำหรับดึงข้อมูลที่จำเป็นของคน
 */
interface PersonDetailInterface<P> {
    /**
     * พ่อที่ถูกกำหนดใน entity แล้ว
     */
    fun getFatherInRelation(person: P): P
    fun setFather(person: P, fatherIdCard: String)

    /**
     * แม่ที่ถูกกำหนดใน entity แล้ว
     */
    fun getMotherInRelation(person: P): P
    fun setMother(person: P, motherIdCard: String)

    /**
     * แฟนที่ถูกกำหนดใน entity แล้ว
     */
    fun getMateInRelation(person: P): List<P>
    fun addMate(person: P, mateIdCard: String)

    fun getIdCard(person: P): String
    fun getFirstName(person: P): String
    fun getLastName(person: P): String
    fun getPcuCode(person: P): String?
    fun getHouseNumber(person: P): String?
    fun getFatherInformationId(person: P): String?
    fun getFatherFirstName(person: P): String?
    fun getFatherLastName(person: P): String?
    fun getMotherInformationId(person: P): String?
    fun getMotherFirstName(person: P): String?
    fun getMotherLastName(person: P): String?
    fun getMateInformationId(person: P): String?
    fun getMateFirstName(person: P): String?
    fun getMateLastName(person: P): String?
    fun getAge(person: P): Int?
    fun getSex(person: P): GENOSEX?
}

enum class GENOSEX { MALE, FEMALE }
