package ffc.airsync.school

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.School
import javax.sql.DataSource

class SchoolJdbi(
    ds: DataSource? = null
) : MySqlJdbi(ds), QuerySchool {
    override fun get(): List<School> {
        return jdbiDao.extension<QuerySchool, List<School>> { get() }
    }
}
