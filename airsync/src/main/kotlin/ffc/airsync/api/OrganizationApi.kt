package ffc.airsync.api

import ffc.entity.Organization

interface OrganizationApi {
    fun registerOrganization(organization: Organization, url: String): Organization
}
