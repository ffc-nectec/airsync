package ffc.airsync.api.dao

import com.mongodb.*
import ffc.model.printDebug
import java.util.*

abstract class MongoAbsConnect {

    protected val coll: DBCollection
    protected var mongoClient: MongoClient? = null
    protected var dbName: String? = null
    protected var instant: MongoAbsConnect? = null


    constructor(host: String, port: Int, databaseName: String, collection: String) {

        val mongoUrl = System.getenv("MONGODB_URI")
        if (mongoClient == null) {
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
            this.coll = mongoClient!!.getDB(dbName).getCollection(collection)
        else
            this.coll = mongoClient!!.getDB(System.getenv("MONGODB_DBNAME")).getCollection(collection)
    }

}
