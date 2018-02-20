package th.in.ffc.airsync.api;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class ServletContextBuilder {

    public static final String ROOT_PATH = "/v0";

    public static ServletContextHandler build() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(ROOT_PATH);

        ServletHolder jersey = new ServletHolder(new ServletContainer(new ApplicationConfig()));
        jersey.setInitOrder(0);
        context.addServlet(jersey, "/*");
        return context;
    }

}

