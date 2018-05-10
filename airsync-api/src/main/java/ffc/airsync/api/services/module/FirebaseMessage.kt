package ffc.airsync.api.services.module

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import ffc.model.Address
import org.joda.time.Hours
import java.util.concurrent.ExecutionException


fun Message.Builder.putHouseData(address: Address, registrationToken: String, orgId: String) {
    val message = Message.builder()

      .putData("type", "House")
      .putData("_id", address._id)
      .putData("url", "$orgId/place/house/${address._id}")
      .setToken(registrationToken)
      .build()

    var response: String? = null
    try {
        response = FirebaseMessaging.getInstance().sendAsync(message).get()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    } catch (e: ExecutionException) {
        e.printStackTrace()
    }
// Response is a message ID string.
    println("Successfully sent message: " + response!!)
}




