package ffc.airsync.api.dao

import ffc.model.StorageOrg
import ffc.model.TokenMessage
import java.util.*

class MongoTokenDao : TokenDao {

    override fun insert(token: UUID, uuid: UUID, user: String, id: String, type: TokenMessage.TYPEROLE): TokenMessage {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun find(token: UUID): StorageOrg<TokenMessage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findByOrgUuid(orgUUID: UUID): List<StorageOrg<TokenMessage>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(token: UUID) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateFirebaseToken(token: UUID, firebaseToken: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeByOrgUuid(orgUUID: UUID) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
