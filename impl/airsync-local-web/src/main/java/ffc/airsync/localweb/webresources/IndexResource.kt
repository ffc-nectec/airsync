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

import ffc.airsync.localweb.getLogger
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
class IndexResource {
    private val logger by lazy { getLogger(this) }
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/{webpart:(.*.html)}")
    fun getHtml(@PathParam("webpart") webPart: String): String {
        logger.info("Get client html web $webPart")
        return loadFile(webPart)
    }

    @GET
    @Produces("text/css")
    @Path("/{webpart:(.*.css)}")
    fun getCss(@PathParam("webpart") webPart: String): String {
        logger.debug("Get client css web $webPart")
        return loadFile(webPart)
    }

    @GET
    @Produces("application/json")
    @Path("/{webpart:(.*.json)}")
    fun getJson(@PathParam("webpart") webPart: String): String {
        logger.debug("Get client css web $webPart")
        return loadFile(webPart)
    }

    @GET
    @Produces("application/javascript")
    @Path("/{webpart:(.*.js)}")
    fun getJs(@PathParam("webpart") webPart: String): String {
        logger.debug("Get client js web $webPart")
        return loadFile(webPart)
    }

    private fun loadFile(webPart: String): String {
        var fileName = webPart.replace("\\.\\.", "")
        fileName = fileName.replace("^[\\/]+", "")

        logger.debug("After filter = $fileName")
        val classLoader = javaClass.classLoader

        return classLoader.getResource(fileName).readText()
    }
}
