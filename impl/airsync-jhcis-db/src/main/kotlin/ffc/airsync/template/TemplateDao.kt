package ffc.airsync.template

import ffc.entity.Template

interface TemplateDao {
    fun get(): List<Template>
}
