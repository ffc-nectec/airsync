package ffc.airsync.utils

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.entity.House
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.gson.parseTo
import ffc.entity.gson.toJson
import ffc.entity.healthcare.Chronic
import java.io.FileReader
import java.io.FileWriter

fun Person.gets(dao: DatabaseDao = Main.instant.createDatabaseDao()): List<Person> {
    val persons = dao.getPerson()
    val chronic = dao.getChronic()

    return mapChronics(persons, chronic)
}

fun User.gets(dao: DatabaseDao = Main.instant.createDatabaseDao()): List<User> {
    return dao.getUsers()
}

fun Chronic.gets(dao: DatabaseDao = Main.instant.createDatabaseDao()): List<Chronic> {
    return dao.getChronic()
}

fun House.gets(where: String = "", dao: DatabaseDao = Main.instant.createDatabaseDao()): List<House> {
    return if (where.isBlank()) dao.getHouse() else dao.getHouse(where)
}

private fun mapChronics(persons: List<Person>, chronics: List<Chronic>): List<Person> {
    persons.forEach { person ->
        person.chronics.addAll(chronics.filter {
            it.link!!.keys["pid"] == person.link!!.keys["pid"]
        })
    }
    return persons
}

inline fun saveResource(strData: String, fileName: String) {
    val fileWriter = FileWriter(fileName)
    fileWriter.write(strData)
    fileWriter.close()
}

inline fun <reified T> loadResource(fileName: String): T {
    val file = FileReader(fileName).readText()
    return file.parseTo()
}

inline fun <reified T> List<T>.save() {
    saveResource(this.toJson(), "${getClassNameInList(this)}.json")
}

inline fun <reified T> List<T>.load(): List<T> {

    return try {
        loadResource("${getClassNameInList(this)}.json") ?: arrayListOf()
    } catch (ex: java.io.FileNotFoundException) {
        arrayListOf()
    }
}

inline fun <reified T> List<T>.cleanFile() {
    saveResource("", "${getClassNameInList(this)}.json")
}

inline fun <reified T> getClassNameInList(list: List<T>): String {
    return T::class.java.simpleName
}
