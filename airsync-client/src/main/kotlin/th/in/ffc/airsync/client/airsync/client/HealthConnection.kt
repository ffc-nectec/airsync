package th.`in`.ffc.airsync.client.airsync.client

class HealthConnection() {
    companion object {
        private var runLoop = false
        private val thread = Thread(Runnable {
            while (true) {
                if(runLoop) {
                    NetworkClient.client.sendText("H")
                }
                Thread.sleep(5000)
            }
        })
    }

    fun start(){
        runLoop =true
    }
    fun stop(){
        runLoop =false
    }

    fun join(){
        thread.join()
    }

    init {
        thread.start()
    }


}
