package ffc.airsync.template

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.Template

class TemplateJdbi(
    val jdbiDao: MySqlJdbi = MySqlJdbi(null)
) : TemplateDao {
    override fun get(): List<Template> {
        val output = arrayListOf<Template>()
        output.addAll(jdbiDao.extension<TemplateQuery, List<Template>> { getSyntom() })
        output.addAll(jdbiDao.extension<TemplateQuery, List<Template>> { getHomeVisitDetail() })
        output.addAll(jdbiDao.extension<TemplateQuery, List<Template>> { getHomeVisitResult() })
        return output
    }
}
