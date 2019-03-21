package ffc.airsync.api.template

import ffc.airsync.retrofit.RetofitApi
import ffc.airsync.utils.callApi
import ffc.entity.Template
import javax.ws.rs.NotAuthorizedException

class RetofitTemplateApi : RetofitApi<TemplateUrl>(TemplateUrl::class.java), TemplateApi {
    override fun clearAndCreate(template: List<Template>) {
        callApi {
            val response = restService.clearnAndCreate(
                orgId = organization.id,
                authkey = tokenBarer,
                template = template
            ).execute()

            if (response.code() != 201)
                if (response.code() == 401) {
                    var errorBody = ""
                    response.errorBody()?.byteStream()?.reader()?.readLines()?.let { error ->
                        error.forEach {
                            errorBody += it + "\r\n"
                        }
                    }
                    throw NotAuthorizedException("Create Template code ${response.code()} $errorBody")
                }
        }
    }
}
