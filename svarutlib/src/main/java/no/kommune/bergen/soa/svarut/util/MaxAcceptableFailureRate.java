package no.kommune.bergen.soa.svarut.util;

/** Jmx monitor support class */
public class MaxAcceptableFailureRate implements OperationalPolicy {
	private final double limit;

	public MaxAcceptableFailureRate( double limit ) {
		this.limit = limit;
	}

	@Override
	public String reportViolations( InvocationRecord ir ) {
		double exceptionCount = ir.getExceptionCount();
		double invocationCount = ir.getInvocationCount();
		String failureReport = String.format( "%s has a failure rate bigger than %s.", ir.getName(), this.limit );
		if (exceptionCount > invocationCount) {
			return failureReport;
		} else if ((invocationCount + exceptionCount) < 100) {
			return "";
		} else if (limit < (exceptionCount / invocationCount + exceptionCount)) {
			return failureReport;
		}
		return "";
	}

}
