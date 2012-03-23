package no.kommune.bergen.soa.svarut;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import no.kommune.bergen.soa.common.pdf.PdfGenerator;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;

import org.apache.log4j.Logger;

/** PrintServiceProvider facade - for regular snail mail distributions */
public class PrintFacade {
	public final PdfGenerator pdfGenerator;
	public final String frontPageTemplate;
	private PrintServiceProvider printserviceProvider;
	final Logger logger = Logger.getLogger( PrintFacade.class );
	public final VelocityModelFactory modelFactory;

	public PrintFacade( PdfGenerator pdfGenerator, String frontPageTemplate, PrintServiceProvider printserviceProvider, VelocityModelFactory modelFactory ) {
		this.pdfGenerator = pdfGenerator;
		this.frontPageTemplate = frontPageTemplate;
		this.printserviceProvider = printserviceProvider;
		this.modelFactory = modelFactory;
	}

	public PrintServiceProvider getPrintserviceProvider() {
		return printserviceProvider;
	}

	public void setPrintserviceProvider(PrintServiceProvider printserviceProvider) {
		this.printserviceProvider = printserviceProvider;
	}

	/** Oversender fosendelse og dokument med forside til PrintServiceProvider */
	public PrintReceipt print( Forsendelse f ) {
		File forsendelseDokument = f.getFile();
		Map<String, String> model = modelFactory.createModel( f );
		InputStream frontPagePdf = pdfGenerator.createPdf( model, this.frontPageTemplate );
		try {
			File collatedPdf = pdfGenerator.createTempFile();
			int pageCount = pdfGenerator.formatA4( new FileOutputStream( collatedPdf ), new InputStream[] { frontPagePdf, new FileInputStream( forsendelseDokument ) } );
			String printId = this.printserviceProvider.sendToPrint( new FileInputStream( collatedPdf ), f );
			PrintReceipt printReceipt = new PrintReceipt();
			printReceipt.setPageCount( pageCount );
			printReceipt.setPrintId( printId );
			return printReceipt;
		} catch (IOException e) {
			throw new RuntimeException( String.format( "Could not send file to print for ForsendelsesId=%s", f.getId() ), e );
		}
	}

	/** Henter status for nye post-forsendeser fra PrintServiceProvider */
	public void importPrintStatements( ForsendelsesArkiv forsendelsesArkiv ) {
		this.printserviceProvider.importPrintStatements( forsendelsesArkiv );
	}
}
