/*
 * Copyright (c) 2561 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.airsync.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;




public class FFCApiServer {


    protected static final int DEFAULT_PORT = 8080;
    protected static final String DEFAULT_HOST = "127.0.0.1";
    private static FFCApiServer instance;
    @Option(name = "-dev", usage = "mode")
    protected boolean dev = false;

    @Option(name = "-PORT", usage = "port destination ownAction start server")
    protected int port = DEFAULT_PORT;

    @Option(name = "-host", usage = "port destination ownAction start server")
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
