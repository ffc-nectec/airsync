package ffc.airsync.api.dao

import com.mongodb.*
import ffc.model.printDebug
import java.util.*

abstract class MongoAbsConnect(val host: String, val port: Int, val dbName: String, val collection: String) {

    protected lateinit var coll: DBCollection
    protected var mongoClient: MongoClient? = null
    protected var instant: MongoAbsConnect? = null


    init {
        connectToMongo()
    }

    protected fun connectToMongo() {

        val mongoUrl = System.getenv("MONGODB_URI") + "?maxPoolSize=2&maxIdleTimeMS=20000&connectTimeoutMS=20000&socketTimeoutMS=20000"
        printDebug("Mongo Uri $mongoUrl")
        if (mongoClient == null) {
            if (mongoUrl == null) {
                printDebug("Create mongo client localhost")
                mongoClient = MongoClient(Arrays.asList(
                  ServerAddress(host, port)
                )/*,Arrays.asList(credential)*/)

                printDebug("\t mongoUrl=nul")
                this.coll = mongoClient!!.getDB(dbName).getCollection(collection)

            } else {
                printDebug("Create mongo clinet by uri")
                mongoClient = MongoClient(MongoClientURI(mongoUrl))
                printDebug("\tFinish create mongo clinet by uri.")
                printDebug("\t mongoUrl != null get systemenv ${System.getenv("MONGODB_DBNAME")}")
                this.coll = mongoClient!!.getDB(System.getenv("MONGODB_DBNAME")).getCollection(collection)
            }

            mongoClient!!.setWriteConcern(WriteConcern.JOURNALED)
            instant = this
        }
    }

    protected fun disconnetMongo() {
        try {
            mongoClient!!.close()
        } catch (ex: Exception) {

        }
    }

    interface MongoSafeRun {
        fun run()
    }

    protected fun mongoSafe(codeWorking: MongoSafeRun) {
        try {
            //disconnetMongo()
            //connectToMongo()
            codeWorking.run()
            //disconnetMongo()
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }

    }


}
