package th.`in`.ffc.airsync.api.services

import org.eclipse.jetty.websocket.servlet.WebSocketServlet
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import th.`in`.ffc.airsync.api.websocket.EventSocket

class SocketServlet : WebSocketServlet() {
    override fun configure(factory: WebSocketServletFactory?) {
        //factory.register(EventSocket.class)
        factory!!.getPolicy().setIdleTimeout(30000);
        factory!!.register(EventSocket::class.java)

    }
}
