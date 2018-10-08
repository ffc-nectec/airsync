package ffc.airsync.utils

import ffc.entity.gson.parseTo
import ffc.entity.gson.toJson
import java.io.FileReader
import java.io.FileWriter

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
