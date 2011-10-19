package no.kommune.bergen.soa.svarut;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import no.kommune.bergen.soa.common.pdf.PdfGenerator;
import no.kommune.bergen.soa.common.util.MailSender;
import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.util.Files;

/**
 * EmailFacade tilpasset Minside postkassen til Norge.no. I Minside postkassen kan man ikke sende med store vedlegg, og man kan
 * heller ikke sende med linker for nedlasting av dokumenter. Man kan sende små pdf-vedlegg. Pdf-vedlegg kan ha linker. Derfor
 * bygges meldingen opp som en pdf med link dokumentet for å omgå begrensningen i Minside postkassen.
 */
public class EmailFacadeDocumentAlert extends EmailFacade {
	public final PdfGenerator pdfGenerator;
	private String pdfTemplate = EmailFacade.U;

	public EmailFacadeDocumentAlert( TemplateEngine templateEngine, MailSender mailSender, VelocityModelFactory modelFactory, PdfGenerator pdfGenerator ) {
		super( templateEngine, mailSender, modelFactory );
		this.pdfGenerator = pdfGenerator;
	}

	/** Lager pdf-vedlegg basert på pdfTemplate og model */
	@Override
	protected File[] createAttachments( Forsendelse f, Map<String, String> model ) {
		try {
			File file = File.createTempFile( "delete-", ".pdf" );
			Files.writeToFile( file, pdfGenerator.createPdf( model, this.pdfTemplate ) );
			return new File[] { file };
		} catch (IOException e) {
			throw new RuntimeException( "Failed to create attachemet for Forsendelse:" + f.getId(), e );
		}
	}

	public String getPdfTemplate() {
		return pdfTemplate;
	}

	public void setPdfTemplate( String pdfTemplate ) {
		this.pdfTemplate = pdfTemplate;
	}

}
