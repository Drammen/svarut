package org.svarut.sample.utils;

/**
 * Class holds execution time constants. Values can be manipulated by setting the following system property UseMottakserver=true |
 * false (default) and the environment variable TargetServer=LOCALHOST (default) | ACCEPTANCE | INTEGRATION
 * 
 */
public class Constants {
	public static enum TargetServer {
		ACCEPTANCE, JENKINS, LOCALHOST
	};

	public static TargetServer targetServer = TargetServer.LOCALHOST; // LOCALHOST INTEGRATION ACCEPTANCE
	public static String webContainer = "http://localhost:8080";

	static {
		setup();
		if (targetServer == TargetServer.LOCALHOST) {
			webContainer = "http://localhost:8080";
		} else if (targetServer == TargetServer.JENKINS) {
			webContainer = "http://localhost:8099";
		} else if (targetServer == TargetServer.ACCEPTANCE) {
			webContainer = "";
		}
		logConfig();
	}

	static void setup() {
		targetServer = TargetServer.LOCALHOST;
		String hostToTest = System.getenv( "TargetServer" );
		if ("LOCALHOST".equals( hostToTest )) {
			targetServer = TargetServer.LOCALHOST;
		} else if ("ACCEPTANCE".equals( hostToTest )) {
			targetServer = TargetServer.ACCEPTANCE;
		} else if ("JENKINS".equals( hostToTest )) {
			targetServer = TargetServer.JENKINS;
		}
	}

	static void logConfig() {
		StringBuilder sb = new StringBuilder( "TEST CONFIGURATION - " + targetServer );
		sb.append( "\n webContainer: " ).append( webContainer );
		System.out.println( sb.toString() );
	}

}
