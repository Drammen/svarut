package no.kommune.bergen.soa.svarut.util;

/** Jmx monitor support class */
public class RecurringFailureLimit implements OperationalPolicy {

	private final int limit;

	public RecurringFailureLimit( int limit ) {
		this.limit = limit;
	}

	@Override
	public String reportViolations( InvocationRecord ir ) {
		if (ir.getVolatileExceptionCount() > limit) {
			return String.format( "%s has failed more than %s times lately.", ir.getName(), this.limit );
		}
		return "";
	}

}
