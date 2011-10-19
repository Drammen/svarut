package no.kommune.bergen.soa.svarut;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import no.kommune.bergen.soa.svarut.LdapFacade;
import no.kommune.bergen.soa.svarut.ServiceContext;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class LdapFacadeIntTest {

	@Test
	@Ignore
	public void lookup() {
		System.setProperty( "CONSTRETTO_TAGS", "ATEST" );
		XTrustProvider.install();
		ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
		ServiceContext serviceContext = (ServiceContext)context.getBean( "serviceContext" );
		LdapFacade ldapFacade = serviceContext.getLdapFacade();
		boolean exists = ldapFacade.lookup( "24035738572" );
		assertTrue( "looukup(24035738572) returned false", exists );
	}

	@Test
	@Ignore
	public void lookupNull() {
		System.setProperty( "CONSTRETTO_TAGS", "DEV" );
		ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
		ServiceContext serviceContext = (ServiceContext)context.getBean( "serviceContext" );
		LdapFacade ldapFacade = serviceContext.getLdapFacade();
		boolean exists = ldapFacade.lookup( null );
		assertFalse( "looukup(null) returned true", exists );
		exists = ldapFacade.lookup( "" );
		assertFalse( "looukup('') returned true", exists );
	}

}
