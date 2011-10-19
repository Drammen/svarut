package no.kommune.bergen.soa.svarut;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;
import no.kommune.bergen.soa.common.pdf.PdfGenerator;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;
import no.kommune.bergen.soa.svarut.util.FilHenter;
import no.kommune.bergen.soa.util.Files;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PrintFacadeTest {
	PrintFacade printFacade;
	public static final String frontPageTemplate = "f1.{AVSENDER_NAVN}\n{AVSENDER_ADRESSE1}\n{AVSENDER_ADRESSE2} {AVSENDER_ADRESSE3}\n{AVSENDER_POSTNR} {AVSENDER_POSTSTED}\n \n\nf2. \n \n \n \n \n{NAVN}\n{ADRESSE1}\n{ADRESSE2} {ADRESSE3}\n{POSTNR} {POSTSTED}\n{LAND}\n\n \n\nh1.{TITTEL}\n\n \n\n{MELDING}\n\n \n\nVedlagt følger dokument til deg fra Bergen kommune. Kommunen gir deg nå muligheten til å motta dokumenter elektronisk. For at du skal kunne kommunisere elektronisk med oss og andre offentlige instanser som NAV, Lånekassen, skatteetaten så må du aktivisere tjenesten, se http://www.norge.no/minside/. Du vil da motta varsel via sms/epost om nye dokumenter og du logger deg inn via eget passord og engangskoder som du mottar på sms. Dersom du ikke leser dokumentet elektronisk vil du noen dager senere motta dokumentet i posten. Enklere kan det ikke gjøres ! &#10;&#10; &#10;&#10;Bergen kommune tilbyr stadig flere elektroniske tjenester hvor det er mulig å sende og motta dokumenter elektronisk, se  http://www.bergen.kommune.no/selvbetjening";
	public static final String pdfTemplate = "{TITTEL}\n\n{MELDING}\n \n\nb1.Bekreftelse på at dokumentet er mottatt elektronisk.\n\n \nKlikk {Link} for å åpne (åpning av dokumentet krever PDF-leser, last ned {ReaderDownload-Link}). Du bekrefter dermed at du har mottatt dokumentet. Dersom du ikke åpner dokumentet vil det bli sendt som  ordinær post. \n \n Klikk {Help-Link} dersom du ikke klarer å åpne dokumentet.";
	PrintServiceProvider mockPrintServiceProvider;

	@Before
	public void init() {
		PdfGenerator pdfGenerator = new SvarUtPdfGenerator( "target" );
		mockPrintServiceProvider = EasyMock.createNiceMock( PrintServiceProvider.class );
		this.printFacade = new PrintFacade( pdfGenerator, frontPageTemplate, mockPrintServiceProvider, VelocityModelFactoryTest.createVelocityModelFactory() );
	}

	@Test
	public void print() throws Exception {
		Forsendelse forsendelse = createForsendelse();
		String aPrintId = "123";
		EasyMock.expect( this.mockPrintServiceProvider.sendToPrint( (InputStream)EasyMock.anyObject(), EasyMock.eq( forsendelse ) ) ).andReturn( aPrintId );
		EasyMock.replay( mockPrintServiceProvider );
		PrintReceipt printReceipt = printFacade.print( forsendelse );
		Assert.assertEquals( aPrintId, printReceipt.getPrintId() );
		EasyMock.verify( mockPrintServiceProvider );
	}

	private Forsendelse createForsendelse() {
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );

		File file = FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf);
		forsendelse.setFile( file );
		forsendelse.setId( file.getName() );
		return forsendelse;
	}

	@Test
	public void importPrintStatements() {
		ForsendelsesArkiv forsendesesArkiv = ForsendelsesArkivTest.createForsendesesArkiv();
		this.mockPrintServiceProvider.importPrintStatements( EasyMock.eq( forsendesesArkiv ) );
		EasyMock.replay( mockPrintServiceProvider );
		this.printFacade.importPrintStatements( forsendesesArkiv );
		EasyMock.verify( mockPrintServiceProvider );
	}

	@Test
	public void printToFile() throws IOException {
		final File fileOut = File.createTempFile( "qwe_", ".pdf" );
		printToFile( fileOut );
		assertTrue( fileOut.exists() );
	}

	@Test
	@Ignore
	public void printToView() throws IOException {
		final File fileOut = File.createTempFile( "view_", ".pdf" );
		printToFile( fileOut );
		view( fileOut );
	}

	@Test
	@Ignore
	public void printToDefaultPrinter() throws IOException {
		final File fileOut = File.createTempFile( "lpr_", ".pdf" );
		printToFile( fileOut );
		lpr( fileOut );
	}

	private void printToFile( final File fileOut ) throws IOException {
		SvarUtPdfGenerator pdfGenerator = new SvarUtPdfGenerator( "target" );
		PrintServiceProvider printServiceProvider = newPrintServiceProvider( fileOut );
		VelocityModelFactory velocityModelFactory = VelocityModelFactoryTest.createVelocityModelFactory();
		PrintFacade printFacade = new PrintFacade( pdfGenerator, pdfTemplate, printServiceProvider, velocityModelFactory );
		Forsendelse forsendelse = createForsendelse();
		printFacade.print( forsendelse );
	}

	private PrintServiceProvider newPrintServiceProvider( final File fileOut ) {
		PrintServiceProvider printServiceProvider = new PrintServiceProvider() {
			@Override
			public String sendToPrint( InputStream inputStream, Forsendelse forsendelse ) {
				try {
					Files.writeToFile( fileOut, inputStream );
				} catch (IOException e) {
					throw new RuntimeException( "Could not write to file" + fileOut );
				}
				return fileOut.getName();
			}

			@Override
			public void importPrintStatements( ForsendelsesArkiv forsendelsesArkiv ) {}
		};
		return printServiceProvider;
	}

	public static void view( File pdf ) {
		try {
			Runtime.getRuntime().exec( "evince " + pdf.getPath() );
		} catch (IOException e) {
			throw new RuntimeException( "Unable to launch: evince " + pdf.getPath() );
		}
	}

	public static void lpr( File pdf ) {
		try {
			Runtime.getRuntime().exec( "lpr " + pdf.getPath() );
		} catch (IOException e) {
			throw new RuntimeException( "Unable to launch: lpr " + pdf.getPath() );
		}
	}

}
