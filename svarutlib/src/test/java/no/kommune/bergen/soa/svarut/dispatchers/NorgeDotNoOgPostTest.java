package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.*;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NorgeDotNoOgPostTest {
    ServiceDelegate serviceDelegate;
	LdapFacade ldapFacade;
	ForsendelsesArkiv forsendelsesArkiv;
	EmailFacade emailFacade;
	PrintFacade printFacade;
	NorgeDotNoOgPost dispatcher;
	long leadTimeBeforePrint = 3 * 24 * 60 * 60 * 1000;

	@Before
	public void init() {
        serviceDelegate = createStrictMock(ServiceDelegate.class);
		this.ldapFacade = createStrictMock( LdapFacade.class );
		this.forsendelsesArkiv = createStrictMock( ForsendelsesArkiv.class );
		this.emailFacade = createStrictMock( EmailFacade.class );
		this.printFacade = createStrictMock( PrintFacade.class );
        DispatchPolicy dispatchPolicy= new DispatchPolicy();
        dispatchPolicy.setShipmentParams(Arrays.asList(new DispatchPolicyShipmentParams(ShipmentPolicy.NORGE_DOT_NO_OG_APOST, 3)));
		this.dispatcher = new NorgeDotNoOgPost( serviceDelegate,ldapFacade, forsendelsesArkiv, emailFacade, printFacade, dispatchPolicy);
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
	public void handleUnread() {
		int variant = 3;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		forsendelse.setId( "" + variant );
		forsendelse.setFile( new File( "src/test/resources/test.pdf" ) );
		long now = System.currentTimeMillis();
		long sent = now - (leadTimeBeforePrint + 1);
		Date date = new Date();
		date.setTime( sent );
		forsendelse.setSendt( date );
		PrintReceipt printReceipt = newPrintReceipt( "printId" );
		expect( printFacade.print( eq( forsendelse ) ) ).andReturn( printReceipt );
		forsendelsesArkiv.setPrinted( eq( forsendelse.getId() ), eq( printReceipt ) );
		replay( printFacade );
		replay( forsendelsesArkiv );
		dispatcher.handleUnread( forsendelse );
		verify( printFacade );
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

	private PrintReceipt newPrintReceipt( String printId ) {
		PrintReceipt printReceipt = new PrintReceipt();
		printReceipt.setPrintId( printId );
		return printReceipt;
	}

}
