package ffc.airsync.hosdetail

interface HosDao {
    fun get(): HashMap<String, String>
}
