package ffc.airsync.utils

/**
 * ใช้สำหรับช่วยในการสร้างชุดคำสั่ง สำหรับการตรวจสอบการ update ข้อมูลใหม่ขึ้นไป cloud
 * @param localData ข้อมูลที่อยู่บน local ในที่นี้จะเป็นข้อมูลจาก jhcis
 * @param cloudData ข้อมูลที่อยู่บน cloud
 * @param checkItem กฏการตรวจสอบว่า วัตถุ local และ cloud เหมือนกัน
 * @param updateNewData ถ้าเกิดมีข้อมูลใหม่จะ update จะให้ทำอย่างไร
 */
fun <T> checkDataUpdate(
    localData: List<T>,
    cloudData: List<T>,
    checkItem: (local: T, cloud: T) -> Boolean,
    updateNewData: (newData: List<T>) -> Unit
) {
    val newData = arrayListOf<T>()
    localData.forEach { local ->
        val cloud = cloudData.find { checkItem(local, it) }
        if (cloud == null)
            newData.add(local)
    }

    if (newData.isNotEmpty()) {
        updateNewData(newData)
    }
}
