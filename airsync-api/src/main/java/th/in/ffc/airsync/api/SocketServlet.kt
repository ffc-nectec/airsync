package th.`in`.ffc.airsync.api.services

import org.eclipse.jetty.websocket.servlet.WebSocketServlet
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import th.`in`.ffc.airsync.api.websocket.ApiSocket

class SocketServlet : WebSocketServlet() {
    override fun configure(factory: WebSocketServletFactory?) {
        //factory.register(ApiSocket.class)
        factory!!.getPolicy().setIdleTimeout(10000);
        factory!!.register(ApiSocket::class.java)

    }
}
