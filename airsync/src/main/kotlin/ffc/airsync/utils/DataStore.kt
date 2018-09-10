package ffc.airsync.utils

import ffc.airsync.Main
import ffc.airsync.db.DatabaseDao
import ffc.entity.House
import ffc.entity.Person
import ffc.entity.User
import ffc.entity.gson.parseTo
import ffc.entity.healthcare.Chronic
import java.io.FileWriter
import java.nio.charset.Charset

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

inline private fun writeFile(strData: String, fileName: String) {
    val fileWriter = FileWriter(fileName)
    fileWriter.write(strData)
    fileWriter.close()
}

inline private fun <reified T> getResourceAs(filename: String): T {
    val classloader = Thread.currentThread().contextClassLoader
    val file = classloader.getResourceAsStream(filename)
        .bufferedReader(Charset.forName("UTF-8"))

    return file.readText().parseTo()
}
