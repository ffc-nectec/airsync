package th.in.ffc.airsync.api;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.StringUtil;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;




public class FFCApiServer {


    protected static final int DEFAULT_PORT = 8080;
    protected static final String DEFAULT_HOST = "0.0.0.0";
    private static FFCApiServer instance;
    @Option(name = "-dev", usage = "mode")
    protected boolean dev = false;

    @Option(name = "-port", usage = "port destination to start server")
    protected int port = DEFAULT_PORT;

    @Option(name = "-host", usage = "port destination to start server")
    protected String host = DEFAULT_HOST;

    public FFCApiServer(String[] args) {
        try {
            CmdLineParser parser = new CmdLineParser(this);
            parser.parseArgument(args);
        } catch (CmdLineException cmd) {
            cmd.printStackTrace();
        }
    }

    public static void main(String[] args) {
        instance = new FFCApiServer(args);
        instance.run();
    }

    private void run() {
        ServletContextHandler context = ServletContextBuilder.build();

        Server server = new Server(JettyServerTuning.getThreadPool());

        server.setConnectors(JettyServerTuning.getConnectors(server,host, port));
        server.setHandler(context);
        server.addBean(JettyServerTuning.getMonitor(server));

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
