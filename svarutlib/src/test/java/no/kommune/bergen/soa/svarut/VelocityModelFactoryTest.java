package no.kommune.bergen.soa.svarut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import org.junit.Before;
import org.junit.Test;

public class VelocityModelFactoryTest {
	private VelocityModelFactory velocityModelFactory;
	private static final String urlTemplate = "http://suse-oas-portal.iktfou.no:7778/wsproxy/forsendelse?forsendelsesId=";
	private static final String helpLink = "http://suse-oas-portal.iktfou.no:7777/wsproxy/HjelpTilNedlasting?forsendelsesId=";
	private static final String pdfLinkText = "Dokument ref.";
	private static final String helpLinkText = "Hjelp";
	private static final String readerDownloadLink = "http://get.adobe.com/no/reader/";
	private static final String readerDownloadLinkText = "Last ned her";

	@Before
	public void init() {
		velocityModelFactory = createVelocityModelFactory();
	}

	@Test
	public void createModel() {
		Forsendelse f = createForsendelse();
		Map<String, String> model = velocityModelFactory.createModel( f );
		verifyModel( model, f );
	}

	@Test
	public void takesTwoArguments() {
		boolean takesTwoArguments = velocityModelFactory.takesTwoArguments( "http://suse-oas-portal.iktfou.no:7778/wsproxy/forsendelse?forsendelsesId=%s" );
		assertFalse( takesTwoArguments );
		takesTwoArguments = velocityModelFactory.takesTwoArguments( "http://localhost:9080/forsendelse/service/rest/forsendelsesservice/download/%s/%s" );
		assertTrue( takesTwoArguments );
	}

	private Forsendelse createForsendelse() {
		ForsendelsesArkiv forsendelsesArkiv = ForsendelsesArkivTest.createForsendesesArkiv();
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		String id = forsendelsesArkiv.save( forsendelse, ForsendelsesArkivTest.getTestDocument() );
		Forsendelse f = forsendelsesArkiv.retrieve( id );
		return f;
	}

	public static VelocityModelFactory createVelocityModelFactory() {
		return new VelocityModelFactory( urlTemplate + "%s", pdfLinkText, helpLink + "%s", helpLinkText, readerDownloadLink, readerDownloadLinkText );
	}

	public static void verifyModel( Map<String, String> model, Forsendelse f ) {
		for (Map.Entry<String, String> entry : model.entrySet()) {
			assertNotNull( entry.getKey() + " is null", entry.getValue() );
		}
		assertEquals( f.getAvsenderNavn(), model.get( "AVSENDER_NAVN" ) );
		assertEquals( f.getAvsenderAdresse1(), model.get( "AVSENDER_ADRESSE1" ) );
		assertEquals( f.getAvsenderAdresse2(), model.get( "AVSENDER_ADRESSE2" ) );
		assertEquals( f.getAvsenderAdresse3(), model.get( "AVSENDER_ADRESSE3" ) );
		assertEquals( f.getAvsenderPostnr(), model.get( "AVSENDER_POSTNR" ) );
		assertEquals( f.getAvsenderPoststed(), model.get( "AVSENDER_POSTSTED" ) );
		assertEquals( f.getNavn(), model.get( "NAVN" ) );
		assertEquals( f.getPostnr(), model.get( "POSTNR" ) );
		assertEquals( f.getPoststed(), model.get( "POSTSTED" ) );
		assertEquals( f.getAdresse1(), model.get( "ADRESSE1" ) );
		assertEquals( f.getAdresse2(), model.get( "ADRESSE2" ) );
		assertEquals( f.getAdresse3(), model.get( "ADRESSE3" ) );
		assertEquals( f.getNavn(), model.get( "NAVN" ) );
		assertEquals( f.getPostnr(), model.get( "POSTNR" ) );
		assertEquals( f.getPoststed(), model.get( "POSTSTED" ) );
		assertEquals( f.getLand(), model.get( "LAND" ) );
		assertEquals( f.getTittel(), model.get( "TITTEL" ) );
		assertEquals( f.getMeldingsTekst(), model.get( "MELDING" ) );
		assertEquals( f.getFnr(), model.get( "FNR" ) );
		assertEquals( String.valueOf( f.getOrgnr() ), model.get( "ORGNR" ) );
		assertEquals( f.getId(), model.get( "ID" ) );
		String url = model.get( "URL" );
		assertNotNull( url );
		assertTrue( url.indexOf( f.getId() ) > -1 );
		assertTrue( url.indexOf( urlTemplate ) > -1 );
		File file = new File( model.get( "FILE" ) );
		assertNotNull( file );
		assertTrue( file.getPath().indexOf( f.getId() ) > -1 );
		String helpUrl = model.get( "Help-Link" );
		assertNotNull( helpUrl );
		assertTrue( helpUrl.indexOf( f.getId() ) > -1 );
		assertTrue( helpUrl.indexOf( helpLink ) > -1 );
		assertNull( model.get( "DOKUMENT" ) );
		String readerDownloadUrl = model.get( "ReaderDownload-Link" );
		assertNotNull( readerDownloadUrl );
		assertTrue( readerDownloadUrl.indexOf( readerDownloadLink ) > -1 );
	}

}
