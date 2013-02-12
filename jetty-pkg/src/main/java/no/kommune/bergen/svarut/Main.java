package no.kommune.bergen.svarut;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.ProtectionDomain;

public class Main {

	private final int port;
	private final String contextPath;
	private final String workPath;
	private final String secret;

	public static void main(String[] args) throws Exception {
		Main sc = new Main();
		sc.start();
	}

	public Main() {
		try {
			String configFile = System.getProperty("config", "no.kommune.bergen.svarut.properties");
			System.getProperties().load(new FileInputStream(configFile));
		} catch (Exception ignored) {
		}

		port = Integer.parseInt(System.getProperty("jetty.port", "9080"));
		contextPath = System.getProperty("jetty.contextPath", "/");
		workPath = System.getProperty("no.kommune.bergen.svarut.workDir", null);
		secret = System.getProperty("no.kommune.bergen.svarut.secret", "eb27fb2e61ed603367461b3b4e37e0a0");
	}

	private void start() {
		// Start a Jetty server with some sensible(?) defaults
		try {
			Server srv = new Server();
			srv.setStopAtShutdown(true);

			// Allow 5 seconds to complete.
			// Adjust this to fit with your own webapp needs.
			// Remove this if you wish to shut down immediately (i.e. kill <pid> or Ctrl+C).
			srv.setGracefulShutdown(5000);

			// Increase thread pool
			QueuedThreadPool threadPool = new QueuedThreadPool();
			threadPool.setMaxThreads(100);
			srv.setThreadPool(threadPool);

			// Ensure using the non-blocking connector (NIO)
			Connector connector = new SelectChannelConnector();
			connector.setPort(port);
			connector.setMaxIdleTime(30000);
			srv.setConnectors(new Connector[]{connector});

			// Get the war-file
			ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
			String warFile = protectionDomain.getCodeSource().getLocation().toExternalForm();
			String currentDir = new File(protectionDomain.getCodeSource().getLocation().getPath()).getParent();

			// Add the warFile (this jar)
			WebAppContext context = new WebAppContext(warFile, contextPath);
			context.setServer(srv);
			resetTempDirectory(context, currentDir);

			// Add the handlers
			HandlerList handlers = new HandlerList();
			handlers.addHandler(context);
			srv.setHandler(handlers);

			srv.start();
			srv.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void resetTempDirectory(WebAppContext context, String currentDir) throws IOException {
		File workDir;
		if (workPath != null) {
			workDir = new File(workPath);
		} else {
			workDir = new File(currentDir, "work");
		}
		FileUtils.deleteDirectory(workDir);
		context.setTempDirectory(workDir);
	}


}
