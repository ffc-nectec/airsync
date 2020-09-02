/*
 *
 *  * Copyright 2020 NECTEC
 *  *   National Electronics and Computer Technology Center, Thailand
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
    getLogger(this).info("List ${T::class.java.simpleName} to file have ${this.size} item.")
    saveResource(this.toJson(), getDataStore(filename))
}

inline fun <reified T> HashMap<String, T>.save(filename: String) {
    getLogger(this).info("Map ${T::class.java.simpleName} to file have ${this.size} item.")
    saveResource(this.toJson(), getDataStore(filename))
}

inline fun <reified T> List<T>.load(filename: String = "${getClassNameInList(this)}.json"): List<T> {
    return try {
        loadResource(getDataStore(filename))
    } catch (ex: java.io.FileNotFoundException) {
        arrayListOf()
    }
}

inline fun <reified R> ffcFileLoad(file: File): List<R> {
    if (!file.isFile) return emptyList()
    return FileReader(file).readText().parseTo<List<Any>>().map {
        it.toJson().parseTo<R>()
    }
}

inline fun <reified T> ffcFileSave(file: File, data: List<T>) {
    require(!file.isDirectory) { "ไม่สามารถบันทึก file ${file.absolutePath} ลง Directory ได้" }
    val fileWriter = FileWriter(file)
    fileWriter.write(data.toJson())
    fileWriter.close()
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
