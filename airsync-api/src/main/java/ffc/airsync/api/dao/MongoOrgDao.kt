package ffc.airsync.api.dao

import com.mongodb.*
import ffc.airsync.api.get6DigiId
import ffc.model.Organization
import ffc.model.printDebug
import org.bson.types.ObjectId
import java.util.*
import javax.ws.rs.NotFoundException

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


        //uuid id token ipaddress
        val doc = createDoc(organization, ObjectId())
        coll.insert(doc)


    }

    override fun find(): List<Organization> {
        val orgCursorList = coll.find()
        val orgList = loadDocList(orgCursorList)
        if (orgList.size < 1) throw NotFoundException("ไม่พบรายการ org ลงทะเบียน ในระบบ")
        return orgList

    }

    override fun findByUuid(uuid: UUID): Organization {
        val query = BasicDBObject("orgUuid", uuid.toString())
        val doc = coll.findOne(query) ?: throw NotFoundException("ไม่พบ uuid ${uuid.toString()} ที่ค้นหา")
        val organization = loadDoc(doc)

        return organization
    }

    override fun findByIpAddress(ipAddress: String): List<Organization> {
        val query = BasicDBObject("lastKnownIp", ipAddress)
        val docList = coll.find(query)
        val orgList = loadDocList(docList)

        if (orgList.size < 1) throw NotFoundException("ไม่พบรายการลงทะเบียนในกลุ่มของ Org ip $ipAddress")

        return orgList
    }

    override fun findByToken(token: UUID): Organization {
        val query = BasicDBObject("token", token.toString())
        val doc = coll.findOne(query) ?: throw NotFoundException("ไม่พบ token ${token.toString()} ที่ค้นหา")
        val organization = loadDoc(doc)

        return organization

    }

    override fun findById(id: String): Organization {
        val query = BasicDBObject("idOrg", id)
        val doc = coll.findOne(query) ?: throw NotFoundException("ไม่พบ id org $id ที่ค้นหา")
        val organization = loadDoc(doc)

        return organization
    }

    override fun remove(organization: Organization) {
        val query = BasicDBObject("orgUuid", organization.uuid.toString())
        coll.remove(query)
    }

    override fun updateToken(organization: Organization): Organization {
        val query = BasicDBObject("orgUuid", organization.uuid.toString())
        val oldDoc = coll.findOne(query)
          ?: throw NotFoundException("ไม่พบ Object organization ${organization.uuid} ให้ Update")


        organization.token = UUID.randomUUID()
        val updateDoc = createDoc(organization, ObjectId(oldDoc.get("_id").toString()))


        return organization
    }

    override fun removeByOrgUuid(orgUUID: UUID) {
        val query = BasicDBObject("orgUuid", orgUUID.toString())

        val doc = coll.findAndRemove(query) ?: throw NotFoundException()

    }

    private fun createDoc(organization: Organization, objId: ObjectId): BasicDBObject {

        val shortId = objId.get6DigiId()

        val doc = BasicDBObject("_id", objId)
          .append("_shortId", shortId)
          .append("orgUuid", organization.uuid.toString())
          .append("pcuCode", organization.pcuCode)
          .append("name", organization.name)
          .append("token", organization.token.toString())
          .append("idOrg", organization.id)

          .append("lastKnownIp", organization.lastKnownIp)
          .append("firebaseToken", organization.firebaseToken)

        return doc
    }

    private fun loadDoc(doc: DBObject): Organization {

        val organization = Organization(
          UUID.fromString(doc.get("orgUuid").toString()),
          doc.get("idOrg").toString())
        organization.pcuCode = doc.get("pcuCode").toString()
        organization.name = doc.get("name").toString()
        organization.token = UUID.fromString(doc.get("token").toString())
        organization.id = doc.get("idOrg").toString()

        organization.lastKnownIp = doc.get("lastKnownIp").toString()
        organization.firebaseToken = doc.get("firebaseToken").toString()

        return organization
    }

    private fun loadDocList(cursor: DBCursor): List<Organization> {

        val orgList = arrayListOf<Organization>()
        while (cursor.hasNext()) {
            val it = cursor.next()

            val organization = loadDoc(it)
            orgList.add(organization)
        }
        return orgList

    }
}
