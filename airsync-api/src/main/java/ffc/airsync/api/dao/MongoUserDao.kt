package ffc.airsync.api.dao

import com.mongodb.*
import ffc.airsync.api.dao.UserDao.Companion.checkBlockUser
import ffc.model.*
import java.nio.charset.StandardCharsets
import java.util.*
import java.security.MessageDigest


class MongoUserDao : UserDao {


    companion object {
        private var mongoClient: MongoClient? = null
        private var dbName: String? = null
        var instant: MongoUserDao? = null

    }

    private val coll: DBCollection
    private val SALTPASS = """
uxF3Ocv5eg4BoQBK9MmR
rwPARiCL9ovpr3zmlJlj
kIQnpzRIgEh8WLFNHyy1
ALqs9ES1aQlsc47DlG5f
SbAOMWzMd1T03dyigoHR
7hox2nDJ7tMJRHab5gsy
Ux2VxiCIvJtfPAobOxYW
HazJzQEGdXpmeM2aK6MD
mpOARM2427A6CY14uomK
Cxe9aEkJEFtlLLo6NaNW
yLkbHUfMNDwWeu2BRXuS
m7BHwYSyKGFJdLnq4jJd
sr4QI6aK7g3GCm8vG6Pd
RAtlJZFto0bi9OZta5b4
DLrNTZXXtB3Ci17sepXU
HSYUuw11GJmeuiLKgJYZ
PCHuw2hpoozErKVxEv86
f6zMttthJyQnrDBHGhma
j1nrasD5fg9NxuwkdJq8
ytF2v69RwtGYf7C6ygwD
"""


    constructor(host: String, port: Int, databaseName: String) {
        val mongoUrl = System.getenv("MONGODB_URI")
        val collection = "user"


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


    override fun insert(user: User, org: Organization) {
        //printDebug("Insert username mongo. ${user.toJson()}")
        val query = BasicDBObject("user", user.username)
        coll.remove(query)

        val userDoc = objToDoc(user, org)
        coll.insert(userDoc)


    }

    override fun find(orgUuid: UUID): List<UserStor> {
        val listUser = arrayListOf<UserStor>()

        val query = BasicDBObject("orgUuid", orgUuid.toString())

        val userListDoc = coll.find(query)

        if (userListDoc.hasNext()) {
            val userDoc = userListDoc.next()
            val userStor = docToUserObj(userDoc)
            listUser.add(userStor)
        }

        return listUser
    }


    override fun findById(id: String): List<UserStor> {
        val listUser = arrayListOf<UserStor>()

        val query = BasicDBObject("orgId", id)
        val userListDoc = coll.find(query)

        if (userListDoc.hasNext()) {
            val userDoc = userListDoc.next()
            val userStor = docToUserObj(userDoc)
            listUser.add(userStor)
        }

        return listUser


    }


    override fun isAllow(user: User, orgUuid: UUID): Boolean {
        checkBlockUser(user)
        val query = BasicDBObject("orgUuid", orgUuid.toString())
          .append("user", user.username)
          .append("pass", getPass(user.password))
        val user = coll.findOne(query)
        return user != null
    }

    override fun isAllowById(user: User, orgId: String): Boolean {
        checkBlockUser(user)
        val query = BasicDBObject("orgId", orgId)
          .append("user", user.username)
          .append("pass", getPass(user.password))
        val user = coll.findOne(query)
        return user != null
    }

    override fun removeByOrgUuid(orgUUID: UUID) {
        val query = BasicDBObject("orgUuid", orgUUID.toString())
        coll.remove(query)
    }

    private fun docToUserObj(userDoc: DBObject): UserStor {
        val username = userDoc.get("user").toString()
        val password = userDoc.get("pass").toString()
        val orgId = userDoc.get("orgId").toString()
        val orgUuid = UUID.fromString(userDoc.get("orgUuid").toString())
        val user = User(username = username, password = password)

        return UserStor(user = user, orgUuid = orgUuid, orgId = orgId)
    }

    private fun getPass(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val encodedhash = digest.digest(
          (SALTPASS + password).toByteArray(StandardCharsets.UTF_8))

        val hexString = StringBuffer()
        for (i in 0 until encodedhash.size) {
            val hex = Integer.toHexString(0xff and encodedhash[i].toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }

    private fun objToDoc(user: User, org: Organization): BasicDBObject {
        val userDoc = BasicDBObject("orgUuid", org.uuid.toString())
          .append("orgId", org.id)
          .append("user", user.username)
          .append("pass", getPass(user.password))
        return userDoc
    }
}
