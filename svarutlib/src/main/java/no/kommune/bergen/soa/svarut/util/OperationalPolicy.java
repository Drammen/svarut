package no.kommune.bergen.soa.svarut.util;

/** Jmx monitor support interface */
public interface OperationalPolicy {
	/** Be quiet if in compliance */
	String reportViolations( InvocationRecord ir );
}
