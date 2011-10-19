package no.kommune.bergen.soa.svarut.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import no.kommune.bergen.soa.svarut.util.InvocationRecord;
import no.kommune.bergen.soa.svarut.util.RecurringFailureLimit;

import org.junit.Test;

public class RecurringFailureLimitTest {

	@Test
	public void reportPolicyViolations() {
		InvocationRecord ir = new InvocationRecord( "testA" );
		ir.addPolicy( new RecurringFailureLimit( 2 ) );
		assertEquals( "", ir.reportPolicyViolations() );
		ir.recordSuccess();
		ir.recordException( "Shit happens!" );
		ir.recordException( "Shit happens!" );
		assertEquals( "", ir.reportPolicyViolations() );
		ir.recordException( "Shit happens!" );
		assertTrue( ir.reportPolicyViolations().indexOf( "failed more than" ) > -1 );
		ir.recordSuccess();
		assertEquals( "", ir.reportPolicyViolations() );
		ir.recordException( "Shit happens!" );
		assertEquals( "", ir.reportPolicyViolations() );
	}
}
