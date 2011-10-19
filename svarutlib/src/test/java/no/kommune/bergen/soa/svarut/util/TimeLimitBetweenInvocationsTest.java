package no.kommune.bergen.soa.svarut.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import no.kommune.bergen.soa.svarut.util.InvocationRecord;
import no.kommune.bergen.soa.svarut.util.TimeLimitBetweenInvocations;

import org.junit.Test;

public class TimeLimitBetweenInvocationsTest {

	@Test
	public void violateLimitNotInvokedSinceBoot() throws InterruptedException {
		InvocationRecord ir = new InvocationRecord( "testA" );
		ir.addPolicy( new TimeLimitBetweenInvocations( 1 ) );
		Thread.sleep( 10 );
		String report = ir.reportPolicyViolations();
		assertTrue( report.indexOf( "since boot" ) > -1 );
	}

	@Test
	public void violateNotInvokedForTooLong() throws InterruptedException {
		InvocationRecord ir = new InvocationRecord( "testA" );
		ir.addPolicy( new TimeLimitBetweenInvocations( 1 ) );
		ir.recordSuccess();
		Thread.sleep( 10 );
		String report = ir.reportPolicyViolations();
		assertTrue( report.indexOf( "too long" ) > -1 );
	}

	@Test
	public void notViolatedLimitNotInvokedSinceBoot() throws InterruptedException {
		InvocationRecord ir = new InvocationRecord( "testA" );
		ir.addPolicy( new TimeLimitBetweenInvocations( 1000 * 60 * 60 * 3 ) );
		String report = ir.reportPolicyViolations();
		assertEquals( "", report );
	}

	@Test
	public void notViolated() throws InterruptedException {
		InvocationRecord ir = new InvocationRecord( "testA" );
		ir.addPolicy( new TimeLimitBetweenInvocations( 1000 * 60 * 60 * 3 ) );
		ir.recordSuccess();
		String report = ir.reportPolicyViolations();
		assertEquals( "", report );
	}

}
