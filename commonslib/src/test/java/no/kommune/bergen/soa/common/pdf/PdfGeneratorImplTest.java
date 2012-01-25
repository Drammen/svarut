package no.kommune.bergen.soa.common.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import no.kommune.bergen.soa.util.Files;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfReader;

public class PdfGeneratorImplTest {

	private static final String TEMPDIR = "target/output";

	private Map<String, String> model;
	private PdfGenerator testObj;


	@BeforeClass
	public static void setUpClass() throws Exception {
		rmdir(TEMPDIR);
		Document.compress = false;
	}

	@Before
	public void setUp() throws Exception {
		model = new HashMap<String, String>();
		testObj = new PdfGeneratorImpl(TEMPDIR);
		model.put( "link", "http://google.com" );
		model.put( "link-text", "Google!" );
	}

	@Test
	@Ignore
	public void visual() throws Exception {
		final String frontPageTemplate = "f1.{AVSENDER_NAVN}\n{AVSENDER_ADRESSE1}\n{AVSENDER_ADRESSE2} {AVSENDER_ADRESSE3}\n{AVSENDER_POSTNR} {AVSENDER_POSTSTED}\n \n\nf2. \n \n \n \n{NAVN}\n{ADRESSE1}\n{ADRESSE2} {ADRESSE3}\n{POSTNR} {POSTSTED}\n{LAND}\n\n \n\n \n\n \n\n \n\n \n\nh1.{TITTEL}\n\n \n\n{MELDING}\n\n \n\nVedlagt følger dokument til deg fra Bergen kommune. Kommunen gir deg nå muligheten til å motta dokumenter elektronisk. For at du skal kunne kommunisere elektronisk med oss og andre offentlige instanser som NAV, Lånekassen, skatteetaten så må du aktivisere tjenesten, se http://www.norge.no/minside/. Du vil da motta varsel via sms/epost om nye dokumenter og du logger deg inn via eget passord og engangskoder som du mottar på sms. Dersom du ikke leser dokumentet elektronisk vil du noen dager senere motta dokumentet i posten. Enklere kan det ikke gjøres ! \n\n \n\nBergen kommune tilbyr stadig flere elektroniske tjenester hvor det er mulig å sende og motta dokumenter elektronisk, se  http://www.bergen.kommune.no/selvbetjening \n\nSome text {link} more text";
		model.put( "AVSENDER_NAVN", "Einar Valen" );
		model.put( "AVSENDER_ADRESSE1", "Kvamsvegen 17" );
		model.put( "AVSENDER_ADRESSE2", "" );
		model.put( "AVSENDER_ADRESSE3", "" );
		model.put( "AVSENDER_POSTNR", "5265" );
		model.put( "AVSENDER_POSTSTED", "Ytre Arna" );
		model.put( "NAVN", "Einar Valen" );
		model.put( "ADRESSE1", "Kvamsvegen 17" );
		model.put( "ADRESSE2", "" );
		model.put( "ADRESSE3", "" );
		model.put( "POSTNR", "5265" );
		model.put( "POSTSTED", "Ytre Arna" );
		model.put( "LAND", "Norge" );
		model.put( "TITTEL", "Dette er dokumentets tittel" );
		model.put( "MELDING;", "Dette er meldingen." );
		String velocityMacroA = "hello, worldA!";
		File result = File.createTempFile( "formated-", ".pdf", new File(TEMPDIR) );
		Files.writeToFile( result, testObj.createPdf( model, frontPageTemplate ) );
		view( result );
	}

	@Test
	public void formatA4() throws Exception {
		File result = File.createTempFile( "formated-", ".pdf", new File(TEMPDIR) );
		FileOutputStream newPdfOutputStream = new FileOutputStream( result );
		InputStream folgebrev =  Thread.currentThread().getContextClassLoader().getResourceAsStream("test.pdf" );
		InputStream dokument = Thread.currentThread().getContextClassLoader().getResourceAsStream( "1000_210000_000000002187.pdf" );

		testObj.formatA4( newPdfOutputStream, new InputStream[] { folgebrev, dokument } );
		assertTrue( result.exists() );
		assertTrue( result.length() > 0 );
		assertTrue( testObj.isValidPdf( result ) );
		PdfReader readerResult = new PdfReader( new FileInputStream( result ) );
		assertEquals( "antallSider folgebrev", 1, new PdfReader( Thread.currentThread().getContextClassLoader().getResourceAsStream("test.pdf" ) ).getNumberOfPages() );
		assertEquals( "antallSider dokument", 14, new PdfReader( Thread.currentThread().getContextClassLoader().getResourceAsStream("1000_210000_000000002187.pdf" ) ).getNumberOfPages() );
		assertEquals( "antall sider resultat", 16, readerResult.getNumberOfPages() );
		//view( result );
	}

	@Test
	public void createSimple() throws Exception {
		String velocityMacro = "hello, world!";
		File result = new File(TEMPDIR, "createSimple.pdf" );
		Files.writeToFile( result, testObj.createPdf( new HashMap<String, String>(), velocityMacro ) );

		assertTrue( result.getPath().contains( "target" + File.separator + "output" ) );
		assertTrue( contains( velocityMacro, result ) );
	}

	@Test
	public void concatAddBlankPageWhenDocumentHasOddNumberOfPages() throws Exception {
		File result = File.createTempFile( "concatinated-", ".pdf", new File(TEMPDIR) );
		FileOutputStream newPdfOutputStream = new FileOutputStream( result );
		InputStream docA = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.pdf" );
		InputStream docB = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.pdf" );
		InputStream docC = Thread.currentThread().getContextClassLoader().getResourceAsStream("1000_210000_000000002187.pdf" );
		InputStream docD = Thread.currentThread().getContextClassLoader().getResourceAsStream("000000000061.pdf" );
		testObj.concat( newPdfOutputStream, new InputStream[] { docA, docB, docC, docD } );
		assertTrue( result.exists() );
		assertTrue( result.length() > 0 );
		assertTrue( testObj.isValidPdf( result ) );
		//view( result );
		PdfReader readerResult = new PdfReader( new FileInputStream( result ) );
		assertEquals( "antallSider docA", 1, new PdfReader( Thread.currentThread().getContextClassLoader().getResourceAsStream("test.pdf" ) ).getNumberOfPages() );
		assertEquals( "antallSider docB", 1, new PdfReader( Thread.currentThread().getContextClassLoader().getResourceAsStream("test.pdf" ) ).getNumberOfPages() );
		assertEquals( "antallSider docC", 14, new PdfReader( Thread.currentThread().getContextClassLoader().getResourceAsStream("1000_210000_000000002187.pdf" ) ).getNumberOfPages() );
		assertEquals( "antallSider docD", 4, new PdfReader( Thread.currentThread().getContextClassLoader().getResourceAsStream("000000000061.pdf" ) ).getNumberOfPages() );
		assertEquals( "antall sider resultat", 22, readerResult.getNumberOfPages() );
	}

	@Test
	public void testNormalPortrait() throws Exception {
		testRotation("normal_portrait.pdf");
	}

	@Test
	public void testNormalLandscape() throws Exception {
		testRotation("normal_landscape.pdf");
	}

	@Test
	public void test270rotation() throws Exception {
		testRotation("270rotation.pdf");
	}

	@Test
	public void testLandscape_no_margin() throws Exception {
		testRotation("green_landscape_no_margin.pdf");
	}

	@Test
	public void testLandscape_with_margin() throws Exception {
		testRotation("grey_landscape_with_margin.pdf");
	}

	@Test
	public void testPortrait_with_margin() throws Exception {
		testRotation("grey_portrait_with_margin.pdf");
	}

	private void testRotation(String filename) throws Exception {
		File result = File.createTempFile("concatinated-", ".pdf", new File(TEMPDIR));
		FileOutputStream newPdfOutputStream = new FileOutputStream(result);
		InputStream ikkesnu = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		testObj.formatA4(newPdfOutputStream, new InputStream[]{ikkesnu});
		assertTrue(result.exists());
		assertTrue(result.length() > 0);
		assertTrue(testObj.isValidPdf(result));

		PdfReader reader = new PdfReader(new FileInputStream(result));
		int numberOfPages = reader.getNumberOfPages();
		for (int i = 1; i <= numberOfPages; i++) {
			Rectangle size = reader.getPageSize(i);
			assertEquals(842, size.getHeight(), 0.1);
			assertEquals(595, size.getWidth(), 0.1);
			assertEquals(0, size.getRotation());
		}
	}



	@Test
	public void concat() throws Exception {
		String velocityMacroA = "hello, worldA!";
		String velocityMacroB = "hello, worldB!";
		File pdfA = new File(TEMPDIR, "pdfA.pdf" );
		Files.writeToFile( pdfA, testObj.createPdf( model, velocityMacroA ) );
		File pdfB = new File(TEMPDIR, "pdfB.pdf" );
		Files.writeToFile( pdfB, testObj.createPdf( model, velocityMacroB ) );
		File result = new File(TEMPDIR, "concat.pdf" );
		Files.writeToFile( result, testObj.concat( pdfA, pdfB ) );
		assertTrue( result.getPath().contains( "target" + File.separator + "output" ) );
		assertTrue( contains( velocityMacroA, result ) );
		assertTrue( contains( velocityMacroB, result ) );
		//view( result );
	}

	@Test
	public void isValidPdf() throws Exception {
		String velocityMacro = "hello, world!";
		File pdf = new File(TEMPDIR, "isValidPdf.pdf" );
		Files.writeToFile( pdf, testObj.createPdf( model, velocityMacro ) );
		assertTrue( testObj.isValidPdf( pdf ) );
		byte[] fileContent = Files.getFileContent( pdf );
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( fileContent );
		assertTrue( testObj.isValidPdf( byteArrayInputStream ) );
		ByteArrayInputStream byteArrayInputStreamErr = new ByteArrayInputStream( "qwe".getBytes() );
		assertFalse( testObj.isValidPdf( byteArrayInputStreamErr ) );
	}

	@Test
	public void concatStreams() throws Exception {
		final int numberOfPdfsToCOncatinate = 5;
		String[] velocityMacros = new String[numberOfPdfsToCOncatinate];
		InputStream[] inputStreams = new InputStream[numberOfPdfsToCOncatinate];
		for (int i = 0; i < numberOfPdfsToCOncatinate; i++) {
			velocityMacros[i] = "hello, world" + i;
			inputStreams[i] = testObj.createPdf( model, velocityMacros[i] );
		}
		File result = File.createTempFile( "test-", ".pdf", new File(TEMPDIR) );
		testObj.concat( new FileOutputStream( result ), inputStreams );
		for (int i = 0; i < numberOfPdfsToCOncatinate; i++) {
			assertTrue( contains( velocityMacros[i], result ) );
		}
	}

	@Test
	public void link() throws Exception {
		String velocityMacro = "{link}";
		File result = new File(TEMPDIR, "link.pdf" );
		Files.writeToFile( result, testObj.createPdf( model, velocityMacro ) );

		assertTrue( contains( model.get( "link" ), result ) );
		assertTrue( contains( model.get( "link-text" ), result ) );
	}

	@Test
	public void textAndLink() throws Exception {
		String velocityMacro = "some text {link} more text";
		File result = new File(TEMPDIR, "textAndLink.pdf" );
		Files.writeToFile( result, testObj.createPdf( model, velocityMacro ) );

		assertTrue( contains( model.get( "link" ), result ) );
		assertTrue( contains( model.get( "link-text" ), result ) );
		assertTrue( contains( "some text", result ) );
		assertTrue( contains( "more text", result ) );
		assertFalse( contains( "{", result ) );
	}

	@Test
	public void severalVariables() throws Exception {
		String velocityMacro = "{link} {foo} {bar}";
		model.put( "foo", "FOO" );
		model.put( "bar", "BAR" );
		File result = new File(TEMPDIR, "severalVariables.pdf" );
		Files.writeToFile( result, testObj.createPdf( model, velocityMacro ) );
		assertTrue( contains( model.get( "link" ), result ) );
		assertTrue( contains( model.get( "link-text" ), result ) );
		assertTrue( contains( "FOO", result ) );
		assertTrue( contains( "BAR", result ) );
		assertFalse( contains( "{", result ) );
	}

	@Test
	public void twoParagraphs() throws Exception {
		String velocityMacro = "foo\n\nh1.bar";
		File result = new File(TEMPDIR, "twoParagraphs.pdf" );
		Files.writeToFile( result, testObj.createPdf( model, velocityMacro ) );
		assertTrue( contains( "foo", result ) );
		assertTrue( contains( "bar", result ) );
		assertFalse( "Still contains variable formating", contains( "{", result ) );
		assertFalse( "Still contains header formating", contains( "h1.", result ) );
		//view( result );
	}

	private boolean contains( String needle, File haystack ) throws Exception {
		BufferedReader fileReader = new BufferedReader( new FileReader( haystack ) );
		String line;
		while (fileReader.ready()) {
			line = fileReader.readLine();
			if (line.contains( needle )) return true;
		}
		return false;
	}

	private static void rmdir( String directory ) {
		File dir = new File( directory );
		if (dir.exists()) {
			for (File f : dir.listFiles()) {
				f.delete();
			}
		}
		dir.delete();
	}

	public static void view( File pdf ) {
		try {
			Runtime.getRuntime().exec( "evince " + pdf.getPath() );
		} catch (IOException e) {
			throw new RuntimeException( "Unamble to launch: evince " + pdf.getPath() );
		}
	}

}
