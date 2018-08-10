/*
 * Copyright (c) 2018 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.client.webservice.webresources

import ffc.airsync.localweb.printDebug
import java.io.File
import java.io.FileInputStream
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
class IndexResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{webpart:(.*.html)}")
    fun getHtml(@PathParam("webpart") webPart: String): String {
        printDebug("Get client html web $webPart")
        return loadFile(webPart)
    }

    @GET
    @Produces("text/css")
    @Path("/{webpart:(.*.css)}")
    fun getCss(@PathParam("webpart") webPart: String): String {
        printDebug("Get client css web $webPart")
        return loadFile(webPart)
    }

    @GET
    @Produces("application/json")
    @Path("/{webpart:(.*.json)}")
    fun getJson(@PathParam("webpart") webPart: String): String {
        printDebug("Get client css web $webPart")
        return loadFile(webPart)
    }

    @GET
    @Produces("application/javascript")
    @Path("/{webpart:(.*.js)}")
    fun getJs(@PathParam("webpart") webPart: String): String {
        printDebug("Get client js web $webPart")
        return loadFile(webPart)
    }

    private fun loadFile(webPart: String): String {
        try {
            var fileName = webPart.replace("\\.\\.", "")
            fileName = fileName.replace("^[\\/]+", "")

            printDebug("After filter = $fileName")

            // val classLoader = Thread.currentThread().contextClassLoader

            val classLoader = javaClass.classLoader
            val resourceURL = classLoader.getResource(fileName)
            val file = File(resourceURL.file)

            val fileInputStream = FileInputStream(file)
            val data = ByteArray(file.length().toInt())
            fileInputStream.read(data)
            fileInputStream.close()
            val str = String(data, charset("UTF-8"))
            return str
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }
}
