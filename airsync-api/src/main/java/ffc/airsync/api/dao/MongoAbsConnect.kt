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
                printDebug("\tFinish create mongo clinet by uri.")
            }


            mongoClient!!.setWriteConcern(WriteConcern.JOURNALED)
            instant = this
        }


        if (mongoUrl == null) {
            printDebug("\t mongoUrl=nul")
            this.coll = mongoClient!!.getDB(dbName).getCollection(collection)
        } else {
            printDebug("\t mongoUrl != null get systemenv ${System.getenv("MONGODB_DBNAME")}")
            this.coll = mongoClient!!.getDB(System.getenv("MONGODB_DBNAME")).getCollection(collection)
        }
    }

}
