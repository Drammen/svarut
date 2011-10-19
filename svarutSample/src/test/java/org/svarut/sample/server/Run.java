package org.svarut.sample.server;

import org.eclipse.jetty.server.Server;

import java.io.File;

public class Run {

	public static void main(String... args) throws Exception {
		System.setProperty("CONSTRETTO_TAGS", "ITEST");
		Server server = new Server(8080);
		String dir;
		if (new File("src/main/webapp").exists()) dir = "src/main/webapp";
		else dir = "svarutSample/src/main/webapp";
		server.setHandler(new org.eclipse.jetty.webapp.WebAppContext(dir, "/"));
		server.start();
		server.join();
	}
}
