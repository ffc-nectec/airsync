package ffc.airsync.hosdetail

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import javax.sql.DataSource

class HosDetailJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), HosDao {
    override fun get(): HashMap<String, String> {
        return jdbiDao.extension<QueryHosDetail, List<kotlin.collections.HashMap<String, String>>> { get() }[0]
    }
}
