package no.kommune.bergen.soa.svarut.util;

import java.util.Date;

/** Jmx monitor support class */
public class TimeLimitBetweenInvocations implements OperationalPolicy {

	private final long limit;

	public TimeLimitBetweenInvocations( long limit ) {
		this.limit = limit;
	}

	@Override
	public String reportViolations( InvocationRecord ir ) {
		long now = System.currentTimeMillis();
		long lastInvoked = ir.getLastInvoked();
		long creationTime = ir.getCreationTime();
		if (0 == lastInvoked && now > (creationTime + limit)) {
			return String.format( "%s has not been invoked since boot up - %s ", ir.getName(), new Date( creationTime ) );
		} else if (0 == lastInvoked) {
			return "";
		} else if (now > (lastInvoked + limit)) {
			return String.format( "%s has not been invoked for too long (Limit is %s ms)", ir.getName(), this.limit );
		}
		return "";
	}

}
