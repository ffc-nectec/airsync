package ffc.airsync.api.organization

import ffc.entity.Organization
import ffc.entity.Token

interface OrganizationApi {
    fun registerOrganization(
        localOrganization: Organization,
        onSuccessRegister: (organization: Organization, token: Token) -> Unit
    )

    fun deleteOrganization()
}
