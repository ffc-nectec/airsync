package ffc.airsync.api.pidvola

import ffc.entity.Person
import ffc.entity.User
import ffc.entity.place.House

/**
 * ตัวประมวลผล อสม.
 */
interface VolaProcess {

    /**
     * ประมวลผลจับคู่ User อสม. กับ person ว่ามี pid อะไร
     * เพื่อที่จะนำไปจับคู่กับ pidvola ต่อ
     * @param users รายการ user
     * @param persons รายการคน
     * @return รายการ user อสม. ที่ใส่ pid ใน bundle แล้ว
     */
    fun processUser(users: List<User>, persons: List<Person>): List<User>

    /**
     * ประมวลผล อสม. แล้วส่งค่ากลับคืนมาเป็นบ้านที่ update
     * @param houses รายการบ้านบน cloud ทั้งหมด
     * @param users รายการ user อสม.
     * @return รายการบ้านที่ต้อง update ขึ้น cloud เพราะมี อสม. ดูแล
     */
    fun processHouse(houses: List<House>, users: List<User>): List<House>
}
