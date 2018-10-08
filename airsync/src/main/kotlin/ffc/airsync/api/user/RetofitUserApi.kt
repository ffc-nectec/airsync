package ffc.airsync.api.user

import ffc.airsync.retrofit.RetofitApi
import ffc.entity.User

class RetofitUserApi : RetofitApi(), UserApi {
    override fun putUser(userInfoList: List<User>): List<User> {
        val respond =
            restService.regisUser(user = userInfoList, orgId = organization.id, authkey = tokenBarer).execute()
        return respond.body() ?: arrayListOf()
    }
}
