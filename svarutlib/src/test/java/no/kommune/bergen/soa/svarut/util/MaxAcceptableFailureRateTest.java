package no.kommune.bergen.soa.svarut.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import no.kommune.bergen.soa.svarut.util.InvocationRecord;
import no.kommune.bergen.soa.svarut.util.MaxAcceptableFailureRate;

import org.junit.Test;

public class MaxAcceptableFailureRateTest {

	@Test
	public void violateMoreExceptionsThanSuccesses() throws InterruptedException {
		InvocationRecord ir = new InvocationRecord( "testA" );
		ir.addPolicy( new MaxAcceptableFailureRate( .01 ) );
		assertEquals( "", ir.reportPolicyViolations() );
		ir.recordSuccess();
		assertEquals( "", ir.reportPolicyViolations() );
		ir.recordException( "EXCEPTION" );
		assertEquals( "", ir.reportPolicyViolations() );
		ir.recordException( "EXCEPTION" );
		assertTrue( ir.reportPolicyViolations().indexOf( "failure rate bigger" ) > -1 );
	}

	@Test
	public void violateExceptionRate() throws InterruptedException {
		InvocationRecord ir = new InvocationRecord( "testA" );
		ir.addPolicy( new MaxAcceptableFailureRate( .01 ) );
		for (int i = 0; i < 98; i++) {
			ir.recordSuccess();
		}
		assertEquals( "", ir.reportPolicyViolations() );
		ir.recordException( "EXCEPTION" );
		assertEquals( "", ir.reportPolicyViolations() );
		ir.recordException( "EXCEPTION" );
		assertTrue( ir.reportPolicyViolations().indexOf( "failure rate bigger" ) > -1 );
	}
}
