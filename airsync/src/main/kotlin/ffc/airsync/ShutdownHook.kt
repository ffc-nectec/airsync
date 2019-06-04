package ffc.airsync

import ffc.airsync.utils.jobCount
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class ShutdownHook : Thread() {
    override fun run() {
        isShutdown = true
        runBlocking {
            delay(1000)
            var working = true
            while (working) {
                if (jobCount() == 0) {
                    working = false
                } else {
                    delay(500)
                }
            }
        }
    }
}
