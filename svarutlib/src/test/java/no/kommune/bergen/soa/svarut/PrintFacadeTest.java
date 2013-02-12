package no.kommune.bergen.soa.svarut;

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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class PrintFacadeTest {

	public static final String FRONT_PAGE_TEMPLATE_PROD = "f1.{AVSENDER_NAVN}\n{AVSENDER_ADRESSE1}\n{AVSENDER_ADRESSE2} {AVSENDER_ADRESSE3}\n{AVSENDER_POSTNR} {AVSENDER_POSTSTED}\n \n\nf2. \n \n \n \n \n{NAVN}\n{ADRESSE1}\n{ADRESSE2} {ADRESSE3}\n{POSTNR} {POSTSTED}\n{LAND}\n\n \n\n \n\n \n\n \n\n \n\nh1.{TITTEL}\n\n \n\n{MELDING}\n\n \n\n" +
			"Vedlagt følger dokument fra Bergen kommune. For privatpersoner kan dokumentet leses elektronisk i «Min meldingsboks» på https://www.altinn.no. Gjelder dokumentet en byggesøknad for en eiendom som du eier (er hjemmelshaver til), så vil dokumentet - og andre dokumenter i saken - være tilgjengelig på https://www.bergen.kommune.no/dinside/privat. Dokumenter fra andre saksområder vil bli tilgjengelige etter hvert. «Din side/privat» inneholder ellers oversikt over en del andre forhold mellom privatpersoner og Bergen kommune.\n\n \n\n" +
			"Det arbeides med å legge til rette for tilsvarende tjenester for foretak/organisasjoner.\n\n \n\n" +
			"Bergen kommune utvikler stadig flere elektroniske tjenester som du finner på https://www.bergen.kommune.no/. Benytter du tjenestene sparer du tid og vi kommer raskere i gang med å behandle henvendelsen din. Du bidrar samtidig til mindre miljøbelastning og reduserte kostnader.\n\n \n\n \n\n \n\n" +
			"h1.Digitalt førstevalg - din vei til raskere, enklere og sikrere tjenester fra Bergen kommune";

	private PrintFacade printFacade;
	private PrintServiceProvider mockPrintServiceProvider;

	@Before
	public void init() {
		PdfGenerator pdfGenerator = new SvarUtPdfGenerator("target");
		mockPrintServiceProvider = EasyMock.createNiceMock(PrintServiceProvider.class);
		printFacade = new PrintFacade(pdfGenerator, FRONT_PAGE_TEMPLATE_PROD, mockPrintServiceProvider, VelocityModelFactoryTest.createVelocityModelFactory());
	}

	@Test
	public void print() throws Exception {
		Forsendelse forsendelse = createForsendelse();
		String aPrintId = "123";
		EasyMock.expect(mockPrintServiceProvider.sendToPrint((InputStream) EasyMock.anyObject(), EasyMock.eq(forsendelse))).andReturn(aPrintId);
		EasyMock.replay(mockPrintServiceProvider);
		PrintReceipt printReceipt = printFacade.print(forsendelse);
		Assert.assertEquals(aPrintId, printReceipt.getPrintId());
		EasyMock.verify(mockPrintServiceProvider);
	}

	private Forsendelse createForsendelse() {
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(1);
		File file = FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf);
		forsendelse.setFile(file);
		forsendelse.setId(file.getName());
		return forsendelse;
	}

	@Test
	public void importPrintStatements() {
		ForsendelsesArkiv forsendelsesArkiv = ForsendelsesArkivTest.createForsendesesArkiv();
		mockPrintServiceProvider.importPrintStatements(EasyMock.eq(forsendelsesArkiv));
		EasyMock.replay(mockPrintServiceProvider);
		printFacade.importPrintStatements(forsendelsesArkiv);
		EasyMock.verify(mockPrintServiceProvider);
	}

	@Test
	public void printToFile() throws IOException {
		final File fileOut = File.createTempFile("qwe_", ".pdf");
		printToFile(fileOut);
		assertTrue(fileOut.exists());
	}

	@Test
	@Ignore
	public void printToView() throws IOException {
		final File fileOut = File.createTempFile("view_", ".pdf");
		printToFile(fileOut);
		view(fileOut);
	}

	@Test
	@Ignore
	public void printToDefaultPrinter() throws IOException {
		final File fileOut = File.createTempFile("lpr_", ".pdf");
		printToFile(fileOut);
		lpr(fileOut);
	}

	private void printToFile(final File fileOut) throws IOException {
		SvarUtPdfGenerator pdfGenerator = new SvarUtPdfGenerator("target");
		PrintServiceProvider printServiceProvider = newPrintServiceProvider(fileOut);
		VelocityModelFactory velocityModelFactory = VelocityModelFactoryTest.createVelocityModelFactory();
		PrintFacade printFacade = new PrintFacade(pdfGenerator, FRONT_PAGE_TEMPLATE_PROD, printServiceProvider, velocityModelFactory);
		Forsendelse forsendelse = createForsendelse();
		printFacade.print(forsendelse);
	}

	private PrintServiceProvider newPrintServiceProvider(final File fileOut) {
		return new PrintServiceProvider() {
			@Override
			public String sendToPrint(InputStream inputStream, Forsendelse forsendelse) {
				try {
					Files.writeToFile(fileOut, inputStream);
				} catch (IOException e) {
					throw new RuntimeException("Could not write to file" + fileOut);
				}
				return fileOut.getName();
			}

			@Override
			public void importPrintStatements(ForsendelsesArkiv forsendelsesArkiv) {
			}
		};
	}

	public static void view(File pdf) {
		try {
			Runtime.getRuntime().exec("evince " + pdf.getPath());
		} catch (IOException e) {
			throw new RuntimeException("Unable to launch: evince " + pdf.getPath());
		}
	}

	public static void lpr(File pdf) {
		try {
			Runtime.getRuntime().exec("lpr " + pdf.getPath());
		} catch (IOException e) {
			throw new RuntimeException("Unable to launch: lpr " + pdf.getPath());
		}
	}
}
