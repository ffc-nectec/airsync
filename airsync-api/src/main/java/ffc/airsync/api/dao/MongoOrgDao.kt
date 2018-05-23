package ffc.airsync.api.dao

import com.mongodb.*
import ffc.airsync.api.get6DigiId
import ffc.model.Organization
import ffc.model.printDebug
import org.bson.types.ObjectId
import java.util.*

class MongoOrgDao : OrgDao {

    companion object {
        private var mongoClient: MongoClient? = null
        private var dbName: String? = null
        var instant: MongoOrgDao? = null

    }

    val coll: DBCollection


    constructor(host: String, port: Int, databaseName: String) {
        val mongoUrl = System.getenv("MONGODB_URI")
        val collection = "organize"


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


    override fun insert(organization: Organization) {
        var queryRemove = BasicDBObject("orgUuid", organization.uuid.toString())

        coll.remove(queryRemove)

        val objId = ObjectId()
        val shortId = objId.get6DigiId()

        //uuid id token ipaddress
        val doc = BasicDBObject("_id", objId)
          .append("_shortId", shortId)
          .append("orgUuid", organization.uuid.toString())
          .append("token", organization.token)


    }

    override fun find(): List<Organization> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByUuid(uuid: UUID): Organization {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByIpAddress(ipAddress: String): List<Organization> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByToken(token: UUID): Organization {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: String): Organization {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(organization: Organization) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateToken(organization: Organization): Organization {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeByOrgUuid(orgUUID: UUID) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
