package ffc.airsync.api.user

import ffc.airsync.api.Sync
import ffc.entity.User

interface UserInterface : Sync {
    val localUser: List<User>
    val cloudUser: List<User>
}
