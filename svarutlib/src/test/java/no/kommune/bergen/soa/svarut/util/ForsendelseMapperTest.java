package no.kommune.bergen.soa.svarut.util;

import no.kommune.bergen.soa.common.pdf.PdfGeneratorImpl;
import no.kommune.bergen.soa.svarut.dto.Adresse123;
import no.kommune.bergen.soa.svarut.dto.Forsendelse;
import static no.kommune.bergen.soa.svarut.dto.ShipmentPolicy.ALTINN_OG_APOST;
import no.kommune.bergen.soa.util.Files;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ForsendelseMapperTest {
	private static final String FNR = "FNR";
	private static final String ORGNR = "123456789";
	private static final String navn = "navn";
	private static final String adresse1 = "adresse1";
	private static final String adresse2 = "adresse2";
	private static final String adresse3 = "adresse3";
	private static final String postnr = "postnr";
	private static final String poststed = "poststed";
	private static final String avsender_navn = "avsender_navn";
	private static final String avsender_adresse1 = "avsender_adresse1";
	private static final String avsender_adresse2 = "avsender_adresse2";
	private static final String avsender_adresse3 = "avsender_adresse3";
	private static final String avsender_postnr = "avsender_postnr";
	private static final String avsender_poststed = "avsender_poststed";
	private static final String land = "land";
	private static final String tittel = "tittel";
	private static final String melding = "melding";
	private static final String appid = "appid";
	private static final String email = "svarut.to@hudson.iktfou.no";
	private static final String replyTo = "svarut.from@hudson.iktfou.no";
	ForsendelseMapper forsendelsesMapper;
	public static final File testPdf = new File( "src/test/resources/test.pdf" );
	static PdfGeneratorImpl pdfGenerator = new PdfGeneratorImpl( "target" );

	@Before
	public void init() {
		forsendelsesMapper = new ForsendelseMapper();
	}

	@Test
	public void mapForsendelse() {
		Forsendelse forsendelseFraRq = createComForsendelse( 1 );
		no.kommune.bergen.soa.svarut.domain.Forsendelse forsendelse = forsendelsesMapper.fromDto( forsendelseFraRq );
		validateForsendelse( forsendelse, 1 );
		forsendelse = createIntrinsicForsendelse( 1 );
		forsendelseFraRq = forsendelsesMapper.toDto( forsendelse );
		validateForsendelse( forsendelseFraRq, 1 );
	}

	@Test
	public void mapForsendelseNoOrgNoFnr() {
		Forsendelse forsendelseFraRq = createComForsendelse( 1, null, null );
		no.kommune.bergen.soa.svarut.domain.Forsendelse forsendelse = forsendelsesMapper.fromDto( forsendelseFraRq );
		validateForsendelse( forsendelse, 1, null, null );
		forsendelse = createIntrinsicForsendelse( 1, null, null );
		forsendelseFraRq = forsendelsesMapper.toDto( forsendelse );
		validateForsendelse( forsendelseFraRq, 1 );
	}

	public no.kommune.bergen.soa.svarut.domain.Forsendelse createIntrinsicForsendelse( int variant ) {
		return createIntrinsicForsendelse( variant, FNR + variant, ORGNR + variant );
	}

	public no.kommune.bergen.soa.svarut.domain.Forsendelse createIntrinsicForsendelse( int variant, String fnr, String orgnr ) {
		no.kommune.bergen.soa.svarut.domain.Forsendelse f = new no.kommune.bergen.soa.svarut.domain.Forsendelse();
		f.setFnr( fnr );
		f.setOrgnr( orgnr );
		f.setNavn( navn + variant );
		f.setAdresse1( adresse1 + variant );
		f.setAdresse2( adresse2 + variant );
		f.setAdresse3( adresse3 + variant );
		f.setPostnr( postnr + variant );
		f.setPoststed( poststed + variant );
		f.setLand( land + variant );
		f.setAvsenderNavn( avsender_navn + variant );
		f.setAvsenderAdresse1( avsender_adresse1 + variant );
		f.setAvsenderAdresse2( avsender_adresse2 + variant );
		f.setAvsenderAdresse3( avsender_adresse3 + variant );
		f.setAvsenderPostnr( avsender_postnr + variant );
		f.setAvsenderPoststed( avsender_poststed + variant );
		f.setTittel( tittel + variant );
		f.setMeldingsTekst( melding + variant );
		f.setAppid( appid + variant );
		f.setShipmentPolicy( ALTINN_OG_APOST.value());
		f.setEmail( email );
		f.setReplyTo( replyTo );
		return f;
	}

	public static Forsendelse createComForsendelse( int variant ) {
		return createComForsendelse( variant, FNR, ORGNR );
	}

	public static Forsendelse createComForsendelse( int variant, String fnr, String orgnr ) {
		Forsendelse forsendelse = new Forsendelse();
		if (fnr != null) forsendelse.setFodselsnummer(fnr + variant);
		if (orgnr != null) forsendelse.setOrgnr( Integer.parseInt( orgnr + variant ) );
		forsendelse.setNavn( navn + variant );
		Adresse123 adresse = new Adresse123();
		adresse.setAdresse1( adresse1 + variant );
		adresse.setAdresse2( adresse2 + variant );
		adresse.setAdresse3( adresse3 + variant );
		adresse.setPostnr( postnr + variant );
		adresse.setPoststed( poststed + variant );
		adresse.setLand( land + variant );
		forsendelse.setAdresse( adresse );
		forsendelse.setAvsenderNavn( avsender_navn + variant );
		Adresse123 avsenderAdresse = new Adresse123();
		avsenderAdresse.setAdresse1( avsender_adresse1 + variant );
		avsenderAdresse.setAdresse2( avsender_adresse2 + variant );
		avsenderAdresse.setAdresse3( avsender_adresse3 + variant );
		avsenderAdresse.setPostnr( avsender_postnr + variant );
		avsenderAdresse.setPoststed( avsender_poststed + variant );
		forsendelse.setTittel( tittel + variant );
		forsendelse.setMeldingstekst( melding + variant );
		forsendelse.setAvsenderadresse( avsenderAdresse );
		forsendelse.setAppid( appid + variant );
		forsendelse.setForsendelsesMate( ALTINN_OG_APOST );
		forsendelse.setEpost( email );
		forsendelse.setReplyTo( replyTo );
		return forsendelse;
	}

	public static void validateForsendelse( no.kommune.bergen.soa.svarut.domain.Forsendelse f, int variant ) {
		validateForsendelse( f, variant, FNR, ORGNR );
	}

	public static void validateForsendelse( no.kommune.bergen.soa.svarut.domain.Forsendelse f, int variant, String fnr, String orgnr ) {
		if (f.getFnr() != null) assertEquals( "fnr", (fnr + variant), f.getFnr() );
		if (f.getOrgnr() != null) assertEquals( "orgnr", (orgnr + variant), f.getOrgnr() );
		assertEquals( "navn", (navn + variant), f.getNavn() );
		assertEquals( "adresse1", (adresse1 + variant), f.getAdresse1() );
		assertEquals( "adresse2", (adresse2 + variant), f.getAdresse2() );
		assertEquals( "adresse3", (adresse3 + variant), f.getAdresse3() );
		assertEquals( "postnr", (postnr + variant), f.getPostnr() );
		assertEquals( "poststed", (poststed + variant), f.getPoststed() );
		assertEquals( "land", (land + variant), f.getLand() );
		assertEquals( "avsender_navn", (avsender_navn + variant), f.getAvsenderNavn() );
		assertEquals( "avsender_adresse1", (avsender_adresse1 + variant), f.getAvsenderAdresse1() );
		assertEquals( "avsender_adresse2", (avsender_adresse2 + variant), f.getAvsenderAdresse2() );
		assertEquals( "avsender_adresse3", (avsender_adresse3 + variant), f.getAvsenderAdresse3() );
		assertEquals( "avsender_postnr", (avsender_postnr + variant), f.getAvsenderPostnr() );
		assertEquals( "avsender_poststed", (avsender_poststed + variant), f.getAvsenderPoststed() );
		assertEquals( "appid", (appid + variant), f.getAppid() );
		assertEquals( "tittel", (tittel + variant), f.getTittel() );
		assertEquals( "melding", (melding + variant), f.getMeldingsTekst() );
		assertEquals( "email", email, f.getEmail() );
		assertEquals( "replyTo", replyTo, f.getReplyTo() );
	}

	public static void validateForsendelse( Forsendelse f, int variant ) {
		validateForsendelse( f, variant, FNR, ORGNR );
	}

	public static void validateForsendelse( Forsendelse f, int variant, String fnr, String orgnr ) {
		if (f.getFodselsnummer() != null) assertEquals( "fnr", (fnr + variant), f.getFodselsnummer() );
		if (f.getOrgnr() != 0) assertEquals( "orgnr", Integer.parseInt( orgnr + variant ), f.getOrgnr() );
		assertEquals( "navn", (navn + variant), f.getNavn() );
		assertEquals( "adresse1", (adresse1 + variant), f.getAdresse().getAdresse1() );
		assertEquals( "adresse2", (adresse2 + variant), f.getAdresse().getAdresse2() );
		assertEquals( "adresse3", (adresse3 + variant), f.getAdresse().getAdresse3() );
		assertEquals( "postnr", (postnr + variant), f.getAdresse().getPostnr() );
		assertEquals( "poststed", (poststed + variant), f.getAdresse().getPoststed() );
		assertEquals( "land", (land + variant), f.getAdresse().getLand() );
		assertEquals( "avsender_navn", (avsender_navn + variant), f.getAvsenderNavn() );
		assertEquals( "avsender_adresse1", (avsender_adresse1 + variant), f.getAvsenderadresse().getAdresse1() );
		assertEquals( "avsender_adresse2", (avsender_adresse2 + variant), f.getAvsenderadresse().getAdresse2() );
		assertEquals( "avsender_adresse3", (avsender_adresse3 + variant), f.getAvsenderadresse().getAdresse3() );
		assertEquals( "avsender_postnr", (avsender_postnr + variant), f.getAvsenderadresse().getPostnr() );
		assertEquals( "avsender_poststed", (avsender_poststed + variant), f.getAvsenderadresse().getPoststed() );
		assertEquals( "appid", (appid + variant), f.getAppid() );
		assertEquals( "tittel", (tittel + variant), f.getTittel() );
		assertEquals( "melding", (melding + variant), f.getMeldingstekst() );
		assertEquals( "epost", email, f.getEpost() );
		assertEquals( "replyTo", replyTo, f.getReplyTo() );
	}

	public static byte[] getTestDocumentContent() {
		try {
			return Files.getFileContent( testPdf );
		} catch (IOException e) {
			throw new RuntimeException( e.getMessage(), e );
		}
	}

}
