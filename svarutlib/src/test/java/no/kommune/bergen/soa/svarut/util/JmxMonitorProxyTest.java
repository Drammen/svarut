package no.kommune.bergen.soa.svarut.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.util.JmxMonitorProxy;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class JmxMonitorProxyTest {
	private JmxMonitorProxy jmxMonitorProxy;
	private ServiceDelegate serviceDelegateMock;

	@Before
	public void init() {
		serviceDelegateMock = EasyMock.createNiceMock( ServiceDelegate.class );
		jmxMonitorProxy = new JmxMonitorProxy( serviceDelegateMock );
	}

	@Test
	public void invocationReport() {
		int invocationCount = 17;
		for (int i = 0; i < invocationCount; i++) {
			jmxMonitorProxy.dispatch();
		}
		serviceDelegateMock.printUnread();
		EasyMock.expectLastCall().andThrow( new RuntimeException() );
		EasyMock.replay( serviceDelegateMock );
		try {
			jmxMonitorProxy.printUnread();
		} catch (RuntimeException e) {}
		assertEquals( 17L, jmxMonitorProxy.dispatchIr.getInvocationCount() );
		assertEquals( 0L, jmxMonitorProxy.printUnreadIr.getInvocationCount() );
		assertEquals( 1L, jmxMonitorProxy.printUnreadIr.getExceptionCount() );
		String report = jmxMonitorProxy.invocationReport();
		assertNotNull( report );
		assertTrue( report.indexOf( "<td>" + invocationCount + "</td>" ) > -1 );
		assertTrue( report.indexOf( "<td>1</td>" ) > -1 );
		//System.out.println( report );
	}

	@Test
	public void reportOperationalPolicyViolations() {
		String report = jmxMonitorProxy.reportOperationalPolicyViolations();
		assertNotNull( report );
		assertEquals( "none", report );
	}

}
