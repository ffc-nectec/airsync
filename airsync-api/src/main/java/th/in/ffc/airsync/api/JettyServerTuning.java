package th.in.ffc.airsync.api;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

final class JettyServerTuning {

    private static final int MAX_THREADS = 500;
    private static final int MIN_THREADS = 50;
    private static final int IDLE_TIMEOUT = 6000;

    private JettyServerTuning() {}

    static QueuedThreadPool getThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool(MAX_THREADS, MIN_THREADS, IDLE_TIMEOUT);
        threadPool.setDaemon(true);
        threadPool.setDetailedDump(false);
        return threadPool;
    }

    static LowResourceMonitor getMonitor(Server server) {
        LowResourceMonitor monitor = new LowResourceMonitor(server);
        monitor.setPeriod(1000);
        monitor.setLowResourcesIdleTimeout(1000);
        monitor.setMonitorThreads(true);
        monitor.setMaxConnections(0);
        monitor.setMaxMemory(0);
        monitor.setMaxLowResourcesTime(5000);
        return monitor;
    }

    static Connector[] getConnectors(Server server,String host, int port) {
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(host);
        connector.setPort(port);
        connector.setIdleTimeout(30000);
        connector.setAcceptQueueSize(3000);
        return new Connector[]{connector};
    }
}

