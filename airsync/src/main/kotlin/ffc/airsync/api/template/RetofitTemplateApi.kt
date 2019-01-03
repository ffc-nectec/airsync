package ffc.airsync.api.template

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.Template

class RetofitTemplateApi : RetofitApi<TemplateUrl>(TemplateUrl::class.java), TemplateApi {
    override fun clearAndCreate(template: List<Template>) {
        callApi {
            restService.clearnAndCreate(
                orgId = organization.id,
                authkey = tokenBarer,
                template = template
            ).execute()
        }
    }
}
