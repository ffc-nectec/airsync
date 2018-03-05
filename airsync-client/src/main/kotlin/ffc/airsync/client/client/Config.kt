package ffc.airsync.client.client

import java.net.URI

class Config (){
    companion object {
        var pcuUuid ="00000000-0000-0000-0000-000000000009"
        val uri = URI.create("ws://127.0.0.1:8080/airsync")
        //val uri = URI.create("ws://188.166.249.72:80/airsync")
    }
}
