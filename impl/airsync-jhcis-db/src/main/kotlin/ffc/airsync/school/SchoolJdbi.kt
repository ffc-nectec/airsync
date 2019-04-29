package ffc.airsync.school

import ffc.airsync.Dao
import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.place.School

class SchoolJdbi(
    val jdbiDao: Dao = MySqlJdbi(null)
) : QuerySchool {
    override fun get(): List<School> {
        return jdbiDao.extension<QuerySchool, List<School>> { get() }
    }
}
