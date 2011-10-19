package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.*;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KunNorgeDotNoTest {
    ServiceDelegate serviceDelegate;
	LdapFacade ldapFacade;
	ForsendelsesArkiv forsendelsesArkiv;
	EmailFacade emailFacade;
	KunNorgeDotNo dispatcher;

	@Before
	public void init() {
        serviceDelegate = createStrictMock(ServiceDelegate.class);
		this.ldapFacade = createStrictMock( LdapFacade.class );
		this.forsendelsesArkiv = createStrictMock( ForsendelsesArkiv.class );
		this.emailFacade = createStrictMock( EmailFacade.class );
        DispatchPolicy dispatchPolicy = new DispatchPolicy();
        dispatchPolicy.setShipmentParams(Arrays.asList(new DispatchPolicyShipmentParams(ShipmentPolicy.KUN_NORGE_DOT_NO, 1)));

        this.dispatcher = new KunNorgeDotNo( serviceDelegate,ldapFacade, forsendelsesArkiv, emailFacade, dispatchPolicy );
	}

	@Test
	public void sendNorgeNo() {
		int variant = 2;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		forsendelse.setId( "" + variant );
		forsendelse.setFile( new File( "src/test/resources/test.pdf" ) );
		String fnr = forsendelse.getFnr();
		expect( ldapFacade.lookup( eq( fnr ) ) ).andReturn( true );
		emailFacade.send( eq( forsendelse ) );
		forsendelsesArkiv.setSentNorgeDotNo( eq( forsendelse.getId() ) );
		replay( ldapFacade );
		replay( emailFacade );
		replay( forsendelsesArkiv );
		dispatcher.send( forsendelse );
		verify( ldapFacade );
		verify( emailFacade );
		verify( forsendelsesArkiv );
	}

	@Test
	public void verifyForsendlese() {
		Forsendelse f = new Forsendelse();
		assertFalse( isOk( f ) );
		f.setNavn( "asd" );
		assertFalse( isOk( f ) );
		f.setAdresse1( "asd" );
		assertFalse( isOk( f ) );
		f.setPostnr( "asd" );
		assertFalse( isOk( f ) );
		f.setPoststed( "asd" );
		assertFalse( isOk( f ) );
		f.setTittel( "asd" );
		assertFalse( isOk( f ) );
		f.setFnr( "asd" );
		assertTrue( isOk( f ) );
	}

	private boolean isOk( Forsendelse f ) {
		boolean failed = false;
		try {
			dispatcher.verify( f );
		} catch (UserException e) {
			failed = true;
		}
		return !failed;
	}

}
