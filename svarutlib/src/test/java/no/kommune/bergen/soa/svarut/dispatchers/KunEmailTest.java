package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.DispatchPolicy;
import no.kommune.bergen.soa.svarut.DispatchPolicyShipmentParams;
import no.kommune.bergen.soa.svarut.EmailFacade;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.easymock.EasyMock.eq;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

public class KunEmailTest {
    ServiceDelegate serviceDelegate;
	ForsendelsesArkiv forsendelsesArkiv;
	EmailFacade emailFacade;
	KunEmail dispatcher;

	@Before
	public void init() {
		forsendelsesArkiv = createStrictMock( ForsendelsesArkiv.class );
		emailFacade = createStrictMock( EmailFacade.class );
        serviceDelegate = createStrictMock(ServiceDelegate.class);

        DispatchPolicy dispatchPolicy = new DispatchPolicy();
        dispatchPolicy.setShipmentParams(Arrays.asList(new DispatchPolicyShipmentParams(ShipmentPolicy.KUN_EMAIL, 1)));

        this.dispatcher = new KunEmail( serviceDelegate,forsendelsesArkiv, emailFacade , dispatchPolicy);
	}

	@Test
	public void sendEmail() {
		int variant = 2;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		forsendelse.setId( "" + variant );
		forsendelse.setFile( new File( "src/test/resources/test.pdf" ) );
		String fnr = forsendelse.getFnr();
		assertNotNull( fnr );
		emailFacade.send( eq( forsendelse ) );
		forsendelsesArkiv.setSentEmail( eq( forsendelse.getId() ) );
		forsendelsesArkiv.stop( eq( forsendelse.getId() ) );
		replay( emailFacade );
		replay( forsendelsesArkiv );
		dispatcher.send( forsendelse );
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
		f.setEmail( "asd" );
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
