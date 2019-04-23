package ffc.airsync.api.template

import ffc.airsync.Main
import ffc.airsync.templateApi

class TemplateInit() {
    init {
        val template = Main.instant.dao.getTemplate()
        templateApi.clearAndCreate(template)
    }
}
