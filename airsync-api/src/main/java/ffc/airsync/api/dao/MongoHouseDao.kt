/*
 * Copyright (c) 2561 NECTEC
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

package ffc.airsync.api.dao

import com.mongodb.*
import ffc.airsync.api.get6DigiId
import java.util.*
import java.util.Arrays
import ffc.model.*
import me.piruin.geok.LatLng
import org.bson.types.ObjectId
import javax.ws.rs.NotFoundException
import kotlin.collections.ArrayList


class MongoHouseDao : HouseDao {

    val coll: DBCollection

    constructor(host: String, port: Int, databaseName: String, collection: String) {
        val mongoUrl = System.getenv("MONGODB_URI")
        if (mongoClient == null) {

            //printDebug("Mongo URI " + mongoUrl.substring(5))
            //Ref. https://mongodb.github.io/mongo-java-driver/2.13/getting-started/quick-tour/
            //val credential = MongoCredential.createCredential(userName, database, password)
            if (mongoUrl == null) {
                printDebug("Create mongo client localhost")
                mongoClient = MongoClient(Arrays.asList(
                  ServerAddress(host, port)
                )/*,Arrays.asList(credential)*/)
                dbName = databaseName
            } else {
                printDebug("Create mongo clinet by uri")
                mongoClient = MongoClient(MongoClientURI(mongoUrl))
            }


            mongoClient!!.setWriteConcern(WriteConcern.JOURNALED)

            instant = this
        }
        if (mongoUrl == null)
            this.coll = getCollection(collection = collection, dbName = dbName)
        else
            this.coll = getCollection(collection = collection, dbName = System.getenv("MONGODB_DBNAME"))
    }

    companion object {
        private var mongoClient: MongoClient? = null
        private var dbName: String? = null
        var instant: MongoHouseDao? = null

    }


    override fun insert(orgUuid: UUID, house: Address) {

        var query = BasicDBObject("orgUuid", orgUuid.toString())
          .append("hid", house.hid)


        val objId = ObjectId()
        val shortId = objId.get6DigiId()
        house._id = objId.toHexString()
        house._shortId = shortId

        val doc = BasicDBObject("_id", objId)
          .append("_shortId", shortId)
          .append("orgUuid", orgUuid.toString())
          .append("hid", house.hid)
          .append("latitude", house.coordinates?.latitude)
          .append("longitude", house.coordinates?.longitude)
        house.coordinates = null
        doc.append("property", house.toJson())
        printDebug(doc)

        coll.remove(query)
        coll.insert(doc)

    }

    override fun insert(orgUuid: UUID, houseList: List<Address>) {
        printDebug("MongoHouseDao Insert")
        houseList.forEach {
            insert(orgUuid, it)
        }

    }

    override fun update(house: Address) {
        printDebug("Call MongoHouseDao.upldate ${house.toJson()}")
        val query = BasicDBObject("_id", ObjectId(house._id))
        //.append("orgUuid", orgUuid.toString())
        printDebug("\tquery old house ")
        val oldDoc = coll.findOne(query) ?: throw NotFoundException("ไม่พบ Object ให้ Update")

        val orgUuid = oldDoc.get("orgUuid").toString()
        printDebug("\tget orgUuid $orgUuid")


        printDebug("\tcreate update doc")
        val updateDoc = BasicDBObject("_id", ObjectId(house._id))
          .append("orgUuid", orgUuid)
          .append("hid", house.hid)
          .append("latitude", house.coordinates?.latitude)
          .append("longitude", house.coordinates?.longitude)

        house.pcuCode = oldDoc.get("property").toString().fromJson<Address>().pcuCode
        updateDoc.append("property", house.toJson())

        printDebug("\tcall collection.update (oldDoc, updateDoc)")
        coll.update(oldDoc, updateDoc)
    }

    override fun update(houseList: List<Address>) {
        houseList.forEach {
            update(it)
        }
    }

    override fun find(latlng: Boolean): List<StorageOrg<Address>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun findBy_Id(orgUuid: UUID, _id: String, latlng: Boolean): List<StorageOrg<Address>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun find(orgUuid: UUID, latlng: Boolean): List<StorageOrg<Address>> {
        var query = BasicDBObject("orgUuid", orgUuid.toString())

        if (latlng) {
            query = query
              .append("longitude", BasicDBObject("\$ne", 0.0))
              .append("latitude", BasicDBObject("\$ne", 0.0))
        }

        val cursor = coll.find(query)

        printDebug("getHouseInMongo size = ${cursor.count()}")
        val listHouse: ArrayList<StorageOrg<Address>> = arrayListOf()
        while (cursor.hasNext()) {
            val it = cursor.next()
            val property = it.get("property")
            printDebug(property)
            val house: Address = property.toString().fromJson()
            house.coordinates = LatLng(it.get("latitude").toString().toDouble(), it.get("longitude").toString().toDouble())
            printDebug(house)
            listHouse.add(StorageOrg(orgUuid, house))

        }
        return listHouse
    }

    override fun findByHouseId(orgUuid: UUID, hid: Int): StorageOrg<Address>? {
        val query = BasicDBObject("orgUuid", orgUuid.toString())
          .append("hid", hid)
        val dbObj = coll.findOne(query)
        val house: Address = dbObj.get("property").toString().fromJson()
        house.coordinates = LatLng(dbObj.get("latitude").toString().toDouble(), dbObj.get("longitude").toString().toDouble())

        return StorageOrg(orgUuid, house)
    }

    override fun removeByOrgUuid(orgUuid: UUID) {
        val query = BasicDBObject("orgUuid", orgUuid.toString())
        val cursor = coll.find(query)
        while (cursor.hasNext()) {
            coll.remove(cursor.next())
        }

    }

    private fun getCollection(dbName: String?, collection: String): DBCollection {
        return mongoClient!!.getDB(dbName).getCollection(collection)
    }


}
