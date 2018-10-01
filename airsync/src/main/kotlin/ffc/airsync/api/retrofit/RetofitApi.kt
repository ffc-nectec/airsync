package ffc.airsync.api.retrofit

import ffc.entity.House
import ffc.entity.Organization
import ffc.entity.Person

abstract class RetofitApi(val persons: List<Person>, val houses: List<House>, val organization: Organization) {

    init {

    }
}
