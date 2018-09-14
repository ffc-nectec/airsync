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

inline fun <reified T> loadResource(fileName: String): List<T> {
    val file = FileReader(fileName).readText()
    val fileJson = file.parseTo<List<Any>>()

    val result = arrayListOf<T>()

    fileJson.forEach {
        val anyJson = it.toJson()
        result.add(anyJson.parseTo())
    }
    return result
}

inline fun <reified T> List<T>.save(filename: String = "${getClassNameInList(this)}.json") {
    saveResource(this.toJson(), filename)
}

inline fun <reified T> List<T>.load(filename: String = "${getClassNameInList(this)}.json"): List<T> {

    return try {
        loadResource(filename)
    } catch (ex: java.io.FileNotFoundException) {
        arrayListOf()
    }
}

inline fun <reified T> List<T>.cleanFile() {
    saveResource("[]", "${getClassNameInList(this)}.json")
}

inline fun <reified T> getClassNameInList(list: List<T>): String {
    return T::class.java.simpleName
}
