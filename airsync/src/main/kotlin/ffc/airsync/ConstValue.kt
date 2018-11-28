package ffc.airsync

import ffc.entity.Person
import ffc.entity.User
import ffc.entity.healthcare.HealthCareService
import ffc.entity.healthcare.analyze.HealthAnalyzer
import ffc.entity.place.House

val houses = arrayListOf<House>()
val persons = arrayListOf<Person>()
val users = arrayListOf<User>()
val relation = arrayListOf<Person>()
val healthCare = arrayListOf<HealthCareService>()
val pcucode = StringBuilder()
val analyzer = hashMapOf<String, HealthAnalyzer>()
