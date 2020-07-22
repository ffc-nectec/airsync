package ffc.airsync.api.genogram.lib

internal data class Person<P>(val pcucode: String, val houseNumber: String, val person: P, val name: String? = null)
