package no.kommune.bergen.soa.svarut.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Jmx monitor support class */
public class InvocationRecord {
	private final String name;
	private long invocationCount = 0, exceptionCount = 0, volatileExceptionCount = 0, lastInvoked = 0, lastException = 0;
	private String lastExceptionMsg = "";
	private final long creationTime = System.currentTimeMillis();
	private final List<OperationalPolicy> policies = new ArrayList<OperationalPolicy>();

	public InvocationRecord( String name ) {
		this.name = name;
	}

	public void addPolicy( OperationalPolicy policy ) {
		policies.add( policy );
	}

	public String reportPolicyViolations() {
		StringBuilder sb = new StringBuilder();
		for (OperationalPolicy policy : policies) {
			String violation = policy.reportViolations( this );
			if (0 < violation.length()) {
				sb.append( violation ).append( "\n" );
			}
		}
		return sb.toString();
	}

	public synchronized void recordSuccess() {
		this.invocationCount++;
		this.lastExceptionMsg = "";
		this.volatileExceptionCount = 0;
		lastInvoked = System.currentTimeMillis();
	}

	public synchronized void recordException( String msg ) {
		this.exceptionCount++;
		this.volatileExceptionCount++;
		this.lastExceptionMsg = msg;
		lastException = System.currentTimeMillis();
	}

	public static String reportAsHtml( InvocationRecord[] list ) {
		StringBuilder sb = new StringBuilder( "<table>\n<tr><th>Method</th><th>Invocations</th><th>Last</th><th>Exceptions</th><th>Lately</th><th>Last</th><th>Message</th></tr>\n" );
		for (InvocationRecord entry : list) {
			Date lastInvoked = new Date( entry.getLastInvoked() );
			Date lastException = new Date( entry.getLastException() );
			sb.append( "<tr><td>" ).append( entry.getName() ).append( "</td><td>" ).append( entry.getInvocationCount() ).append( "</td><td>" ).append( lastInvoked ).append( "</td><td>" ).append( entry.getExceptionCount() ).append( "</td><td>" )
					.append( entry.getVolatileExceptionCount() ).append( "</td><td>" ).append( lastException ).append( "</td><td>" ).append( entry.getLastExceptionMsg() ).append( "</td></tr>\n" );
		}
		sb.append( "</table>\n" );
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public synchronized long getInvocationCount() {
		return invocationCount;
	}

	public synchronized long getExceptionCount() {
		return exceptionCount;
	}

	public synchronized long getLastInvoked() {
		return lastInvoked;
	}

	public synchronized long getLastException() {
		return lastException;
	}

	public synchronized String getLastExceptionMsg() {
		return lastExceptionMsg;
	}

	public synchronized long getVolatileExceptionCount() {
		return volatileExceptionCount;
	}

	/** Return Time when this class was instantiated */
	public long getCreationTime() {
		return creationTime;
	}

}
