package ffc.airsync.api.genogram.lib

/**
 * สำหรับดึงข้อมูลที่จำเป็นของคน
 */
interface PersonDetailInterface<P> {
    /**
     * พ่อที่ถูกกำหนดใน entity แล้ว
     */
    val fatherInRelation: P
    fun setFather(person: P, fatherIdCard: String)

    /**
     * แม่ที่ถูกกำหนดใน entity แล้ว
     */
    val motherInRelation: P
    fun setMother(person: P, motherIdCard: String)

    /**
     * แฟนที่ถูกกำหนดใน entity แล้ว
     */
    val mateInRelation: List<P>
    fun addMate(person: P, mateIdCard: String)

    fun getIdCard(person: P): String?
    fun getName(person: P): String?
    fun getPcuCode(person: P): String?
    fun getHouseNumber(person: P): String?
    fun getFatherId(person: P): String?
    fun getFatherName(person: P): String?
    fun getFatherLastName(person: P): String
    fun getMotherId(person: P): String?
    fun getMotherName(person: P): String?
    fun getMotherLastName(person: P): String?
    fun getMateId(person: P): String?
    fun getMateName(person: P): String?
    fun getMateLastName(person: P): String?
    fun getAge(person: P): Int?
    fun getSex(person: P): GENOSEX?
}

enum class GENOSEX { MALE, FEMALE }
