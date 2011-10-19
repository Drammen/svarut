package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.DispatchPolicy;
import no.kommune.bergen.soa.svarut.DispatchPolicyShipmentParams;
import no.kommune.bergen.soa.svarut.PrintFacade;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;
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

public class KunPostTest {
    ServiceDelegate serviceDelegate;
	ForsendelsesArkiv forsendelsesArkiv;
	PrintFacade printFacade;
	KunPost dispatcher;

	@Before
	public void init() {
        serviceDelegate = createStrictMock(ServiceDelegate.class);
		this.forsendelsesArkiv = createStrictMock( ForsendelsesArkiv.class );
		this.printFacade = createStrictMock( PrintFacade.class );
        DispatchPolicy dispatchPolicy = new DispatchPolicy();
        dispatchPolicy.setShipmentParams(Arrays.asList(
                new DispatchPolicyShipmentParams(ShipmentPolicy.KUN_APOST, 1),
                new DispatchPolicyShipmentParams(ShipmentPolicy.KUN_BPOST, 2)));

		this.dispatcher = new KunPost( serviceDelegate,forsendelsesArkiv, printFacade, dispatchPolicy );
	}

	@Test
	public void sendApost() {
		final int variant = 2;
		final String printId = "printId";
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		forsendelse.setId( "" + variant );
		forsendelse.setFile( new File( "src/test/resources/test.pdf" ) );
		forsendelse.setFnr( "" );
        forsendelse.setShipmentPolicy(ShipmentPolicy.KUN_APOST.value());
		PrintReceipt printReceipt = newPrintReceipt( printId );
		expect( printFacade.print( eq( forsendelse ) ) ).andReturn( printReceipt );
		forsendelsesArkiv.setPrinted( eq( forsendelse.getId() ), eq( printReceipt ) );
		replay( printFacade );
		replay( forsendelsesArkiv );
		dispatcher.send( forsendelse );
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
		assertTrue( isOk( f ) );
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
