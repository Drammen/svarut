package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.*;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import static no.kommune.bergen.soa.svarut.dto.ShipmentPolicy.ALTINN_OG_APOST;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

public class AltinnOgPostTest {
    ServiceDelegate serviceDelegate;
	ForsendelsesArkiv forsendelsesArkiv;
	AltinnFacade altinnFacade;
	PrintFacade printFacade;
	AltinnOgPost dispatcher;
	long leadTimeBeforePrint = 3 * 24 * 60 * 60 * 1000;

	@Before
	public void init() {
        forsendelsesArkiv = createStrictMock( ForsendelsesArkiv.class );
        altinnFacade = createStrictMock( AltinnFacade.class );
        printFacade = createStrictMock( PrintFacade.class );
        serviceDelegate = createStrictMock(ServiceDelegate.class);
        DispatchPolicy dispatchPolicy= new DispatchPolicy();
        dispatchPolicy.setShipmentParams(Arrays.asList(new DispatchPolicyShipmentParams(ALTINN_OG_APOST,1)));
        dispatcher = new AltinnOgPost( serviceDelegate,forsendelsesArkiv, altinnFacade, printFacade, dispatchPolicy);
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
		PrintReceipt printReceipt = newPrintReceipt( "MY-PRINT-ID" );
		expect( printFacade.print( eq( forsendelse ) ) ).andReturn( printReceipt );
		forsendelsesArkiv.setPrinted( eq( forsendelse.getId() ), eq( printReceipt ) );
		replay( printFacade );
		replay( forsendelsesArkiv );
		dispatcher.handleUnread( forsendelse );
		verify( printFacade );
		verify( forsendelsesArkiv );
	}

	@Test
	public void verifyForsendelse() {
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
		f.setOrgnr( "asd" );
		assertFalse( isOk( f ) );
		f.setMeldingsTekst("Hei");
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
