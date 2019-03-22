package ffc.airsync.utils

import ffc.entity.gson.parseTo
import ffc.entity.gson.toJson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

inline fun saveResource(strData: String, fileName: String) {
    val fileWriter = FileWriter(fileName)
    fileWriter.write(strData)
    fileWriter.close()
}

inline fun <reified T> loadResource(fileName: String): List<T> {
    val fileJson = FileReader(fileName).readText().parseTo<List<Any>>()

    return fileJson.map {
        val ii = it.toJson().parseTo<T>()
        ii
    }
}

inline fun <reified T> loadResourceHashMap(fileName: String): HashMap<String, T> {
    return FileReader(fileName).readText().parseTo()
}

inline fun <reified T> List<T>.save(filename: String = "${getClassNameInList(this)}.json") {
    saveResource(this.toJson(), getDataStore(filename))
}

inline fun <reified T> HashMap<String, T>.save(filename: String) {
    saveResource(this.toJson(), getDataStore(filename))
}

inline fun <reified T> List<T>.load(filename: String = "${getClassNameInList(this)}.json"): List<T> {
    return try {
        loadResource(getDataStore(filename))
    } catch (ex: java.io.FileNotFoundException) {
        arrayListOf()
    }
}

inline fun <reified T> HashMap<String, T>.load(filename: String): HashMap<String, T> {
    return try {
        loadResourceHashMap(getDataStore(filename))
    } catch (ex: java.io.FileNotFoundException) {
        hashMapOf()
    }
}

inline fun <reified T> List<T>.cleanFile() {
    val fileName = "${getClassNameInList(this)}.json"
    saveResource("[]", getDataStore(fileName))
}

inline fun <reified T> getClassNameInList(list: List<T>): String {
    return T::class.java.simpleName
}

fun getDataStore(filename: String): String {
    val dataDirectory = File(getPathJarDir(), "data")
    if (!dataDirectory.exists())
        dataDirectory.mkdirs()
    return File(dataDirectory, filename).absolutePath
}
