package ffc.airsync.hosdetail

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension

class HosDetailJdbi(
    val jdbiDao: MySqlJdbi = MySqlJdbi(null)
) : HosDao {
    override fun get(): HashMap<String, String> {
        return jdbiDao.extension<QueryHosDetail, List<kotlin.collections.HashMap<String, String>>> { get() }[0]
    }
}
