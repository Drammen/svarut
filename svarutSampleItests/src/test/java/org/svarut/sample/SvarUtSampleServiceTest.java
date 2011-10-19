package org.svarut.sample;

import no.kommune.bergen.svarut.v1.*;

import org.junit.Before;
import org.junit.Test;
import org.svarut.sample.utils.SvarUtServiceCreator;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class SvarUtSampleServiceTest {
	private static final String fnr = "24035738572";
	private static final String navn = "navn";
	private static final String adresse1 = "adresse1";
	private static final String adresse2 = "adresse2";
	private static final String adresse3 = "adresse3";
	private static final String postnr = "postnr";
	private static final String poststed = "poststed";
	private static final String land = "land";
	private static final String tittel = "tittel";
	private static final String melding = "melding";
	private static final String epost = "qweq.asd@some.com";
	private SvarUtService port;

	@Before
	public void setup() throws MalformedURLException {
		port = SvarUtServiceCreator.getService();
	}

	@Test
	public void sendInvalidRq() {
		try {
			String forsendelsesId = port.send(null, new ForsendelsesRq() );
			fail( "Should not be reached! ForsendelsesId=" + forsendelsesId );
		} catch (Exception e) {
			assertTrue( e instanceof SOAPFaultException );
			assertEquals( "DataHandler is null", e.getMessage() );
		}
	}

	@Test
	public void sendNorgeDotNoOgApost() throws Exception {
		ForsendelsesRq forsendelseRq = createForsendelsesRq( 1 );
		String forsendelsesId = port.send(null, forsendelseRq );
		assertNotNull( forsendelsesId );
		System.out.println( "forsendelsesId=" + forsendelsesId );
		List<ForsendelseStatus> forsendelser = port.retrieveForPerson(null, fnr );
		assertTrue( forsendelser.size() > 0 );
		DokumentRs rs = port.retrieveContent( createUserContext(), forsendelsesId );
		assertEquals( forsendelsesId, rs.getFilnavn() );
		DataHandler dataHandler = rs.getData();
		assertNotNull(dataHandler.getInputStream().available());
		SvarUtServiceCreator.waitTillFinishedWorking();
		List<ForsendelseStatus> statuser = port.retrieveStatus(null, Arrays.asList(new String[]{forsendelser.get(0).getId()}));
		assertNotNull("Ikkje sendt", statuser.get(0).getSendtNorgedotno());
		//File file = new File( "target", rs.getFilnavn() );
		//Files.writeToFile( file, dataHandler.getInputStream() );
		//view( file );
	}

	@Test
	public void sendNoFnr() throws Exception {
		ForsendelsesRq forsendelseRq = createForsendelsesRq( 1 );
		forsendelseRq.getForsendelse().setFodselsnummer( null );
		forsendelseRq.getForsendelse().setForsendelsesMate( ShipmentPolicy.KUN_APOST );
		String forsendelsesId = port.send(null, forsendelseRq );
		assertNotNull( forsendelsesId );
	}

	@Test (expected=SOAPFaultException.class)
	public void sendWitNoForsendelse() throws Exception {
		ForsendelsesRq forsendelseRq = createForsendelsesRq( 1 );
		forsendelseRq.setForsendelse( null );
		port.send(null, forsendelseRq );
	}

	public static ForsendelsesRq createForsendelsesRq( int variant ) throws Exception {
		ForsendelsesRq rq = new ForsendelsesRq();
		Forsendelse forsendelse = new Forsendelse();
		forsendelse.setFodselsnummer( fnr );
		forsendelse.setNavn( navn + variant );
		Adresse123 adresse = new Adresse123();
		adresse.setAdresse1( adresse1 + variant );
		adresse.setAdresse2( adresse2 + variant );
		adresse.setAdresse3( adresse3 + variant );
		adresse.setPostnr( postnr + variant );
		adresse.setPoststed( poststed + variant );
		adresse.setLand( land + variant );
		forsendelse.setAdresse( adresse );
		forsendelse.setTittel( tittel + variant );
		forsendelse.setMeldingstekst( melding + variant );
		forsendelse.setEpost( epost );
		rq.setForsendelse( forsendelse );
		forsendelse.setForsendelsesMate( ShipmentPolicy.NORGE_DOT_NO_OG_APOST );
		URL url = SvarUtSampleServiceTest.class.getResource( "/Undervisningsfritak.pdf" );
		URLDataSource urldatasource = new URLDataSource( url );
		DataHandler dataHandler = new DataHandler( urldatasource );
		rq.setData( dataHandler );
		return rq;
	}

	public static void view( File pdf ) {
		try {
			Runtime.getRuntime().exec( "evince " + pdf.getPath() );
		} catch (IOException e) {
			throw new RuntimeException( "Unable to launch: evince " + pdf.getPath() );
		}
	}

	private UserContext createUserContext() {
		return createUserContext( fnr );
	}

	public static UserContext createUserContext( String fnr ) {
		UserContext uc = new UserContext();
		uc.setUserid( fnr );
		return uc;
	}

}
