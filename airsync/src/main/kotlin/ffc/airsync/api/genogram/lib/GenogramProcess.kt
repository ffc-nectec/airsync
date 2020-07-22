package ffc.airsync.api.genogram.lib

/**
 * สำหรับประมวลผล Genogram
 */
interface GenogramProcess<P> {
    /**
     * @param persons รายการคน
     * @param dataFunction ฟังชั่นสำหรับดึงข้อมูลคนที่จำเป็นสำหรับการจับคู่ Genogram
     * @return คนที่ใส่ความสัมพันธุ์แล้ว
     */
    fun process(persons: List<P>): List<P>
}
