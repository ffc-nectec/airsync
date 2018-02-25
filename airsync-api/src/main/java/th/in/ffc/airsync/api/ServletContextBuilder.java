package th.in.ffc.airsync.api;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import th.in.ffc.airsync.api.services.SocketServlet;

public class ServletContextBuilder {

    public static final String ROOT_PATH = "";

    public static ServletContextHandler build() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(ROOT_PATH);

        ServletHolder jersey = new ServletHolder(new ServletContainer(new ApplicationConfig()));
        jersey.setInitOrder(0);
        context.addServlet(jersey, "/v0/*");

        ServletHolder holderEvents = new ServletHolder("ws-events", SocketServlet.class);
        //holderEvents.setInitOrder(1);
        context.addServlet(holderEvents, "/socket/*");

        return context;
    }

}

