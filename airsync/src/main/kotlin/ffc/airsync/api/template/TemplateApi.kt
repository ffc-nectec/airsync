package ffc.airsync.api.template

import ffc.entity.Template

interface TemplateApi {
    fun clearAndCreate(template: List<Template>)
}

val templateApi: TemplateApi by lazy { RetofitTemplateApi() }
