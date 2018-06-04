package ffc.airsync.api.services.module

import ffc.model.TokenMessage
import ffc.model.User
import ffc.model.printDebug
import java.util.*
import javax.ws.rs.NotAuthorizedException

object UserService {


    fun create(orgId: String, userList: ArrayList<User>) {
        val org = orgDao.findById(orgId)
        userList.forEach {
            printDebug("insert username " + org.name + " User = " + it.username)
            orgUser.insert(it, org)
        }
    }


    fun login(id: String, user: String, pass: String): TokenMessage {

        val checkUser = orgUser.isAllowById(User(user, pass), id)
        if (checkUser) {
            val org = orgDao.findById(id)
            val token = UUID.randomUUID()

            val tokenObj = tokenMobile.insert(token = token,
              uuid = org.uuid,
              user = user,
              id = id,
              type = TokenMessage.TYPEROLE.USER)
            return tokenObj
        }
        throw NotAuthorizedException("Not Auth")
    }
}
