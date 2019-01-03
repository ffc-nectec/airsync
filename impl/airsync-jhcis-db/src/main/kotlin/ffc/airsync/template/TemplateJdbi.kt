package ffc.airsync.template

import ffc.airsync.MySqlJdbi
import ffc.airsync.extension
import ffc.entity.Template
import javax.sql.DataSource

class TemplateJdbi(
    dbHost: String = "127.0.0.1",
    dbPort: String = "3333",
    dbName: String = "jhcisdb",
    dbUsername: String = "root",
    dbPassword: String = "123456",
    ds: DataSource? = null
) : MySqlJdbi(dbHost, dbPort, dbName, dbUsername, dbPassword, ds), TemplateDao {
    override fun get(): List<Template> {
        val output = arrayListOf<Template>()
        output.addAll(jdbiDao.extension<TemplateQuery, List<Template>> { getSyntom() })
        output.addAll(jdbiDao.extension<TemplateQuery, List<Template>> { getHomeVisitDetail() })
        output.addAll(jdbiDao.extension<TemplateQuery, List<Template>> { getHomeVisitResult() })
        return output
    }
}
